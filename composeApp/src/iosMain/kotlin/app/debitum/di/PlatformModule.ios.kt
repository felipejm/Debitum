package app.debitum.di

import androidx.room.Room
import app.debitum.data.local.DATABASE_NAME
import app.debitum.data.local.DebitumDatabase
import app.debitum.data.local.buildDatabase
import app.debitum.data.settings.IosThemeStore
import app.debitum.data.settings.ThemeStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single<ThemeStore> { IosThemeStore() }
    single {
        buildDatabase(Room.databaseBuilder<DebitumDatabase>(name = databasePath()))
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun databasePath(): String {
    val documents = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documents?.path) + "/$DATABASE_NAME"
}
