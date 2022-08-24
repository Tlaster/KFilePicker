package moe.tlaster.kfilepicker

import androidx.compose.ui.window.Window
import platform.AppKit.NSApp

fun main() {
    Window {
        App()
    }
    NSApp?.run()
}
