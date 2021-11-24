package chenjox.biw.storage.kordex

import chenjox.biw.storage.manager.DirectoryManager
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.group
import com.kotlindiscord.kord.extensions.extensions.*
import com.kotlindiscord.kord.extensions.types.edit
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed
import java.nio.file.Path
//private val


public class CloudExtension(
    private val directoryManager: DirectoryManager,
    private val extensionGuildIds: Long,
    private val allowedDownloadRoles: List<Snowflake>,
    private val allowedUploadRoles: List<Snowflake>
) : Extension() {

    private val extensionGuildId: Snowflake = Snowflake(extensionGuildIds)

    public companion object {
        public operator fun invoke(path: Path, extensionGuildId: Long, allowedRoles: List<Snowflake>, allowedUploadRoles: List<Snowflake>): CloudExtension {
            return CloudExtension(DirectoryManager(path),extensionGuildId, allowedRoles, allowedUploadRoles)
        }
    }

    override val name: String = "CloudExtension"

    @OptIn(KordPreview::class)
    override suspend fun setup() {
        publicSlashCommand {
            name = "cloud"
            description = "Alles Cloud mäßige!"
            guild(extensionGuildId)
            this@CloudExtension.allowedDownloadRoles.forEach {
                allowRole(it)
            }
            group("lookup"){
                guild(extensionGuildId)
                description = "Alles was das runterladen betrifft."
                ephemeralSubCommand {
                    name = "traverse"
                    description = "Traversiere die Verzeichnisse"
                    guild(extensionGuildId)
                    invokeTraverser(directoryManager)
                }
            }
        }
        ephemeralMessageCommand {
            name = "Anhang hochladen"
            guild(extensionGuildId)
            allowedUploadRoles.forEach {
                allowRole(it)
            }
            action root@{
                val attachment = this.targetMessages.firstOrNull()?.attachments?.firstOrNull()
                respond {
                    embed {
                        title = "`init...`"
                    }
                }
                if(attachment==null){
                    edit {
                        embed {
                            title = "Fehler!"
                            description = "Keinen Anhang gefunden!"
                        }
                    }
                }else{
                    DirectoryTraverser(directoryManager, this@root).sendNextDirSelection(Path.of(""),attachment)
                }

            }
        }
        //ephemeralUserCommand {
        //    name = "test"
        //    action {
        //this.user.asMember(extensionGuildId).addRole()
        //    }
        //}
    }
}