package moe.tlaster.kfilepicker

import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.files.File
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
        get() = file.name
    actual val size: Long
        get() = readAllBytes().size.toLong()

    actual fun readAllBytes(): ByteArray {
        return FileReaderSync().readAsArrayBuffer(file).toByteArray()
    }

    actual fun writeAllBytes(bytes: ByteArray) {
    }
}

private fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()
