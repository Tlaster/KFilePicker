package moe.tlaster.kfilepicker

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual object FilePicker {
    private lateinit var frame: Frame

    fun init(frame: Frame) {
        this.frame = frame
    }

    actual suspend fun pickFiles(
        allowedExtensions: List<String>,
        allowMultiple: Boolean,
    ): List<PlatformFile> {
        if (System.getProperty("os.name").contains("nux")) {
            val chooser = JFileChooser().apply {
                if (allowedExtensions.any()) {
                    fileFilter = FileNameExtensionFilter("", *allowedExtensions.toTypedArray())
                }
                dialogType = JFileChooser.OPEN_DIALOG
                isMultiSelectionEnabled = allowMultiple
            }
            return if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                (chooser.selectedFiles + chooser.selectedFile).map {
                    PlatformFile(it)
                }
            } else {
                emptyList()
            }
        } else {
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

    actual suspend fun createFile(name: String): PlatformFile? {
        if (System.getProperty("os.name").contains("nux")) {
            val chooser = JFileChooser().apply {
                dialogType = JFileChooser.SAVE_DIALOG
            }
            return if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                PlatformFile(chooser.selectedFile)
            } else {
                null
            }
        } else {
            val dialog = FileDialog(frame).apply {
                mode = FileDialog.SAVE
            }
            dialog.isVisible = true
            return dialog.files.map {
                PlatformFile(
                    file = it
                )
            }.firstOrNull()
        }
    }
}


actual class PlatformFile(
    private val file: File,
) {
    actual val size: Long
        get() = file.length()

    actual suspend fun readAllBytesAsync(): ByteArray {
        return file.readBytes()
    }

    actual suspend fun writeAllBytesAsync(bytes: ByteArray) {
        file.writeBytes(bytes)
    }

    actual val path: String
        get() = file.absolutePath

    actual val name: String
        get() = file.name
}
