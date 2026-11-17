# AGENTS.md

Guidance for coding agents working in this repository.

## Project overview

- **App:** RenE-commerce (Android, single-module: `:app`)
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Build:** Gradle Kotlin DSL, version catalog (`gradle/libs.versions.toml`)
- **DI:** Hilt / Dagger
- **Data:** Retrofit 3 + Gson, Room, Coil 3, Coroutines/Flow
- **Min/Target/Compile SDK:** 24 / 37 / 37 (see `app/build.gradle.kts`)
- **Java/Kotlin target:** Java 11 bytecode (`compileOptions`)

## Common commands

Run from repo root. Use the Gradle wrapper:

- Build debug APK: `./gradlew :app:assembleDebug`
- Build release APK: `./gradlew :app:assembleRelease`
- Run local/JVM unit tests: `./gradlew :app:testDebugUnitTest`
- Run all unit tests (all variants): `./gradlew :app:test`
- Run instrumented tests (device/emulator required): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew :app:lintDebug` (or `./gradlew lint`)
- Clean: `./gradlew clean`

### Running a single test

Prefer targeted Gradle test filters instead of running the whole suite:

- Single test class:
  - `./gradlew :app:testDebugUnitTest --tests "com.reneprojects.feature.products.domain.interactor.ProductsInteractorTest"`
- Single test method:
  - `./gradlew :app:testDebugUnitTest --tests "com.reneprojects.feature.products.domain.interactor.ProductsInteractorTest.observeProductSections_groupsProductsByCategory_andPassesMappedIconTypes"`

Notes: instrumented tests live under `app/src/androidTest`; use `connectedDebugAndroidTest`/`connectedAndroidTest` for those (filtering support differs by task/setup).

## Architecture & code organization

Follow the existing layered/package structure under `app/src/main/java/com/reneprojects/`:

- `app/` — application entrypoints (`Application`, activities)
- `ui/theme/` — Compose theme/color/typography
- `components/<component>/{view,model,...}` — reusable UI components
- `feature/<feature>/...` — feature-level code (UI, viewmodel, domain/interactor, mappers, models)
- `core/` — shared infrastructure and data concerns:
  - `core/di/` — Hilt modules (network, database, etc.)
  - `core/network/` — network constants/config
  - `core/database/` — Room database
  - `core/common/` — shared utilities (result types, cache manager, constants)
  - `core/feature/<feature>/{repository,local,remote,mapper}` — data access per feature

Keep boundaries clear: UI observes ViewModel/UI state; domain/interactors coordinate business logic and mapping; repositories/data sources handle remote/local/cache details.

## Conventions & patterns

- **Hilt modules are often colocated** with the abstraction/implementation they bind (see `ProductRepositoryModule`/`ProductsInteractorModule` inside the same file, region-delimited). Follow local style before introducing new module locations.
- **Interfaces + Impl classes**: abstractions are typically exposed as interfaces with `*Impl` implementations injected via `@Binds` in `@InstallIn(SingletonComponent::class)` modules.
- **Visibility:** internal implementation types and feature-internal APIs are common (`internal class/interface`). Preserve visibility unless broader access is required.
- **Result/error handling:** use the existing result wrapper (`com.reneprojects.core.common.result.RenEcommerceResult`) and repository remote result types rather than ad-hoc error channels.
- **Remote data sources:** delegate HTTP handling to `safeRemoteCall` (`core/common/remote/`), which centralizes `304`, ETag extraction, empty bodies, HTTP errors and `CancellationException`. Each feature data source only declares its endpoint and how to extract the body.
- **Flows/coroutines:** repositories expose `Flow` for observable local data; use appropriate dispatchers (`Dispatchers.IO` for blocking/db/network coordination in repos, `flowOn(Dispatchers.Default)` for mapping where used).
- **Room:** schema export is configured to `app/schemas` via KSP arg in `app/build.gradle.kts` (`room.schemaLocation`). When changing entities/DAOs/database version, update schemas/migrations as required by Room.
- **Mappers:** keep DTO/entity/UI model conversions in dedicated mapper classes/modules.
- **Card pricing:** `ProductMapper` computes the final price, the struck-through original and the rounded discount once (`toDisplayPrices`); views only render them. Card components reuse `DiscountBadge`, and `ItemCard` / `ProductCardData` stay separate presentation models even when they share fields.
- **Landing filtering:** category chips filter products already stored in Room through `ProductsInteractor.observeProductListing` (no per-filter endpoint). The selection lives in the ViewModel (`SavedStateHandle`). Without a filter the screen is a `LazyColumn`; with a filter it swaps to a `LazyVerticalGrid(GridCells.Fixed(2))` whose full-span `stickyHeader` is the chip row.
- **Grids:** use `GridCells.Fixed(n)`, `Arrangement.spacedBy(8.dp)` for card separation and `contentPadding` (16.dp horizontal) for screen margins; cards must not add their own side padding and their height follows content (no fixed image height). One scrollable per direction: never nest the grid inside the `LazyColumn`.
- **Compose/UI:** prefer stateless composables and state hoisting patterns consistent with existing screens/components.

## Testing

- Frameworks in use: JUnit4, Mockito, Robolectric for local unit tests; Espresso/Compose UI test deps for androidTest.
- Existing unit tests live under `app/src/test/java/...` and may use `@RunWith(RobolectricTestRunner::class)` and Mockito mocks.
- When adding tests, match the style of nearby tests (package, naming, mocking approach, coroutine handling with `runBlocking`/flows as currently used).
- Mapper classes do not get dedicated unit tests: their output is covered through interactor tests that use the real `ProductMapperImpl` (as `ProductsInteractorTest` does).

## Repo-specific notes

- `README.md` contains a high-level architecture summary, roadmap, and structure diagram; treat source as the source of truth if paths/descriptions drift.
- No `.editorconfig`, lint config override, or CI workflow was found in-repo at initialization time; rely on default Android/Kotlin/Gradle tooling behavior unless the project adds them.
- Generated/build artifacts may appear locally (`build/`, `.gradle/`, `.idea/`); do not commit or modify them as part of feature work.
- `local.properties` is machine-specific (SDK path) — avoid changing it unless necessary for the local environment.

## Memory

- At the start of every session, read `MEMORY.md` to learn the project status and past decisions.
- When you finish a task, update `MEMORY.md`: current status, important decisions (with their reasons), and mistakes to avoid.
- Keep it brief (about 50 lines max): summarize or remove what is no longer useful.
- If something becomes a permanent rule, propose moving it to `AGENTS.md` instead of keeping it in memory.
- Never store sensitive data (keys, tokens, personal data).

## Skills

- `.opencode/skills/core-repository` documents how to add the data layer for a new endpoint: a function in the shared `RenEcommerceApiService`, DTOs, remote data source, Room entity/DAO/migration, TTL+ETag cache, repository, Hilt wiring and tests.
- `.opencode/skills/reusable-ui-component` documents how to add a reusable Compose component under `components/<name>/{model,view}`: presentation model, stateless entry composable, private items, theme tokens, preview and feature-layer handoff.
- Load the matching skill (`@core-repository`, `@reusable-ui-component`) before writing that code. Skills, `AGENTS.md` and `MEMORY.md` are local-only and ignored by git.
## Límits
- ✅ Always: respect  rules, update `MEMORY.md` when every task is completed.
- ⚠️ Ask before : create new files,change the format of the saved data.
- 🚫 Never: add dependencies, frameworks or a build step.