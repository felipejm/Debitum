package app.debitum.di

import app.debitum.data.local.DebitumDatabase
import app.debitum.data.repository.RoomBillRepository
import app.debitum.domain.repository.BillRepository
import app.debitum.ui.bills.BillsViewModel
import app.debitum.ui.settings.SettingsViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect val platformModule: Module

val dataModule = module {
    single<BillRepository> { RoomBillRepository(get<DebitumDatabase>()) }
}

val viewModelModule = module {
    viewModelOf(::BillsViewModel)
    viewModelOf(::SettingsViewModel)
}

val appModules: List<Module> = listOf(platformModule, dataModule, viewModelModule)

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
