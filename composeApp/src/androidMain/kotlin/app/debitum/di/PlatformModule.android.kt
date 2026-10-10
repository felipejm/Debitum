package app.debitum.di

import android.content.Context
import androidx.room.Room
import app.debitum.data.local.DATABASE_NAME
import app.debitum.data.local.DebitumDatabase
import app.debitum.data.local.buildDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        val context = get<Context>()
        buildDatabase(
            Room.databaseBuilder<DebitumDatabase>(
                context = context,
                name = context.getDatabasePath(DATABASE_NAME).absolutePath,
            ),
        )
    }
}
