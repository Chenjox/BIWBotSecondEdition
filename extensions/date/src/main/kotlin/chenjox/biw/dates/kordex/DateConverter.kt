package chenjox.biw.dates.kordex

import com.kotlindiscord.kord.extensions.commands.Argument
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.CommandContext
import com.kotlindiscord.kord.extensions.commands.converters.SingleConverter
import com.kotlindiscord.kord.extensions.parser.StringParser
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.rest.builder.interaction.OptionsBuilder
import dev.kord.rest.builder.interaction.StringChoiceBuilder
import kotlinx.datetime.LocalDate

public typealias Validator<T> = (suspend CommandContext.(arg: Argument<*>, value: T) -> Unit)

public fun Arguments.date(display: String, description: String, validator: Validator<LocalDate>? = null): SingleConverter<LocalDate>{
    return this.arg(display, description, DateConverter(validator))
}

public class DateConverter(
    override var validator: Validator<LocalDate>? = null
) : SingleConverter<LocalDate>() {
    override val signatureTypeString: String = "converter.date"

    override suspend fun parse(parser: StringParser?, context: CommandContext, named: String?): Boolean {
        val rawDate: String = named ?: parser?.parseNext()?.data ?: return false
        val dateComponents: List<String> = rawDate.split(':','/','.')
        val day = dateComponents.getOrNull(0)?.toIntOrNull() ?: return false
        val month = dateComponents.getOrNull(1)?.toIntOrNull() ?: return false
        val year = dateComponents.getOrNull(2)?.toIntOrNull() ?: return false
        parsed = LocalDate(year, month, day)
        return true
    }

    override suspend fun parseOption(context: CommandContext, option: OptionValue<*>): Boolean {
        val optionValue = (option as? OptionValue.StringOptionValue)?.value ?: return false
        val dateComponents: List<String> = optionValue.split(':','/','.')
        val day = dateComponents.getOrNull(0)?.toIntOrNull() ?: return false
        val month = dateComponents.getOrNull(1)?.toIntOrNull() ?: return false
        val year = dateComponents.getOrNull(2)?.toIntOrNull() ?: return false
        parsed = LocalDate(year, month, day)
        return true
    }

    override suspend fun toSlashOption(arg: Argument<*>): OptionsBuilder {
        return StringChoiceBuilder(arg.displayName, arg.description).apply { required = true }
    }

}