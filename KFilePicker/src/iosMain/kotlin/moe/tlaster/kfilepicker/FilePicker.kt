package moe.tlaster.kfilepicker

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.cinterop.usePinned
import platform.Foundation.NSURL
import platform.UIKit.*
import platform.UniformTypeIdentifiers.UTType
import platform.darwin.NSObject

internal class PickerDelegate(
    private val canceled: () -> Unit,
    private val picked: (url: List<NSURL>) -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {
    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        canceled.invoke()
    }

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentAtURL: NSURL) {
        picked.invoke(listOf(didPickDocumentAtURL))
    }

    override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
        picked.invoke(didPickDocumentsAtURLs.map { it as NSURL })
    }
}

internal class PresentationControllerDelegate(
    private val onDismiss: () -> Unit,
) : NSObject(), UIPopoverPresentationControllerDelegateProtocol {
    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        onDismiss.invoke()
    }
}

actual object FilePicker {
    private lateinit var parentViewController: UIViewController

    fun init(
        parentViewController: UIViewController,
    ) {
        this.parentViewController = parentViewController
    }

    actual suspend fun pickFiles(
        allowedExtensions: List<String>,
        allowMultiple: Boolean,
    ): List<PlatformFile> = suspendCoroutine { coroutine ->
        val controller = UIDocumentPickerViewController(
            forOpeningContentTypes = allowedExtensions.map { UTType.typeWithFilenameExtension(it) },
            asCopy = true
        )
        controller.setAllowsMultipleSelection(allowMultiple)
        controller.setDelegate(PickerDelegate(
            canceled = { coroutine.resume(emptyList()) },
            picked = { coroutine.resume(it.map { PlatformFile(it) }) }
        ))
        controller.presentationController?.setDelegate(PresentationControllerDelegate {
            coroutine.resume(emptyList())
        })
        controller.presentViewController(
            parentViewController,
            true,
            null,
        )
    }

    actual suspend fun createFile(name: String): PlatformFile? {
        TODO("Not yet implemented")
    }
}

actual class PlatformFile(
    private val url: NSURL,
) {
    actual val path: String
        get() = TODO("Not yet implemented")
    actual val name: String
        get() = TODO("Not yet implemented")
    actual val size: Long
        get() = TODO("Not yet implemented")

    actual suspend fun readAllBytesAsync(): ByteArray {
        TODO("Not yet implemented")
    }

    actual suspend fun writeAllBytesAsync(bytes: ByteArray) {
    }

}