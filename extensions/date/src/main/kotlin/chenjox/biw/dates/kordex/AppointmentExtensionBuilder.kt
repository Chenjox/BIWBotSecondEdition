package chenjox.biw.dates.kordex

import chenjox.biw.dates.TimezoneDateManager
import java.nio.file.Files
import java.nio.file.Path

public class AppointmentExtensionBuilder {
    public var path: Path = Path.of("dates/date.json")
    public var backupPath: Path = Path.of("dates/backup.json")
    public var guildId: Long = 0
    public var mkdirs: Boolean = false

    public fun setStoragePath(pathe: String){
        path = Path.of(pathe)
    }
    public fun setBackupStoragePath(pathe: String){
        backupPath = Path.of(pathe)
    }
    public fun setGuild(guildId: Long){
        this.guildId = guildId
    }
    public fun createDirs(confirm: Boolean = true){
        mkdirs = confirm
    }
    public fun build(): AppointmentExtension{
        if(mkdirs){
            if(Files.notExists(path.parent)) Files.createDirectory(path.parent)
            if(Files.notExists(backupPath.parent)) Files.createDirectory(backupPath.parent)
            if(Files.notExists(path)){
                Files.createFile(path)
                Files.newOutputStream(path).use {
                    it.write("{}\n".toByteArray())
                }
            }
            if(Files.notExists(backupPath)){
                Files.createFile(backupPath)
                Files.newOutputStream(backupPath).use {
                    it.write("{}\n".toByteArray())
                }
            }
        }
        return AppointmentExtension(path, backupPath, guildId)
    }
}