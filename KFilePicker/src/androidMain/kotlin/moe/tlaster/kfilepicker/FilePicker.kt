package moe.tlaster.kfilepicker

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

actual object FilePicker {
    private lateinit var multipleFilePickerLauncher: MultipleFilePickerLauncher
    private lateinit var singleFilePickerLauncher: SingleFilePickerLauncher
    private lateinit var contentResolver: ContentResolver

    fun init(registry: ActivityResultRegistry, owner: LifecycleOwner, contentResolver: ContentResolver) {
        multipleFilePickerLauncher = MultipleFilePickerLauncher(registry, owner)
        singleFilePickerLauncher = SingleFilePickerLauncher(registry, owner)
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
                path = it.toString(),
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

actual class PlatformFile(
    actual val path: String,
    private val contentResolver: ContentResolver,
) {
    actual val size: Long
        get() = contentResolver.openFileDescriptor(Uri.parse(path), "r")?.statSize ?: 0L

    actual fun readAllBytes(): ByteArray {
        return contentResolver.openInputStream(Uri.parse(path))?.readBytes() ?: emptyArray<Byte>().toByteArray()
    }

    actual fun writeAllBytes(bytes: ByteArray) {
        contentResolver.openOutputStream(Uri.parse(path))?.write(bytes)
    }
}