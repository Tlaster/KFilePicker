package moe.tlaster.android

import moe.tlaster.common.App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import moe.tlaster.kfilepicker.FilePicker

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FilePicker.init(activityResultRegistry, this, contentResolver)
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}