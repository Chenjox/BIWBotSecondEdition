package chenjox.biw.dates

import chenjox.biw.dates.data.DateTimeData
import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Files
import java.nio.file.Path

@Serializable
public data class TimezoneDateManagerData (
    @SerialName("timezone") public val timezone: String,
    @SerialName("data") public val dateManagerData: DateManagerData
    )

public class TimezoneDateManager private constructor(
    public val timezone: TimeZone,
    private val dateManager: DateManager
) {
    public companion object {
        public operator fun invoke(TimeZone: TimeZone, dateManager: DateManager): TimezoneDateManager {
            return TimezoneDateManager(TimeZone, dateManager)
        }
        public operator fun invoke(data: TimezoneDateManagerData): TimezoneDateManager {
            return TimezoneDateManager(TimeZone.of(data.timezone), DateManager(data.dateManagerData))
        }
        @OptIn(ExperimentalSerializationApi::class)
        public operator fun invoke(path: Path): TimezoneDateManager? {
            val data: TimezoneDateManagerData
            Files.newInputStream(path).use {
                data = Json.decodeFromStream<TimezoneDateManagerData>(it)
            }
            return if(data!=null) TimezoneDateManager(data) else null
        }
    }

    public fun insertDate(date: LocalDateTime, reason: String, tags: List<String> = emptyList()){
        dateManager.insertDate(DateTimeData(date.toInstant(timezone), reason, tags))
    }
    public fun deleteDatesBefore(date: LocalDateTime){
        dateManager.deleteDatesBefore(date.toInstant(timezone))
    }

    public fun deleteDatesBefore(i: Instant){
        dateManager.deleteDatesBefore(i)
    }

    /** Returns the Date after the given DateTime, or null if none is found */
    public fun getDateAfter(d: LocalDateTime): Appointment? {
        return dateManager.getDateAfter(d.toInstant(timezone))?.toAppointment()
    }

    public fun getDateTime(i: Instant): LocalDateTime = i.toLocalDateTime(timezone)

    /**
     * Returns the Dates after the given DateTime, or an empty list if none is found
     */
    public fun getDatesAfter(d: LocalDateTime): List<Appointment> {
        return dateManager.getDatesAfter(
            d.toInstant(timezone)
        ).map {
            it.toAppointment()
        }
    }

    public fun getAllDates(): List<Appointment> {
        return dateManager.getAllDates().map {
            it.toAppointment()
        }
    }

    /**
     * Returns the Date before the given DateTime, or null if none is found
     */
    public fun getDateBefore(d: LocalDateTime): Appointment? {
        return dateManager.getDateBefore(d.toInstant(timezone))?.toAppointment()
    }

    /**
     * Returns the Dates before the given DateTime, or an empty list if none is found
     */
    public fun getDatesBefore(d: LocalDateTime): List<Appointment> {
        return dateManager.getDatesBefore(d.toInstant(timezone)).map {
            it.toAppointment()
        }
    }

    private fun DateTimeData.toAppointment(): Appointment {
        return Appointment(this.timeStamp.toLocalDateTime(timezone),this.reason, this.tags, timezone)
    }

    public data class Appointment(
        public val date: LocalDateTime,
        public val string: String,
        public val tags: List<String>,
        public val timeZone: TimeZone
    )

    public fun getData(): TimezoneDateManagerData {
        return TimezoneDateManagerData(timezone = timezone.id, dateManager.getData())
    }

    public fun serialize(path: Path){
        Files.newOutputStream(path).use {
            Json.encodeToStream(getData(),it)
        }
    }

}