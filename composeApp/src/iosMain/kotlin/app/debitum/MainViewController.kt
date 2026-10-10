package app.debitum

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import app.debitum.di.initKoin
import app.debitum.ui.components.IosDatePickerPresenter
import app.debitum.ui.components.LocalIosDatePickerPresenter

fun MainViewController(datePickerPresenter: IosDatePickerPresenter) = ComposeUIViewController(
    configure = { initKoin() },
) {
    CompositionLocalProvider(LocalIosDatePickerPresenter provides datePickerPresenter) {
        App()
    }
}
