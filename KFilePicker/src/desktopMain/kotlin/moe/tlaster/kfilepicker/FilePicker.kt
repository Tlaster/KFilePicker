package moe.tlaster.kfilepicker

import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

actual object FilePicker {
    private lateinit var frame: Frame

    fun init(frame: Frame) {
        this.frame = frame
    }

    actual suspend fun pickFiles(
        allowedExtensions: List<String>,
        allowMultiple: Boolean,
    ): List<PlatformFile> {
        val dialog = FileDialog(frame).apply {
            mode = FileDialog.LOAD
            this.isMultipleMode = allowMultiple
            this.setFilenameFilter { _, name ->
                if (allowedExtensions.isEmpty()) {
                    true
                } else {
                    allowedExtensions.any { name.endsWith(it) }
                }
            }
            this.file = allowedExtensions.joinToString(";")
        }
        dialog.isVisible = true
        return dialog.files.map {
            PlatformFile(
                file = it
            )
        }
    }
}


actual class PlatformFile(
    private val file: File,
) {
    actual val size: Long
        get() = file.length()

    actual fun readAllBytes(): ByteArray {
        return file.readBytes()
    }

    actual fun writeAllBytes(bytes: ByteArray) {
        file.writeBytes(bytes)
    }

    actual val path: String
        get() = file.absolutePath
}

