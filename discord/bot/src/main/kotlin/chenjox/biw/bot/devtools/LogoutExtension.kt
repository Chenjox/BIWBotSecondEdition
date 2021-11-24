package chenjox.biw.bot.devtools

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralMessageCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake

public class LogoutExtension(
    public val ownerId: Snowflake
): Extension() {

    override val name: String
        get() = "LogoutExtension"

    override suspend fun setup() {
        ephemeralMessageCommand {
            name = "Logout"
            //allowUser(ownerId)
            action {
                if(user.id==ownerId) {
                    respond {
                        content = "```\nLogging Out. Closing connections.\n```"
                    }
                    this@LogoutExtension.kord.shutdown()
                }else{
                    respond {
                        content = "```\nUnzureichende Berechtigungen!\n```"
                    }
                }
            }
        }
    }

}