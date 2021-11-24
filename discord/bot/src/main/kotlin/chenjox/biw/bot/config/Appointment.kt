package chenjox.biw.bot.config

public data class Appointment(
    val location: String,
    val backupPath: String,
    val guildId: Long,
    val allowedRoles: List<Long>
)
