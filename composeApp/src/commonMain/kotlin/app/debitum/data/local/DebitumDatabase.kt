package app.debitum.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

const val DATABASE_NAME = "debitum.db"

@Database(entities = [BillEntity::class, BillSeriesEntity::class], version = 1)
@TypeConverters(Converters::class)
@ConstructedBy(DebitumDatabaseConstructor::class)
abstract class DebitumDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao

    abstract fun billSeriesDao(): BillSeriesDao
}

@Suppress("KotlinNoActualForExpect")
expect object DebitumDatabaseConstructor : RoomDatabaseConstructor<DebitumDatabase> {
    override fun initialize(): DebitumDatabase
}

fun buildDatabase(builder: RoomDatabase.Builder<DebitumDatabase>): DebitumDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
