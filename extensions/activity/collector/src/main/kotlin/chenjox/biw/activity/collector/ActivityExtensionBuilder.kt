package chenjox.biw.activity.collector

import chenjox.biw.activity.database.DatabaseSession
import dev.kord.common.entity.Snowflake
import java.nio.file.Files
import java.nio.file.Path

public class ActivityExtensionBuilder internal constructor(guildId: Long){
    private val guildId: Snowflake = Snowflake(guildId)
    private lateinit var databaseFile: Path
    private var mkdir: Boolean = false
    private var ownerId: Snowflake? = null

    public fun setOwner(id: Long){
        ownerId = Snowflake(id)
    }

    public fun setDatabasePath(path: String){
        databaseFile = Path.of(path)
    }
    public fun createDirIfNecessary(confirmation: Boolean = true){
        mkdir = confirmation
    }

    public fun build(): ActivityExtension{
        if(Files.notExists(databaseFile.parent)){
            Files.createDirectory(databaseFile.parent)
        }
        val session = DatabaseSession(databaseFile)
        val collector = DatabaseCollector(session)
        return ActivityExtension(
            collector,
            guildId,
            ownerId
        )
    }

}