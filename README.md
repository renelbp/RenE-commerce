# RenE-commerce

RenE-commerce is an Android application developed in Kotlin using Jetpack Compose for the UI layer. The app presents a product catalog fetched from a remote API, caches the response locally with Room, and applies a lightweight cache policy to minimize redundant network calls while improving perceived performance.

This project is designed to demonstrate a modern Android architecture pattern that balances maintainability, scalability, and production-oriented engineering practices.

## Project Summary

The application follows a layered, modularized architecture built around the following ideas:

- UI is implemented with Jetpack Compose and Material 3
- State is managed through ViewModel + StateFlow
- Business logic is isolated in an interactor layer
- Data access is abstracted through repositories
- Remote data is fetched with Retrofit
- Local persistence is handled with Room
- Dependency injection is managed using Hilt
- Product data is cached using a custom Room-backed cache manager

## Technical Stack

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Lifecycle
- Hilt / Dagger
- Retrofit 3
- Gson Converter
- Room
- Coil 3
- Coroutines + Flow
- Gradle Kotlin DSL

## Architecture

The codebase is organized into clear layers to separate responsibilities:

### 1. UI Layer
The UI layer is composed of `@Composable` screens and reusable components such as product cards and catalog sections.

Relevant files:
- `app/src/main/java/com/reneprojects/app/HomePageActivity.kt`
- `app/src/main/java/com/reneprojects/feature/products/ui/ProductsPage.kt`
- `app/src/main/java/com/reneprojects/feature/products/ui/ProductsSection.kt`

### 2. Presentation Layer
UI state is exposed through a `ProductViewModel` and managed with `StateFlow`.

Relevant files:
- `app/src/main/java/com/reneprojects/feature/products/viewmodel/ProductViewModel.kt`
- `app/src/main/java/com/reneprojects/feature/products/model/ProductUiState.kt`
- `app/src/main/java/com/reneprojects/feature/products/model/ProductUiModel.kt`

The ViewModel is responsible for:
- listening to product updates from the interactor
- triggering the initial loading flow
- handling refresh and retry actions
- updating UI state for loading, errors, and empty states

### 3. Domain / Interactor Layer
The interactor layer sits between the UI and data access, converting repository data into UI-friendly models.

Relevant files:
- `app/src/main/java/com/reneprojects/feature/products/interactor/GetProductsInteractor.kt`
- `app/src/main/java/com/reneprojects/feature/products/mapper/ProductUiMapper.kt`

This layer keeps UI components decoupled from persistence and network implementation details.

### 4. Repository Layer
The repository handles fetching data from the API and writing it to Room, while exposing flows for observing local product data.

Relevant file:
- `app/src/main/java/com/reneprojects/core/feature/products/repository/ProductRepository.kt`

Key responsibilities:
- validate cache status
- perform conditional requests using server ETag metadata
- handle 304 Not Modified responses
- replace the local product dataset with the latest successful result
- keep observable product data available to the UI through Room Flow APIs

### 5. Remote Data Layer
API communication is implemented with Retrofit and a typed service interface.

Relevant files:
- `app/src/main/java/com/reneprojects/core/feature/products/remote/api/ProductsApiService.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/remote/dto/ProductDto.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/remote/dto/ProductsResponseDto.kt`

The app consumes a product endpoint and maps the network response into Room entities through mapper logic.

### 6. Persistence Layer
Room is used for local storage and caching.

Relevant files:
- `app/src/main/java/com/reneprojects/core/database/RenEcommerceDatabase.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/local/entity/ProductEntity.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/local/dao/ProductDao.kt`

Data design:
- `products` table stores product metadata and values needed for the catalog UI
- `ProductDao` includes flow-based read operations and transactional replacement operations
- database schema is generated with Room and stored under the app `schemas` directory

### 7. Cache Management
A custom cache manager provides TTL-based freshness checks and stores metadata such as expiration timestamps and ETags.

Relevant files:
- `app/src/main/java/com/reneprojects/core/common/cachemanager/manager/CacheManager.kt`
- `app/src/main/java/com/reneprojects/core/common/cachemanager/dao/CacheMetadataDao.kt`
- `app/src/main/java/com/reneprojects/core/common/cachemanager/entity/CacheMetaDataEntity.kt`
- `app/src/main/java/com/reneprojects/core/common/cachemanager/model/CacheStatus.kt`

This allows the app to avoid unnecessary fetches while still being able to refresh data when the cache expires or when the user explicitly requests a refresh.

## Dependency Injection

Hilt is used to provide application-scoped dependencies for the network layer, database, repository, mapper, and interactor modules.

Relevant files:
- `app/src/main/java/com/reneprojects/core/di/NetworkModule.kt`
- `app/src/main/java/com/reneprojects/core/di/DatabaseModule.kt`

This approach reduces manual wiring, keeps the architecture modular, and makes the app easier to extend with additional features.

## UI Behavior

The current implementation focuses on a product catalog experience with:

- a Compose-based product list screen
- asynchronous image loading through Coil
- gracefully handled loading and error states
- a refresh mechanism to reload product data
- clean card styling using Material 3 components

## Performance and Reliability Considerations

Several production-oriented implementation decisions are included in this project:

- Room-backed caching reduces repeated network usage
- Flow-based observation keeps the UI synchronized with local data changes
- ETag-aware API requests reduce unnecessary payload transfer
- Local data is preserved when network requests fail, preventing a blank UI if cached content already exists
- UI loading state is derived from actual data availability rather than fixed placeholders

## Project Structure

```text
RenEcommerce/
├── app/
│   ├── schemas/
│   ├── src/
│   │   ├── androidTest/
│   │   ├── main/
│   │   │   ├── java/com/reneprojects/
│   │   │   │   ├── app/
│   │   │   │   ├── core/
│   │   │   │   ├── feature/
│   │   │   │   └── ui/
│   │   │   └── res/
│   │   └── test/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── gradle.properties
├── .gitignore
└── README.md
```

## Build and Run

### Prerequisites

- Android Studio
- JDK 11+
- Android SDK configured for API 24+

### Commands

```bash
./gradlew assembleDebug
```

Then run the app in Android Studio using an emulator or physical device.

## Code Quality Notes

The project uses standard Android engineering practices, including:

- strong separation between data, domain, and UI concerns
- explicit DI modules for network and database dependencies
- reusable data mapper patterns
- immutable UI state models
- coroutine-friendly asynchronous flows

## Notable Design Decisions

- Compose was chosen to simplify the UI layer and reduce boilerplate compared with XML-based views
- Room was used because the app benefits from both remote data fetches and local persistence
- Hilt was selected to keep dependency wiring clean and consistent with Android app architecture
- Cache validation was implemented to support both freshness checks and offline resilience

## Limitations

This project is currently focused on the product catalog flow and does not yet include advanced commerce features such as:

- cart management
- user authentication
- checkout flow
- product detail navigation
- search and filtering
- payment integration

## Conclusion

RenE-commerce is a modern Android project that demonstrates a structured, maintainable, and production-aware approach to mobile application development. It combines Jetpack Compose, Hilt, Retrofit, Room, and Flow-based state management to create a clean architecture that is easy to extend and suitable for real-world product development.

This project highlights my ability to build scalable mobile experiences with thoughtful architecture and practical engineering decisions.

## Contact

- GitHub: [Your GitHub Profile]
- LinkedIn: [Your LinkedIn Profile]
- Email: [Your Email]
