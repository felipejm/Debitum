package app.debitum.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

@Dao
interface BillDao {
    @Query("SELECT * FROM bills WHERE isDeleted = 0 AND dueDate BETWEEN :start AND :end ORDER BY dueDate, name")
    fun observeDueBetween(start: LocalDate, end: LocalDate): Flow<List<BillEntity>>

    @Query("SELECT * FROM bills WHERE id = :id AND isDeleted = 0")
    fun observeById(id: Long): Flow<BillEntity?>

    @Query("SELECT * FROM bills WHERE id = :id")
    suspend fun getById(id: Long): BillEntity?

    @Query("SELECT * FROM bills WHERE seriesId = :seriesId AND seriesMonth > :month AND isDeleted = 0 AND paidDate IS NULL")
    suspend fun getUnpaidSeriesBillsAfter(seriesId: Long, month: YearMonth): List<BillEntity>

    @Insert
    suspend fun insert(bill: BillEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(bills: List<BillEntity>)

    @Update
    suspend fun update(bill: BillEntity)

    @Update
    suspend fun update(bills: List<BillEntity>)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM bills WHERE seriesId = :seriesId AND seriesMonth >= :month")
    suspend fun deleteSeriesBillsFrom(seriesId: Long, month: YearMonth)

    @Query("DELETE FROM bills WHERE seriesId = :seriesId")
    suspend fun deleteSeriesBills(seriesId: Long)
}

@Dao
interface BillSeriesDao {
    @Query("SELECT * FROM bill_series")
    fun observeAll(): Flow<List<BillSeriesEntity>>

    @Query("SELECT * FROM bill_series WHERE startMonth <= :month AND (endMonth IS NULL OR endMonth >= :month)")
    suspend fun getActiveIn(month: YearMonth): List<BillSeriesEntity>

    @Query("SELECT * FROM bill_series WHERE id = :id")
    suspend fun getById(id: Long): BillSeriesEntity?

    @Insert
    suspend fun insert(series: BillSeriesEntity): Long

    @Update
    suspend fun update(series: BillSeriesEntity)

    @Query("DELETE FROM bill_series WHERE id = :id")
    suspend fun delete(id: Long)
}
