import moe.tlaster.common.App
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import moe.tlaster.kfilepicker.FilePicker

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        FilePicker.init(window)
        DesktopMaterialTheme {
            App()
        }
    }
}