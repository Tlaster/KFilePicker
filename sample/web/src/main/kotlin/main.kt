import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.kfilepicker.PlatformFile
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        Body()
    }
}

@Composable
fun Body() {
    var files by remember {
        mutableStateOf<List<PlatformFile>>(emptyList())
    }
    val scope = rememberCoroutineScope()
    Div {
        Button(
            attrs = {
                onClick {
                    scope.launch {
                        files = FilePicker.pickFiles()
                    }
                }
            }
        ) {
            Text("Click me!")
        }
        files.forEach {
            Div {
                Text(it.path)
            }
            Div {
                Text(it.size.toString())
            }
        }
    }
}