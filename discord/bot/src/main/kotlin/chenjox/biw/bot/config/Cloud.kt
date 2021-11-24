package chenjox.biw.bot.config

public data class Cloud(
    val location: String,
    val guildId: Long,
    val uploadAccess: UploadAccess,
    val downloadAccess: DownloadAccess
)

public data class UploadAccess(
    val allowedRoles: List<Long>
)

public data class DownloadAccess(
    val allowedRoles: List<Long>
)