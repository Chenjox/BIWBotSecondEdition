package chenjox.biw.dates.kordex

import com.kotlindiscord.kord.extensions.commands.Argument
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.CommandContext
import com.kotlindiscord.kord.extensions.commands.converters.DefaultingConverter
import com.kotlindiscord.kord.extensions.commands.converters.SingleConverter
import com.kotlindiscord.kord.extensions.parser.StringParser
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

public data class LocalTime(val hour: Int, val minute: Int, val second: Int = 0, val nanosecond: Int = 0)

public operator fun LocalDate.plus(other: LocalTime): LocalDateTime{
    return LocalDateTime(this.year, this.monthNumber, this.dayOfMonth, other.hour, other.minute, other.second, other.nanosecond)
}

public fun Arguments.localTime(display: String, description: String, validator: Validator<LocalTime>? = null): SingleConverter<LocalTime> {
    return this.arg(display,description, TimeConverter(validator))
}

public fun Arguments.defaultingLocalTime(display: String, description: String, default: LocalTime, validator: Validator<LocalTime>? = null): DefaultingConverter<LocalTime> {
    return arg(display, description, TimeConverter(validator).toDefaulting(default))
//return this.arg(display,description,TimeConverter(validator))

}

public class TimeConverter(
    override var validator: Validator<LocalTime>? = null
) : SingleConverter<LocalTime>() {
    override val signatureTypeString: String
        get() = "Something"

    override suspend fun parse(parser: StringParser?, context: CommandContext, named: String?): Boolean {
        val rawDate: String = named ?: parser?.parseNext()?.data ?: return false
        val dateComponents: List<String> = rawDate.split(':','.',',')
        val hour = dateComponents.getOrNull(0)?.toIntOrNull() ?: return false
        val minute = dateComponents.getOrNull(1)?.toIntOrNull() ?: return false
        val second = dateComponents.getOrNull(2)?.toIntOrNull()
        val nanosecond = dateComponents.getOrNull(3)?.toIntOrNull()
        parsed = LocalTime(hour, minute, second?:0, nanosecond?:0)
        return true
    }

    override suspend fun parseOption(context: CommandContext, option: OptionValue<*>): Boolean {
        val rawDate: String = (option as? OptionValue.StringOptionValue)?.value ?: return false
        val dateComponents: List<String> = rawDate.split(':')
        val hour = dateComponents.getOrNull(0)?.toIntOrNull() ?: return false
        val minute = dateComponents.getOrNull(1)?.toIntOrNull() ?: return false
        val second = dateComponents.getOrNull(2)?.toIntOrNull()
        val nanosecond = dateComponents.getOrNull(3)?.toIntOrNull()
        parsed = LocalTime(hour, minute, second?:0, nanosecond?:0)
        return true
    }

    override suspend fun toSlashOption(arg: Argument<*>): OptionsBuilder {
        return StringChoiceBuilder(arg.displayName, arg.description).apply { required = true }
    }

}