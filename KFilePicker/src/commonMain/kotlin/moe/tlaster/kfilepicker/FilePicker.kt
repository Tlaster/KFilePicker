package moe.tlaster.kfilepicker

expect object FilePicker {
    suspend fun pickFiles(
        allowedExtensions: List<String> = emptyList(),
        allowMultiple: Boolean = false,
    ): List<PlatformFile>
}

expect class PlatformFile {
    val path: String
    val name: String
    val size: Long
    suspend fun readAllBytesAsync(): ByteArray
    suspend fun writeAllBytesAsync(bytes: ByteArray)
}