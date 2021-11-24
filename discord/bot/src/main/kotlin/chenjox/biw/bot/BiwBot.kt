package chenjox.biw.bot

import cc.ekblad.toml.TomlValue
import cc.ekblad.toml.decode
import chenjox.biw.activity.collector.ActivityExtension
import chenjox.biw.bot.config.BotConfig
import chenjox.biw.bot.devtools.LogoutExtension
import chenjox.biw.dates.kordex.AppointmentExtension
import chenjox.biw.storage.kordex.CloudExtension
import com.kotlindiscord.kord.extensions.ExtensibleBot
import dev.kord.common.entity.Snowflake
import java.nio.file.Path

public suspend fun main(){

    val tomlDocument = TomlValue.from(Path.of("config.toml"))

    val config = tomlDocument.decode<BotConfig>()

    val bot: ExtensibleBot

    with(config){
        val token = config.bot.token
        val owner = Snowflake(config.bot.ownerId)
        bot = ExtensibleBot(token) {
            extensions {
                add {
                    LogoutExtension(owner)
                }
                cloud?.let {
                    add {
                        CloudExtension(
                            Path.of(it.location),
                            it.guildId,
                            it.downloadAccess.allowedRoles.map { Snowflake(it) },
                            it.uploadAccess.allowedRoles.map { Snowflake(it) }
                        )
                    }
                }
                appointment?.let {
                    add {
                        AppointmentExtension.builder().run {
                            createDirs()
                            setStoragePath(it.location)
                            setBackupStoragePath(it.backupPath)
                            setGuild(it.guildId)
                            build()
                        }
                    }
                }
                activity?.let {
                    add {
                        ActivityExtension.builder(it.guildId).run {
                            createDirIfNecessary()
                            setDatabasePath(it.location)
                            setOwner(owner.value.toLong())
                            build()
                        }
                    }
                }
            }
        }
    }
    bot.start()

}