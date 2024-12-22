package com.arckenver.nations.bukkit.manager

import com.arckenver.nations.bukkit.Nations
import java.io.File
import java.io.InputStream
import java.io.OutputStream

abstract class PersistentManager(
    val file: File,
    val defaultFileContent: String = ""
) {
    constructor(file: String, defaultFileContent: String = "") : this(File(file), defaultFileContent)

    private val actualFile: File get() = if (file.isAbsolute) file else File(Nations.dataDir, file.path)

    abstract fun loadFromStream(stream: InputStream)
    abstract fun dumpToStream(stream: OutputStream)

    fun load() {
        if (actualFile.createNewFile() && defaultFileContent != "") {
            actualFile.writeText(defaultFileContent, Charsets.UTF_8)
        }
        val stream = actualFile.inputStream()
        loadFromStream(stream)
        stream.close()
    }

    fun dump() {
        val stream = actualFile.outputStream()
        dumpToStream(stream)
        stream.close()
    }
}
