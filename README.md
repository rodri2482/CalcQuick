# CalcQuick

A fast, multi-mode calculator for Android built with **Jetpack Compose** and **Material 3**.

> Internal project name: *Apex Calc*

## Features

- **Standard & Scientific modes** — live expression preview as you type, DEG/RAD toggle, memory keys, factorial, powers, percent, implicit multiplication (`2(3)`, `2PI`)
- **Unit Converter** — length, weight, temperature, area, volume, speed, time, data storage, energy
- **Loan calculator** — monthly payment, total payment, total interest
- **Tip & Split** — bill, tip %, per-person totals
- **Discount + Tax** — final price and savings
- **BMI** — metric and imperial
- **History** — persisted across app restarts (Room), starred favorites, recall into the display
- **Themes** — Neon Cyber (dark), OLED Black, Clean Light, Midnight Blue
- Haptic feedback, safe-drawing edge-to-edge UI

## Tech stack

| Layer      | Choice                                        |
|------------|-----------------------------------------------|
| UI         | Jetpack Compose + Material 3                  |
| State      | MVVM (`StateFlow` + `AndroidViewModel`)       |
| Evaluation | Custom zero-dependency recursive-descent parser (no `eval`, no code-execution surface) |
| Storage    | Room (calculation history) · SharedPreferences (settings) |
| Build      | Kotlin 2.2, AGP 9, Gradle 9, KSP, R8-minified release |

## Building

Requirements: JDK 21, Android SDK 36.

```bash
gradle :app:assembleDebug           # debug build
gradle :app:testDebugUnitTest       # unit tests (Robolectric)
```

> If you use Android Studio, open the project and build normally. In CI,
> `gradle/actions/setup-gradle` with Gradle 9.3.1 is used because the wrapper
> JAR is not committed.

## Tests

`app/src/test/java/com/example/MathExpressionEvaluatorTest.kt` covers the
expression engine: precedence, division-by-zero, percent/factorial semantics,
degree/radian trigonometry, locale-safe decimal formatting, overflow, and the
paren-nesting crash guard.

## Security notes

- No network permissions; the release minified with R8.
- `.env` and keystores are gitignored; the `.env.example` placeholder key is
  intentionally left commented out — do not bake API keys into `BuildConfig`
  of a shipping APK (they are trivially extractable).
- See [SECURITY.md](SECURITY.md) for reporting issues.

## License

[MIT](LICENSE)
