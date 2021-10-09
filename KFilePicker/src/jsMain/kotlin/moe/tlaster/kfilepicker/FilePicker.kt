package moe.tlaster.kfilepicker

import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.dom.url.URL
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.FileReaderSync
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual object FilePicker {
    actual suspend fun pickFiles(
        allowedExtensions: List<String>,
        allowMultiple: Boolean,
    ): List<PlatformFile> = suspendCoroutine { continuation ->
        document.createElement("input").let {
            it as HTMLInputElement
        }.apply {
            type = "file"
            multiple = allowMultiple
            accept = allowedExtensions.joinToString(",")
            onchange = {
                val result = files?.asList()?.map {
                    PlatformFile(it)
                } ?: emptyList()
                continuation.resume(result)
            }
            click()
        }
    }
}

actual class PlatformFile(
    private val file: File,
) {
    actual val path: String
        get() = URL.createObjectURL(file)
    actual val size: Long
        get() = file.size.toLong()

    actual suspend fun readAllBytesAsync(): ByteArray = suspendCoroutine { continuation ->
        val reader = FileReader().apply {
            onload = {
                continuation.resume(result.unsafeCast<ArrayBuffer>().toByteArray())
            }
        }
        reader.readAsArrayBuffer(file)
    }

    actual suspend fun writeAllBytesAsync(bytes: ByteArray) {
    }

    actual val name: String
        get() = file.name
}

private fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()
