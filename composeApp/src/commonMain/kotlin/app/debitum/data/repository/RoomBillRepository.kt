package app.debitum.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import app.debitum.data.local.BillEntity
import app.debitum.data.local.BillSeriesEntity
import app.debitum.data.local.DebitumDatabase
import app.debitum.domain.dayClamped
import app.debitum.domain.model.Bill
import app.debitum.domain.model.RecurrenceScope
import app.debitum.domain.monthsBetween
import app.debitum.domain.plusMonths
import app.debitum.domain.repository.BillRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.yearMonth

class RoomBillRepository(
    private val database: DebitumDatabase,
) : BillRepository {
    private val billDao = database.billDao()
    private val seriesDao = database.billSeriesDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeBillsDueIn(month: YearMonth): Flow<List<Bill>> =
        seriesDao.observeAll()
            .onEach { createOccurrencesIn(month) }
            .flatMapLatest { billDao.observeDueBetween(month.firstDay, month.lastDay) }
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeBill(id: Long): Flow<Bill?> = billDao.observeById(id).map { it?.toDomain() }

    override fun observeLastPaidAmount(seriesId: Long, excludeBillId: Long): Flow<Long?> =
        billDao.observeLastPaidAmount(seriesId, excludeBillId)

    override suspend fun createBill(name: String, expectedAmountCents: Long?, dueDate: LocalDate, recurring: Boolean) {
        transaction {
            if (recurring) {
                val month = dueDate.yearMonth
                val seriesId = seriesDao.insert(
                    BillSeriesEntity(
                        name = name,
                        expectedAmountCents = expectedAmountCents,
                        dayOfMonth = dueDate.day,
                        startMonth = month,
                    ),
                )
                billDao.insert(
                    BillEntity(
                        name = name,
                        expectedAmountCents = expectedAmountCents,
                        dueDate = dueDate,
                        seriesId = seriesId,
                        seriesMonth = month,
                    ),
                )
            } else {
                billDao.insert(BillEntity(name = name, expectedAmountCents = expectedAmountCents, dueDate = dueDate))
            }
        }
    }

    override suspend fun updateBill(
        id: Long,
        name: String,
        expectedAmountCents: Long?,
        dueDate: LocalDate,
        scope: RecurrenceScope,
    ) {
        transaction {
            val bill = billDao.getById(id) ?: return@transaction
            billDao.update(bill.copy(name = name, expectedAmountCents = expectedAmountCents, dueDate = dueDate))

            val series = bill.seriesId?.let { seriesDao.getById(it) }
            val month = bill.seriesMonth
            if (scope != RecurrenceScope.ThisAndFollowing || series == null || month == null) return@transaction

            // Earlier months keep the old values, so they must exist before the template changes.
            createOccurrences(series, from = series.startMonth, through = month.plusMonths(-1))
            val updated = series.copy(name = name, expectedAmountCents = expectedAmountCents, dayOfMonth = dueDate.day)
            seriesDao.update(updated)
            billDao.update(
                billDao.getUnpaidSeriesBillsAfter(series.id, month).map { later ->
                    later.copy(
                        name = name,
                        expectedAmountCents = expectedAmountCents,
                        dueDate = later.seriesMonth!!.dayClamped(updated.dayOfMonth),
                    )
                },
            )
        }
    }

    override suspend fun deleteBill(id: Long, scope: RecurrenceScope) {
        transaction {
            val bill = billDao.getById(id) ?: return@transaction
            val series = bill.seriesId?.let { seriesDao.getById(it) }
            val month = bill.seriesMonth
            when {
                series == null || month == null -> billDao.delete(id)
                scope == RecurrenceScope.ThisMonth -> billDao.update(bill.copy(isDeleted = true))
                month <= series.startMonth -> {
                    billDao.deleteSeriesBills(series.id)
                    seriesDao.delete(series.id)
                }
                else -> {
                    seriesDao.update(series.copy(endMonth = month.plusMonths(-1)))
                    billDao.deleteSeriesBillsFrom(series.id, month)
                }
            }
        }
    }

    override suspend fun registerPayment(id: Long, amountCents: Long?, date: LocalDate) {
        val bill = billDao.getById(id) ?: return
        billDao.update(bill.copy(paidAmountCents = amountCents, paidDate = date))
    }

    override suspend fun undoPayment(id: Long) {
        val bill = billDao.getById(id) ?: return
        billDao.update(bill.copy(paidAmountCents = null, paidDate = null))
    }

    private suspend fun createOccurrencesIn(month: YearMonth) {
        transaction {
            billDao.insertIfAbsent(seriesDao.getActiveIn(month).map { it.occurrenceIn(month) })
        }
    }

    private suspend fun createOccurrences(series: BillSeriesEntity, from: YearMonth, through: YearMonth) {
        val count = monthsBetween(from, through) + 1
        if (count <= 0) return
        billDao.insertIfAbsent(List(count) { series.occurrenceIn(from.plusMonths(it)) })
    }

    private suspend fun transaction(block: suspend () -> Unit) {
        database.useWriterConnection { connection ->
            connection.immediateTransaction { block() }
        }
    }
}

private fun BillSeriesEntity.occurrenceIn(month: YearMonth) = BillEntity(
    name = name,
    expectedAmountCents = expectedAmountCents,
    dueDate = month.dayClamped(dayOfMonth),
    seriesId = id,
    seriesMonth = month,
)

private fun BillEntity.toDomain() = Bill(
    id = id,
    name = name,
    expectedAmountCents = expectedAmountCents,
    dueDate = dueDate,
    paidAmountCents = paidAmountCents,
    paidDate = paidDate,
    seriesId = seriesId,
)
