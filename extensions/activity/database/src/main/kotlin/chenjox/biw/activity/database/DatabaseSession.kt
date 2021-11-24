package chenjox.biw.activity.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.nio.file.Files
import java.nio.file.Path

/**
 * Class representing a Database Session and responsible for ensuring the existence of the db file, should be created once.
 *
 * Not a singleton for scalability.
 * @param databaseLocation the location of the database in the filesystem.
 */
public class DatabaseSession(
    public val databaseLocation: Path
) {
    private val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseLocation.toString()}")

    init {
        if (!Files.isRegularFile(databaseLocation)) ActivityDatabase.Schema.create(driver)
    }

    /**
     * The connection of the database.
     */
    public val connection: ActivityDatabase = ActivityDatabase(
        driver = driver
    )

    public fun size(): Long {
        return Files.size(databaseLocation)
    }
}