package chenjox.biw.storage.manager

import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

public class DirectoryManager private constructor(
    public val path: Path
    ) {

    public companion object {
        public operator fun invoke(path: Path): DirectoryManager {
            if(Files.isDirectory(path)) return DirectoryManager(path)
            else throw IllegalArgumentException("${path.toString()} is not a directory!")
        }

        public operator fun invoke(path: String): DirectoryManager {
            return DirectoryManager(Path.of(path))
        }
    }

    public fun readDirectoriesFromPath(ofPath: Path = Path.of(""), depth: Int = 1): List<Path>{
        val p = path.resolve(ofPath)
        if (Files.isDirectory(p)) {
            val s = Files.walk(
                p,
                depth
            )
            return s.filter { !it.equals(p) && Files.isDirectory(it) }.collect(Collectors.toList())
        }
        throw IllegalArgumentException("The supplied path $ofPath is not a directory! Looked in $p")
    }

    public fun readFilesFromPath(ofPath: Path = Path.of(""), depth: Int = 1): List<Path>{
        val p = path.resolve(ofPath)
        if (Files.isDirectory(p)) {
            val s = Files.walk(
                p,
                depth
            )
            return s.filter { !it.equals(p) && Files.isRegularFile(it) }.collect(Collectors.toList())
        }
        throw IllegalArgumentException("The supplied path $ofPath is not a directory! Looked in $p")
    }

    public suspend fun writeFileToPath(toPath: Path, fileStream: suspend (FileOutputStream) -> Unit): Path{
        val p = path.resolve(toPath).toFile()
        p.outputStream().use {
            fileStream(it)
        }
        return p.toPath()
    }

    public fun readFileFromPath(ofPath: Path): InputStream {
        val p = path.resolve(ofPath)
        if(Files.isRegularFile(p)){
            return Files.newInputStream(p)
        }
        throw IllegalArgumentException("The supplied path $ofPath is not a file!")
    }

    public fun isFile(ofPath: Path): Boolean {
        return path.resolve(ofPath).isRegularFile()
    }

    public fun isDirectory(ofPath: Path): Boolean {
        return path.resolve(ofPath).isDirectory()
    }

}

internal fun Path.isRegularFile(): Boolean = Files.isRegularFile(this)
internal fun Path.isDirectory(): Boolean = Files.isDirectory(this)