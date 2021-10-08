package moe.tlaster.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import moe.tlaster.kfilepicker.FilePicker
import moe.tlaster.kfilepicker.PlatformFile

@Composable
fun App() {
    var files by remember {
        mutableStateOf<List<PlatformFile>>(emptyList())
    }
    val scope = rememberCoroutineScope()
    Column {
        Button(onClick = {
            scope.launch {
                files = FilePicker.pickFiles()
            }
        }) {
            Text("Click me!")
        }
        files.forEach {
            Text(it.path)
        }
    }

}
