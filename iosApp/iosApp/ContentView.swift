import ComposeApp
import SwiftUI
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(datePickerPresenter: SwiftDatePickerPresenter())
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ZStack {
            Color(red: 44 / 255, green: 32 / 255, blue: 54 / 255)
                .ignoresSafeArea()
            ComposeView()
                .ignoresSafeArea()
        }
    }
}
