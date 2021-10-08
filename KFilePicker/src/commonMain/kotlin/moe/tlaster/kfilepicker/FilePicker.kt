package moe.tlaster.kfilepicker

expect object FilePicker {
    suspend fun pickFiles(
        allowedExtensions: List<String> = emptyList(),
        allowMultiple: Boolean = false,
    ): List<PlatformFile>
}

expect class PlatformFile {
    val path: String
    val size: Long
    fun readAllBytes(): ByteArray
    fun writeAllBytes(bytes: ByteArray)
}