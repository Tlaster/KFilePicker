package moe.tlaster.kfilepicker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FilePicker.init(activityResultRegistry, this, contentResolver)
        setContent {
            App()
        }
    }
}
