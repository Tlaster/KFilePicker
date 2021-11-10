package moe.tlaster.kfilepicker

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.coroutines.suspendCoroutine

actual object FilePicker {
    private lateinit var multipleFilePickerLauncher: MultipleFilePickerLauncher
    private lateinit var singleFilePickerLauncher: SingleFilePickerLauncher
    private lateinit var createFilePickerLauncher: CreateFilePickerLauncher
    private lateinit var contentResolver: ContentResolver

    fun init(registry: ActivityResultRegistry, owner: LifecycleOwner, contentResolver: ContentResolver) {
        multipleFilePickerLauncher = MultipleFilePickerLauncher(registry, owner)
        singleFilePickerLauncher = SingleFilePickerLauncher(registry, owner)
        createFilePickerLauncher = CreateFilePickerLauncher(registry, owner)
        this.contentResolver = contentResolver
    }

    actual suspend fun pickFiles(allowedExtensions: List<String>, allowMultiple: Boolean): List<PlatformFile> {
        val mime = getMimeTypes(allowedExtensions).joinToString("|")
        return if (allowMultiple) {
            multipleFilePickerLauncher.launch(mime)
        } else {
            listOfNotNull(singleFilePickerLauncher.launch(mime))
        }.map {
            PlatformFile(
                uri = it,
                contentResolver = contentResolver,
            )
        }
    }

    private fun getMimeTypes(allowedExtensions: List<String>): List<String> {
        if (allowedExtensions.isEmpty()) {
            return listOf("*/*")
        }
        val mimes = ArrayList<String>()
        for (element in allowedExtensions) {
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(element) ?: continue
            mimes.add(mime)
        }
        return mimes
    }

    actual suspend fun createFile(name: String): PlatformFile? {
        return createFilePickerLauncher.launch(name)?.let {
            PlatformFile(it, contentResolver)
        }
    }
}


class MultipleFilePickerLauncher(
    registry: ActivityResultRegistry,
    owner: LifecycleOwner,
) {
    private val channel = Channel<List<Uri>>()
    private val picker = registry.register(
        UUID.randomUUID().toString(),
        owner,
        ActivityResultContracts.GetMultipleContents()
    ) {
        GlobalScope.launch {
            channel.send(it)
        }
    }

    suspend fun launch(type: String): List<Uri> {
        picker.launch(type)
        return channel.receive()
    }
}

class SingleFilePickerLauncher(
    registry: ActivityResultRegistry,
    owner: LifecycleOwner,
) {
    private val channel = Channel<Uri?>()
    private val picker = registry.register(
        UUID.randomUUID().toString(),
        owner,
        ActivityResultContracts.GetContent()
    ) {
        GlobalScope.launch {
            channel.send(it)
        }
    }

    suspend fun launch(type: String): Uri? {
        picker.launch(type)
        return channel.receive()
    }
}

class CreateFilePickerLauncher(
    registry: ActivityResultRegistry,
    owner: LifecycleOwner,
) {
    private val channel = Channel<Uri?>()
    private val picker = registry.register(
        UUID.randomUUID().toString(),
        owner,
        ActivityResultContracts.CreateDocument()
    ) {
        GlobalScope.launch {
            channel.send(it)
        }
    }

    suspend fun launch(name: String): Uri? {
        picker.launch(name)
        return channel.receive()
    }
}

actual class PlatformFile(
    val uri: Uri,
    private val contentResolver: ContentResolver,
) {
    actual val size: Long
        get() = contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0L

    actual suspend fun readAllBytesAsync(): ByteArray {
        return contentResolver.openInputStream(uri)?.readBytes() ?: emptyArray<Byte>().toByteArray()
    }

    actual suspend fun writeAllBytesAsync(bytes: ByteArray) {
        contentResolver.openOutputStream(uri)?.write(bytes)
    }

    actual val name: String
        get() = uri.lastPathSegment ?: ""

    actual val path: String
        get() = uri.toString()
}