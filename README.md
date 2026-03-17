# PetMedTracker

A native Android app for tracking pets and their medications. Users can manage pet profiles, add and remove medications (with optional voice notes), export a medication schedule to PDF, and share it.

---

## Summary

PetMedTracker is structured for clarity and maintainability: Clean Architecture with Proto domain models, Room with migrations, Compose UI, and Koin for DI. Design choices (voice note paths, PDF export in data layer, permission in UI, seed data) are documented above along with the main tradeoffs (dual schema, permission flow, error handling).

## Tools
- **Android Studio**
- **Cursor IDE + custom skills** - used to speed up development, by creating a scaffold with all necessary components and their links

## Architecture

The app follows **Clean Architecture** with clear separation of concerns across three layers. Dependency flow is inward: presentation depends on domain, domain is independent, and data implements domain contracts.

### Layer overview

| Layer | Role | Key components |
|-------|------|-----------------|
| **Presentation** | UI and user actions | Jetpack Compose screens, ViewModels, navigation |
| **Domain** | Business rules and use cases | Use cases, repository interfaces |
| **Data** | Persistence and I/O | Room DAOs/entities, repositories, mappers, helpers |

- **Presentation** does not depend on data types (Room entities, file paths). It uses **domain models** only (Proto-based `Pet` and `Medication`).
- **Domain** defines repository interfaces and use cases; it has no Android or framework dependencies.
- **Data** implements repositories using Room, Proto, and platform APIs (e.g. `VoiceNoteHelper`, `MedicationPdfExporter`).

## Project structure (high level)

```
app/src/main/java/com/example/petmedtracker/
├── PetMedTrackerApp.kt          # Application, Koin start, seed load
├── di/                          # Koin modules (data, domain, presentation)
├── data/
│   ├── local/                   # Room DB, entities, DAOs, migrations
│   ├── mapper/                  # Entity ↔ Proto mappers
│   ├── repository/               # Repository implementations
│   ├── seed/                     # JSON seed loader and DTOs
│   ├── pdf/                      # PDF export
│   └── voice/                    # VoiceNoteHelper (record/play/delete)
├── domain/
│   ├── repository/               # Repository interfaces
│   └── usecase/                  # Use cases (one class per action)
├── presentation/
│   ├── addmedication/            # Add medication screen + VM (with voice note)
│   ├── addpet/                   # Add pet screen + VM
│   ├── petdetail/                # Pet detail screen + VM (medications, delete, voice notes, PDF)
│   ├── petlist/                  # Pet list screen + VM
│   ├── navigation/               # NavGraph, routes
│   ├── theme/                    # Color, Type, Theme
│   └── common/                   # Shared UI (e.g. Species)
└── models/                       # Generated Proto (Pet, Medication) — build output
```

---

## Building and running

- **Min SDK:** 26
- **Target SDK:** 35
- **Build:** Use Android Studio or, from a directory that has a Gradle wrapper, run `./gradlew assembleDebug` (or `gradlew.bat` on Windows).

The app needs **RECORD_AUDIO** for voice notes; the first time the user taps record, the system permission prompt is shown.

---

### Dependency injection

**Koin** is used for DI with three modules:

- **dataModule** — Database, DAOs, repositories, seed loader, PDF exporter, `VoiceNoteHelper`.
- **domainModule** — Use cases (factories) that depend on repository interfaces.
- **presentationModule** — ViewModels (with `SavedStateHandle` from navigation where needed).

ViewModels receive use cases and helpers via constructor injection; Compose screens obtain ViewModels via `koinViewModel()` with route-scoped `NavBackStackEntry` so state is retained per destination.

### Data flow

1. **Read path:** UI → ViewModel → Use case → Repository → DAO → Room. Data is mapped from entities to Proto models before leaving the data layer.
2. **Write path:** User action → ViewModel → Use case → Repository → DAO; optional side effects (e.g. deleting a voice file before deleting a medication) are performed in the ViewModel or repository.
3. **Reactive updates:** Screens observe `StateFlow`/`SharedFlow` from ViewModels; list/detail screens use repository `Flow`s so medication list updates when data changes (e.g. after adding or removing a voice note).

---

## Design decisions

### Domain model: Protocol Buffers

- **Decision:** Use **Protobuf** (with `java_multiple_files` and Lite runtime) for the core domain models `Pet` and `Medication`. This could help _keep parity between Android and iOS apps._
- **Rationale:** Stable, versioned schema; good fit if the same models are shared with other clients or backends later. Generated Java classes are immutable and support builders (e.g. `toBuilder().setVoiceNotePath(path).build()` for partial updates).
- **Tradeoff:** Generated code is Java; Kotlin interop uses getters (e.g. `getVoiceNotePath()`) exposed as properties. Schema changes require regenerating code and, when persisted, a Room migration.

### Persistence: Room + migrations

- **Decision:** Room for SQLite with **explicit migrations** (no `fallbackToDestructiveMigration`). Current migrations: `MIGRATION_1_2` (pets birthday), `MIGRATION_2_3` (medications `voiceNotePath`).
- **Rationale:** Type-safe queries, Kotlin coroutines/Flow support, and controlled schema evolution for production.
- **Tradeoff:** Every new or changed column/table needs a migration and a DB version bump.

### UI: Jetpack Compose + single Activity

- **Decision:** Compose for all UI; a single `MainActivity` hosts a `NavHost` with route-based composables. No fragments.
- **Rationale:** Declarative UI, direct use of state from ViewModels, and simpler navigation and back-stack behavior.
- **Tradeoff:** No built-in fragment-style lifecycle for Compose destinations; ViewModel scoping is done explicitly via `NavBackStackEntry` and Koin.

### Navigation

- **Decision:** Navigation Compose with string routes and typed arguments (e.g. `pet_detail/{petId}`, `add_medication/{petId}`). ViewModels that need `petId` read it from `SavedStateHandle` provided by Koin `parametersOf(backStackEntry.savedStateHandle)`.
- **Rationale:** Type-safe arguments, testable ViewModels, and no navigation logic inside ViewModels (only callbacks like `onBack`, `onPetDeleted`).

### Theming

- **Decision:** Material 3 with a **custom light palette** (light gray, sky blue, tan, cream, soft green) plus a dark variant. Optional dynamic color on Android 12+.
- **Rationale:** Consistent, accessible contrast and a distinct “pet app” look without relying only on default Material themes.

### Voice notes

- **Decision:** Voice notes are stored as **file paths** in the medication model (`voice_note_path` in Proto, `voiceNotePath` in Room). Recording/playback/delete are handled by a **`VoiceNoteHelper`** that uses `MediaRecorder`/`MediaPlayer` and stores files in the app cache directory.
- **Rationale:** Keeps domain model simple (a string path); all platform/audio logic is in one place and testable in isolation. Files are tied to app lifecycle (cache can be cleared by the system).
- **Tradeoffs:**  
  - No sync or backup of audio files; if the user clears app data or uninstalls, voice notes are lost.  
  - Deleting a medication (or, optionally in the future, a pet) explicitly deletes the associated voice file to avoid orphaned files.

### PDF export and share

- **Decision:** A **`MedicationPdfExporter`** in the data layer generates a PDF from a pet and its medications (including text only; voice note is not embedded). The detail screen receives a `Uri` via a `SharedFlow` and launches the system share sheet.
- **Rationale:** Export is a side effect that doesn’t change domain state; keeping it in data allows reuse (e.g. from a worker or another screen later) and keeps ViewModels focused on state and one-off events.

### Seed data

- **Decision:** On first run, **`SeedDataLoader`** loads mocked pets and medications from JSON assets (`pets.json`, `medications.json`) into Room. “First run” is detected by checking for an existing pet (e.g. `pet-1`).
- **Rationale:** Demo content without a backend; loading is done once in `Application.onCreate()` on a coroutine scope so it doesn’t block the main thread.

---

## Engineering tradeoffs

- **Proto + Room:** Two representations (Proto for domain, Entity for DB) require mappers and migrations in two places when the schema grows. Accepted to keep a clear domain boundary and optional future multi-platform or API alignment.
- **No reactive updates for “recording” state:** Recording state (e.g. “which medication is recording”) is held in the ViewModel’s `StateFlow`; when the medications `Flow` re-emits after a voice note is saved, `recordingForMedicationId` is preserved in the Content state so the UI doesn’t flicker.
- **Permission handling in UI:** `RECORD_AUDIO` is requested from the Composable (via `rememberLauncherForActivityResult` and optional `checkSelfPermission`) and only after permission is granted is the ViewModel action (e.g. start recording) invoked. This keeps permission flow in the UI layer while business logic stays in the ViewModel.
- **Result vs exceptions:** Repositories return `Result<Unit>` for writes; ViewModels use `onSuccess`/`onFailure` and push error messages into UI state (e.g. `PetDetailUiState.Error(e.message)`). No global error handler; each screen decides how to show errors.
- **Koin viewModel parameters:** ViewModels that need `SavedStateHandle` (e.g. pet detail, add medication) are created with `parametersOf(navBackStackEntry.savedStateHandle)` so the correct handle is passed per route; the rest of the constructor is filled by Koin `get()`.

---

## Bonus features

- **Add/edit/delete pet**
- **Edit/Delete medication**
- **Calendar date picker for dates selection**

---

## Further improvements

- **Voice Recording:** Show play/pause state, maybe with a progress bar to select the exact time of the audio I want to jump to;
- **Pet avatar:** Add an option to save a profile picture for the pet;
- **Media attachments to medicine:** Add an option to save a picture of the medicine label, recipe, how to address the medication, etc;
- **Use of Google Calendar:** Implement integration with Google Calendar or any other Calendar app to add the reminders and duration of the medication, so pet owners can manage it easier;
- **Frequency/duration UI:** Implement a more robust UI for these two fields, instead of a open field that can lead to mistyping and so confusion; fixed select boxes to select whether is it daily, weekly, etc (like Google Keep);
- **Customized .pdf:** Build a customized UI for the .pdf, to include app logo, pet profile, medication attachments, etc;
---
