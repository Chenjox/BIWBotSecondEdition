package chenjox.biw.activity.collector

import com.kotlindiscord.kord.extensions.checks.inGuild
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.ephemeralUserCommand
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.MessageDeleteEvent
import dev.kord.core.event.message.MessageUpdateEvent

public class ActivityExtension(
    private val databaseCollector: DatabaseCollector,
    private val activityGuild: Snowflake,
    private val ownerId: Snowflake?
) : Extension(){

    public companion object {
        public fun builder(guildId: Long) : ActivityExtensionBuilder{
            return ActivityExtensionBuilder(guildId)
        }
    }

    override val name: String
        get() = "Activity Extension"


    override suspend fun setup() {
        if(ownerId !=null){
            ephemeralUserCommand{
                guild(activityGuild)
                name = "User Info"
                action {
                    if(user.id == ownerId) {
                        val size = databaseCollector.session.size() / (1024)
                        respond {
                            content = "```\nGröße der Datenbank ist: $size kB\n```"
                        }
                    }else {
                        val us = this.targetUsers.take(1).firstOrNull()
                        respond {
                            content = if (us!=null){
                                "`${us.id}`: ${us.username}, Seit <t:${us.id.timestamp}:t> auf Discord aktiv."
                            } else "Something went wrong"
                        }
                    }
                }
            }
        }
        event<MessageCreateEvent> {
            check {
                inGuild(activityGuild)
            }
            action {
                databaseCollector.insertMessage(this.event.message)
            }
        }
        event<MessageUpdateEvent> {
            check {
                inGuild(activityGuild)
            }
            action {
                databaseCollector.insertMessageChange(this.event.new)
            }
        }
        event<MessageDeleteEvent> {
            check {
                inGuild(activityGuild)
            }
            action {
                databaseCollector.insertMessageDeletion(this.event)
            }
        }
    }

}