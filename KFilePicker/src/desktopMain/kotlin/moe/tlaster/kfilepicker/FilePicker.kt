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


//class JSystemFileChooser : JFileChooser() {
//    override fun updateUI() {
//        var old = UIManager.getLookAndFeel()
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
//        } catch (ex: Throwable) {
//            old = null
//        }
//        super.updateUI()
//        if (old != null) {
//            val filePane = findFilePane(this)
//            filePane!!.viewType = FilePane.VIEWTYPE_DETAILS
//            filePane.viewType = FilePane.VIEWTYPE_LIST
//            val background = UIManager.getColor("Label.background")
//            setBackground(background)
//            isOpaque = true
//            try {
//                UIManager.setLookAndFeel(old)
//            } catch (ignored: UnsupportedLookAndFeelException) {
//            } // shouldn't get here
//        }
//    }
//
//    companion object {
//        private fun findFilePane(parent: Container): FilePane? {
//            for (comp in parent.components) {
//                if (comp is FilePane) {
//                    return comp
//                }
//                if (comp is Container) {
//                    val cont: Container = comp as Container
//                    if (cont.componentCount > 0) {
//                        val found = findFilePane(cont)
//                        if (found != null) {
//                            return found
//                        }
//                    }
//                }
//            }
//            return null
//        }
//    }
//}
