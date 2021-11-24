package chenjox.biw.dates

import chenjox.biw.dates.data.DateData
import chenjox.biw.dates.data.DateTimeData
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Files
import java.nio.file.Path

@Serializable
public data class DateManagerData(
    public val dates: List<DateData>
)

public class DateManager private constructor(
    private val dates: MutableList<DateTimeData>
){

    init {
        dates.sort()
    }

    public companion object {

        public operator fun invoke(d: DateManagerData): DateManager {
            return DateManager(d.dates.map{ DateTimeData(it) }.toMutableList())
        }

        public operator fun invoke(map: Map<Instant, String>): DateManager{
            return DateManager(
                map.map { DateData(it.key.toEpochMilliseconds(), it.value) }
                    .map { DateTimeData(it) }
                    .toMutableList()
            )
        }

    }

    private fun sort(){
        dates.sort()
    }

    //private val mutex = Mutex()

    public fun insertDate(d: DateTimeData){
        dates.add(d)
        sort()
    }

    public fun deleteDatesBefore(d: Instant){
        dates.removeIf { it.timeStamp < d }
    }

    /** Returns the Date after the given DateTime, or null if none is found */
    public fun getDateAfter(d: Instant): DateTimeData? {
        return dates.reversed().find {
            it.timeStamp > d
        }
    }

    /**
     * Returns the Dates after the given DateTime, or an empty list if none is found
     */
    public fun getDatesAfter(d: Instant): List<DateTimeData> {
        return dates.filter { it.timeStamp > d }
        //return da ?: emptyList()
    }


    /**
     * Returns the Date before the given DateTime, or null if none is found
      */
    public fun getDateBefore(d: Instant): DateTimeData? {
        return dates.reversed().find {
            it.timeStamp < d
        }
    }

    /**
     * Returns the Dates before the given DateTime, or an empty list if none is found
     */
    public fun getDatesBefore(d: Instant): List<DateTimeData> {
        return dates.filter { it.timeStamp < d }
    }

    public fun getAllDates(): List<DateTimeData> {
        return dates.toList()
    }

    public fun getData(): DateManagerData{
        return DateManagerData(dates.map { it.data })
    }

    public fun serialize(path: Path){
        Files.newOutputStream(path).use {
            Json.encodeToStream(getData(),it)
        }
    }
}