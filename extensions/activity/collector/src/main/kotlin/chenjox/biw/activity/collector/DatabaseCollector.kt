package chenjox.biw.activity.collector

import chenjox.biw.activity.database.DatabaseSession
import dev.kord.common.entity.DiscordPartialMessage
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageDeleteEvent


public class DatabaseCollector(
    public val session: DatabaseSession
) {

    private val connection = session.connection

    public fun authorHash(snowflake: Snowflake): Long{
        val id = snowflake.value.toLong()
        return ((id ushr 32) xor id)
    }

    public fun insertMessage(messageEntity: Message){
        val author = messageEntity.author ?: return // System Message or Webhook
        val userHash = authorHash(author.id) // I'm not allowed to see this.

        val channel = messageEntity.channelId //

        val messageId = messageEntity.id
        val messageContentSize = messageEntity.content.encodeToByteArray().size // I'm NOT allowed to see THIS!

        val attachments = messageEntity.attachments

        connection.transaction {
            with(connection.insertQueriesQueries){
                insertNewChannel(channel.value.toLong())
                insertPseudoUser(userHash)
                insertMessage(
                    message_snowflake = messageId.value.toLong(),
                    channel_snowflake = channel.value.toLong(),
                    message_byte_size = messageContentSize.toLong(),
                    user_hash = userHash
                )
                attachments.forEach {
                    insertNewAttachment(
                        it.id.value.toLong(),
                        it.filename,
                        it.size.toLong()
                    )
                    insertMessageAttachment(
                        attachment_snowflake = it.id.value.toLong(),
                        message_snowflake = messageId.value.toLong()
                    )
                }
            }
        }
    }

    public fun insertMessageChange(partialMessageEntity: DiscordPartialMessage){
        val messageId = partialMessageEntity.id.value.toLong()
        connection.insertQueriesQueries.insertMessageChange(
            messageId
        )
    }

    public fun insertMessageDeletion(t: MessageDeleteEvent){
        val messageId = t.messageId
        connection.insertQueriesQueries.insertMessageDeletion(
            messageId.value.toLong()
        )
    }
}