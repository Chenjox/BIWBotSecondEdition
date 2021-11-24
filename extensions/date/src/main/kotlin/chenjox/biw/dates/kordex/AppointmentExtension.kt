package chenjox.biw.dates.kordex

import chenjox.biw.dates.TimezoneDateManager
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.ephemeralSubCommand
import com.kotlindiscord.kord.extensions.commands.application.slash.publicSubCommand
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalCoalescingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import com.kotlindiscord.kord.extensions.types.respond
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.embed
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toInstant
import java.nio.file.Path

//TODO BUILDER!

public class AppointmentExtension private constructor(
    public val path: Path,
    public val backupPath: Path,
    public val manager: TimezoneDateManager,
    guildId: Long
) : Extension() {

    private val testGuildId = Snowflake(guildId)



    public companion object {
        public operator fun invoke(path: Path, backupPath: Path, guildId: Long): AppointmentExtension {
            val m = TimezoneDateManager(path) ?: throw IllegalArgumentException()
            return AppointmentExtension(path, backupPath, m, guildId)
        }
        public fun builder(): AppointmentExtensionBuilder = AppointmentExtensionBuilder()
    }

    override val name: String
        get() = "Appointment"


    public inner class AddArguments : Arguments(){
        public val reason: String by string("Beschreibung", "Kurze Beschreibung des Termins")
        public val termin: LocalDate by date("Datum", "Das Datum in TT:MM:YYYY Format")
        public val uhrzeit: LocalTime by defaultingLocalTime("Uhrzeit","Die Uhrzeit im hh:mm Format", LocalTime(12, 0))
        public val tags: String? by optionalCoalescingString("Tags","Eine mit Komma getrennte Liste von Tags.")
    }

    public inner class TerminArguments : Arguments(){
        public val tags: String? by optionalCoalescingString("Tags","Eine mit Komma getrennte Liste von Tags.")
    }
    // TODO Backups und so

    override suspend fun setup() {
        publicSlashCommand {
            name = "termin"
            description = "Alle Belange Terminlicher Art"
            guild(testGuildId)
            publicSubCommand( { TerminArguments() } ) {
                name = "übersicht"
                description = "Zeigt aktuelle Termine für alle an"
                guild(testGuildId)

                action {
                    val now = this.event.interaction.data.id.timestamp
                    respond {
                        val d = manager.getDateTime(now).let { dateTime ->
                            manager.getDatesAfter(dateTime).run {
                                val tags = arguments.tags
                                if (tags!=null){
                                    val taglist = tags.split(',').map { it.trim() }
                                    this.filter { (it.tags intersect taglist).isNotEmpty() }
                                } else this
                            }
                        }.take(10)
                        embed {
                            title = "Momentane Termine"
                            description = StringBuilder().also {
                                for (e in d){
                                    val uhrzeitAnzeigen = !(e.date.hour == 12 && e.date.minute == 0 && e.date.second == 0 && e.date.nanosecond == 0)
                                    val l = e.date.toInstant(e.timeZone).epochSeconds
                                    it.append("<t:$l:d>")
                                    if(uhrzeitAnzeigen)it.append(" <t:$l:t> ")
                                    it.append(" - ${e.string}")
                                        .appendLine()
                                        .append("<t:$l:R>")
                                        .appendLine()
                                }
                                if (d.isEmpty()){
                                    it.append("Keine Termine nach <t:${now.epochSeconds}:d> vorhanden!")
                                }
                            }.toString()
                        }
                    }
                }
            }
            ephemeralSubCommand({ TerminArguments() } ) {
                name = "anstehend"
                description = "Zeigt aktuelle Termine für Dich an"
                guild(testGuildId)

                action {
                    val now = this.event.interaction.data.id.timestamp
                    respond {
                        val d = manager.getDateTime(now).let { dateTime ->
                            manager.getDatesAfter(dateTime).run {
                                val tags = arguments.tags
                                if (tags!=null){
                                    val taglist = tags.split(',').map { it.trim() }
                                    this.filter { (it.tags intersect taglist).isNotEmpty() }
                                } else this
                            }
                        }.take(10)
                        embed {
                            title = "Momentane Termine"
                            description = StringBuilder().also {
                                for (e in d){
                                    val uhrzeitAnzeigen = !(e.date.hour == 12 && e.date.minute == 0 && e.date.second == 0 && e.date.nanosecond == 0)
                                    val l = e.date.toInstant(e.timeZone).epochSeconds
                                    it.append("<t:$l:d>")
                                    if(uhrzeitAnzeigen)it.append(" <t:$l:t> ")
                                    it.append(" - ${e.string}")
                                        .appendLine()
                                        .append("<t:$l:R>")
                                        .appendLine()
                                }
                                if (d.isEmpty()){
                                    it.append("Keine Termine nach <t:${now.epochSeconds}:d> vorhanden!")
                                }
                            }.toString()
                        }
                    }
                }
            }
            /*
            ephemeralSubCommand {
                name = "debug"
                description = "Entwickler Menü, Enthält sehr viele technische Infos"
                guild(testGuildId)
                action {
                    respond {
                        val d = manager.getAllDates().take(10)
                        embed {
                            title = "Alle gespeicherten Termine"
                            description = StringBuilder().also {
                                for (e in d){
                                    val l = e.date.toInstant(e.timeZone).epochSeconds
                                    it.append("<t:$l:d>").append(" <t:$l:t> ").append(" - ${e.string}").appendLine()
                                        .append("<t:$l:R>").appendLine()
                                }
                            }.toString()
                        }
                    }
                }
            }
            */
            ephemeralSubCommand({ AddArguments() } ) {
                name = "hinzufügen"
                description = "Fügt einen Termin hinzu"
                guild(testGuildId)
                action {
                    //val arguments = this.arguments
                    val localDateTime = arguments.termin + arguments.uhrzeit
                    val tags = listOf("all").let { taglist ->
                        arguments.tags?.split(',')?.map { it.trim() }?.plus(taglist) ?: taglist
                    }
                    manager.insertDate(localDateTime, arguments.reason, tags) //TODO Tags!
                    respond {
                        val uhrzeitAnzeigen = arguments.uhrzeit.hour != 0 && arguments.uhrzeit.minute != 0 && arguments.uhrzeit.second != 0 && arguments.uhrzeit.nanosecond != 0
                        val l = localDateTime.toInstant(manager.timezone).epochSeconds
                        val timestamp = if(uhrzeitAnzeigen) "<t:$l:t>" else ""
                        content = "Folgender Termin wurde hinzugefügt:\n> <t:$l:d> $timestamp - ${arguments.reason}\n > <t:$l:R>"
                    }
                }
            }
            ephemeralSubCommand {

                name = "speichern"
                description = "Speichert die Termine ab"
                guild(testGuildId)
                action {
                    manager.serialize(backupPath)
                    val now = this.event.interaction.data.id.timestamp
                    manager.deleteDatesBefore(now)
                    manager.serialize(path)
                    respond {
                        content = "Serialized successfully"
                    }
                }
            }
        }
    }



    override suspend fun unload() {
        super.unload()
        manager.serialize(path)
    }

}