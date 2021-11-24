package chenjox.biw.dates.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

public fun DateTimeData(instant: Instant, reason: String, tags: List<String>): DateTimeData{
    return DateTimeData(DateData(instant.toEpochMilliseconds(), reason, tags))
}

@JvmInline
public value class DateTimeData(public val data: DateData) : Comparable<DateTimeData>{
    public val timeStamp: Instant
        get() = Instant.fromEpochMilliseconds(data.unixTimeStamp)
    public val reason: String
        get() = data.reason
    public val tags: List<String>
        get() = data.tags

    override fun compareTo(other: DateTimeData): Int {
        return data.unixTimeStamp.compareTo(other.data.unixTimeStamp)
    }

    public fun toDebugString(): String{
        return "DateData($timeStamp, '$reason')"
    }

}

@Serializable
public data class DateData(
    @SerialName("unix") public val unixTimeStamp: Long,
    @SerialName("appointment") public val reason: String,
    @SerialName("tags") public val tags: List<String> = listOf("all")
)
