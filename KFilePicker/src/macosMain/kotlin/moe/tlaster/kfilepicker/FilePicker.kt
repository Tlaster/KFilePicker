package moe.tlaster.kfilepicker

import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import platform.AppKit.*
import platform.Foundation.*
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped

actual object FilePicker {
    actual suspend fun pickFiles(
        allowedExtensions: List<String>,
        allowMultiple: Boolean,
    ): List<PlatformFile> {
        val dialog = NSOpenPanel()
        dialog.setAllowsMultipleSelection(allowMultiple)
        dialog.setAllowedFileTypes(allowedExtensions)
        return if (dialog.runModal() == NSModalResponseOK) {
            dialog.URLs.mapNotNull {
                if (it is NSURL) {
                    PlatformFile(it)
                } else {
                    null
                }
            }
        } else {
            listOf()
        }
    }

    actual suspend fun createFile(name: String): PlatformFile? {
        val dialog = NSSavePanel()
        dialog.setNameFieldStringValue(name)
        return if (dialog.runModal() == NSModalResponseOK) {
            dialog.URL?.let { PlatformFile(it) }
        } else {
            null
        }
    }
}

actual class PlatformFile(
    private val url: NSURL,
) {
    actual val path: String
        get() = url.path.orEmpty()
    actual val name: String
        get() = url.lastPathComponent.orEmpty()
    actual val size: Long
        get() = NSData.create(url)?.length?.toLong() ?: 0L

    actual suspend fun readAllBytesAsync(): ByteArray {
        val data = NSData.create(url) ?: return ByteArray(0)
        val bytes = data.bytes ?: return ByteArray(0)
        return bytes.readBytes(data.length.toInt())
    }

    actual suspend fun writeAllBytesAsync(bytes: ByteArray) {
        val data = memScoped {
            NSData.create(bytes = allocArrayOf(bytes), length = bytes.size.toULong())
        }
        val path = url.path ?: return
        data.writeToFile(path, atomically = true)
    }
}