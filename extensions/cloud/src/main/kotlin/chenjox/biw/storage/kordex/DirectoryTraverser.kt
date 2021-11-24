package chenjox.biw.storage.kordex

import chenjox.biw.storage.manager.DirectoryManager
import com.kotlindiscord.kord.extensions.commands.application.slash.EphemeralSlashCommand
import com.kotlindiscord.kord.extensions.components.components
import com.kotlindiscord.kord.extensions.components.ephemeralButton
import com.kotlindiscord.kord.extensions.components.ephemeralSelectMenu
import com.kotlindiscord.kord.extensions.components.publicButton
import com.kotlindiscord.kord.extensions.types.EphemeralInteractionContext
import com.kotlindiscord.kord.extensions.types.edit
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.ButtonStyle
import dev.kord.core.entity.Attachment
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import java.nio.file.Path
import kotlin.io.path.name

private val httpClient = HttpClient()

public suspend fun EphemeralSlashCommand<*>.invokeTraverser(directoryManager: DirectoryManager){
    action root@{
        respond {
            embed {
                title = "`init...`"
            }
        }
        DirectoryTraverser(directoryManager,this@root).sendNext(Path.of(""))
    }
}

public class DirectoryTraverser(
    public val directoryManager: DirectoryManager,
    public val ephemeralInteraction: EphemeralInteractionContext
) {
    private val homePath: Path = directoryManager.path

    private var current: Path = homePath


    public suspend fun selectFile(path: Path){
        ephemeralInteraction.edit {
            val f = directoryManager.readFilesFromPath(path,1)
                .take(15)
                .withIndex()
                .associateBy({ it.index.toString() }, {it.value})

            embed {
                title = "Verzeichnis `${path.name.ifEmpty { "Root" }}`"
                description = StringBuilder("Wähle eine Datei aus um sie herunterzuladen\n")
                    .apply {
                        for (s in f){
                            append('`').append(s.value.name).append('`').appendLine()
                        }
                    }
                    .toString()
            }
            components {
                current = homePath.relativize(f.values.first())
                ephemeralSelectMenu(1) {
                    f.forEach {
                        val resolved = homePath.relativize(it.value)
                        option(resolved.name.take(20), it.key) {
                            if (it.key=="0") default = true
                            deferredAck = true
                        }
                    }
                    action {
                        current = homePath.relativize(f[selected.first()]!!)
                    }
                }
                publicButton(0) {
                    style = ButtonStyle.Secondary
                    label = "Datei aussuchen"
                    deferredAck = true
                    action {
                        respond {
                            val f = directoryManager.path.resolve(current)
                            addFile(f)
                        }
                    }
                }
            }
        }
    }

    public suspend fun sendNext(path: Path){
        ephemeralInteraction.edit {
            val f = directoryManager.readFilesFromPath(path,1) //TODO Filter nach legalen herunterladbaren dateien
                .take(15)
            embed {
                title = "Verzeichnis `${path.name.ifEmpty { "Root" }}`"
                description = StringBuilder("Wähle ein Subverzeichnis aus der Liste um es zu öffnen.\nWähle `Datei aussuchen` um eine Datei des Verzeichnisses zu wählen.\nDateien im Verzeichnis\n")
                    .apply {
                        for (s in f){
                            append('`').append(s.name).append('`').appendLine()
                        }
                    }
                    .toString()
            }
            components {
                val l = directoryManager.readDirectoriesFromPath(path, 1)
                    .take(10)
                    .withIndex()
                    .associateBy({ it.index.toString() }, {it.value})
                if (l.isNotEmpty()) {
                    current = homePath.relativize(l.values.first())
                    ephemeralSelectMenu(1) {
                        l.forEach {
                            val resolved = homePath.relativize(it.value)
                            option(resolved.name, it.key) {
                                if (it.key=="0") default = true
                                deferredAck = true
                            }
                        }
                        action {
                            current = homePath.relativize(l[selected.first()]!!)
                        }
                    }
                }
                if(homePath.resolve(path) != homePath) {
                    ephemeralButton(0) {
                        style = ButtonStyle.Primary
                        label = "Ein Verzeichnis höher \u2B06"
                        deferredAck = true

                        action {
                            current = path.parent ?: Path.of("")
                            sendNext(current)
                        }
                    }
                }
                if(l.isNotEmpty()) {
                    ephemeralButton(0) {
                        style = ButtonStyle.Primary
                        label = "Ein Verzeichnis tiefer \u2B07"
                        deferredAck = true
                        action {
                            sendNext(current)
                        }
                    }
                }
                if(f.isNotEmpty()){
                    ephemeralButton {
                        style = ButtonStyle.Secondary
                        label = "Datei aussuchen"
                        deferredAck = true
                        action {
                            selectFile(path)
                        }
                    }
                }
            }
        }
    }

    public suspend fun selectDirectory(path: Path, attachment: Attachment){
        val channel = httpClient.get<ByteReadChannel>(attachment.url)
        directoryManager.writeFileToPath(path.resolve(attachment.filename)){
            channel.copyTo(it)
        }
        ephemeralInteraction.edit {
            embed {
                title = "Erfolgreich hochgeladen"
                description = "`${attachment.filename}` wurde erfolgreich zu `${path.name}` hochgeladen"
            }
            components {

            }
        }
    }

    public suspend fun sendNextDirSelection(path: Path, attachment: Attachment){
        ephemeralInteraction.edit {

            embed {
                title = "Verzeichnis `${path.name.ifEmpty { "Root" }}`"
                description = "Wähle einen Speicherort\n Momentanes Speicherverzeichnis `${path.name.ifEmpty { "Root" }}`."
            }
            components {
                val l = directoryManager.readDirectoriesFromPath(path, 1)
                    .take(10)
                    .withIndex()
                    .associateBy({ it.index.toString() }, {it.value})
                if (l.isNotEmpty()) {
                    current = homePath.relativize(l.values.first())
                    ephemeralSelectMenu(1) {
                        l.forEach {
                            val resolved = homePath.relativize(it.value)
                            option(resolved.name, it.key) {
                                if (it.key=="0") default = true
                                deferredAck = true
                            }
                        }
                        action {
                            current = homePath.relativize(l[selected.first()]!!)
                        }
                    }
                }
                if(homePath.resolve(path) != homePath) {
                    ephemeralButton(0) {
                        style = ButtonStyle.Primary
                        label = "Ein Verzeichnis höher \u2B06"
                        deferredAck = true

                        action {
                            current = path.parent ?: Path.of("")
                            sendNextDirSelection(current, attachment)
                        }
                    }
                }
                if(l.isNotEmpty()) {
                    ephemeralButton(0) {
                        style = ButtonStyle.Primary
                        label = "Ein Verzeichnis tiefer \u2B07"
                        deferredAck = true
                        action {
                            sendNextDirSelection(current, attachment)
                        }
                    }
                }
                ephemeralButton {
                    style = ButtonStyle.Secondary
                    label = "Verzeichnis wählen"
                    deferredAck = true
                    action {
                        selectDirectory(path, attachment)
                    }
                }
            }
        }
    }
}