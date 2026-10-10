import ComposeApp
import SwiftUI
import UIKit

final class SwiftDatePickerPresenter: IosDatePickerPresenter {
    private let calendar: Calendar = {
        var calendar = Calendar(identifier: .gregorian)
        calendar.locale = Locale(identifier: "pt_BR")
        return calendar
    }()

    func present(
        initialYear: Int32,
        initialMonth: Int32,
        initialDay: Int32,
        maxYear: Int32,
        maxMonth: Int32,
        maxDay: Int32,
        callback: IosDatePickerCallback
    ) {
        guard
            let initial = date(initialYear, initialMonth, initialDay),
            let presenter = UIApplication.shared.topViewController
        else { return }
        let maxDate = maxYear > 0 ? date(maxYear, maxMonth, maxDay) : nil

        var host: UIViewController?
        let sheet = DatePickerSheet(initial: initial, maxDate: maxDate, calendar: calendar) { [calendar] selected in
            if let selected {
                let parts = calendar.dateComponents([.year, .month, .day], from: selected)
                callback.onDateSelected(
                    year: Int32(parts.year!),
                    month: Int32(parts.month!),
                    day: Int32(parts.day!)
                )
            }
            host?.dismiss(animated: true)
        }
        let controller = UIHostingController(rootView: sheet)
        controller.sheetPresentationController?.detents = [.large()]
        controller.sheetPresentationController?.prefersGrabberVisible = true
        host = controller
        presenter.present(controller, animated: true)
    }

    private func date(_ year: Int32, _ month: Int32, _ day: Int32) -> Date? {
        calendar.date(from: DateComponents(year: Int(year), month: Int(month), day: Int(day)))
    }
}

private struct DatePickerSheet: View {
    @State private var selection: Date
    private let maxDate: Date?
    private let calendar: Calendar
    private let onFinish: (Date?) -> Void

    init(initial: Date, maxDate: Date?, calendar: Calendar, onFinish: @escaping (Date?) -> Void) {
        _selection = State(initialValue: initial)
        self.maxDate = maxDate
        self.calendar = calendar
        self.onFinish = onFinish
    }

    var body: some View {
        NavigationStack {
            Group {
                if let maxDate {
                    DatePicker("", selection: $selection, in: ...maxDate, displayedComponents: .date)
                } else {
                    DatePicker("", selection: $selection, displayedComponents: .date)
                }
            }
            .datePickerStyle(.graphical)
            .labelsHidden()
            .padding(.horizontal)
            .environment(\.locale, calendar.locale ?? .current)
            .environment(\.calendar, calendar)
            .tint(Color(red: 0xDD / 255, green: 0x78 / 255, blue: 0x5D / 255))
            .frame(maxHeight: .infinity, alignment: .top)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancelar") { onFinish(nil) }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("OK") { onFinish(selection) }
                }
            }
        }
    }
}

private extension UIApplication {
    var topViewController: UIViewController? {
        let window = connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap(\.windows)
            .first { $0.isKeyWindow }
        var top = window?.rootViewController
        while let presented = top?.presentedViewController {
            top = presented
        }
        return top
    }
}
