package app.debitum

import android.app.Application
import app.debitum.di.initKoin
import org.koin.android.ext.koin.androidContext

class DebitumApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DebitumApplication)
        }
    }
}
