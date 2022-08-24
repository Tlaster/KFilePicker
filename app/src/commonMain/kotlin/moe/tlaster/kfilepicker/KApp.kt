package moe.tlaster.kfilepicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@Composable
fun App() {
    var files by remember {
        mutableStateOf<List<PlatformFile>>(emptyList())
    }
    val scope = rememberCoroutineScope()
    Column {
        SampleButton(text = "Launch SingleFilePicker to pick all kinds of files") {
            scope.launch {
                files = FilePicker.pickFiles()
            }
        }
        SampleButton(text = "Launch MultipleFilePicker to pick all kinds of files") {
            scope.launch {
                files = FilePicker.pickFiles(allowMultiple = true)
            }
        }
        SampleButton(text = "Launch SingleFilePicker to pick videos and images") {
            scope.launch {
                files = FilePicker.pickFiles(allowedExtensions = listOf("mp4", "jpg", "png"))
            }
        }
        SampleButton(text = "Launch MultipleFilePicker to pick videos and images") {
            scope.launch {
                files = FilePicker.pickFiles(
                    allowedExtensions = listOf("mp4", "jpg", "png"),
                    allowMultiple = true
                )
            }
        }
        files.forEach {
            Text(it.path)
        }
    }
}

@Composable
private fun SampleButton(text: String, onLaunch: () -> Unit) {
    Button(modifier = Modifier.fillMaxWidth().padding(20.dp),
        onClick = {
            onLaunch.invoke()
        }) {
        Text(text)
    }
}
