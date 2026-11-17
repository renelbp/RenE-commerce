# One-time bootstrap: shared `RemoteResult`

> Status: already applied in this project (`products` and `categories` use the shared type).
> Keep this document as history; do not repeat the migration.

New data sources use a shared result type instead of a sealed interface per feature. Do this once,
before creating the first new data source, and only if the file does not exist yet:

```text
app/src/main/java/com/reneprojects/core/common/remote/RemoteResult.kt
```

## 1. Create the type

```kotlin
package com.reneprojects.core.common.remote

sealed interface RemoteResult<out T> {
    object NotModified : RemoteResult<Nothing>
    data class Success<T>(val data: T, val eTag: String?) : RemoteResult<T>
    data class Error(val code: Int, val message: String) : RemoteResult<Nothing>
}
```

## 2. Migrate the products data source

In `core/feature/products/remote/datasource/ProductsRemoteDataSource.kt`:

- Change the return type to `RemoteResult<List<ProductDto>>`.
- `ProductsRemoteResult.NotModified` → `RemoteResult.NotModified`.
- `ProductsRemoteResult.Success(products = ..., eTag = ...)` → `RemoteResult.Success(data = ..., eTag = ...)`.
- `ProductsRemoteResult.Error(...)` → `RemoteResult.Error(...)`.
- Delete the `ProductsRemoteResult` sealed interface and its imports.
- Delegate the HTTP handling to `safeRemoteCall` (`core/common/remote/SafeRemoteCall.kt`), which
  maps `304`/errors and rethrows `CancellationException`.

In `core/feature/products/repository/ProductRepository.kt`:

- Update imports and `when` branches to `is RemoteResult.NotModified`, `is RemoteResult.Success`,
  `is RemoteResult.Error`.
- Read the payload with `remoteResult.data` instead of `remoteResult.products`.

Nothing else references the old type: `ProductsInteractor` and the ViewModel only see
`ProductRepository`.

## 3. Verify

```sh
grep -rn "ProductsRemoteResult" app/src
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

The `grep` must return no results before continuing with the new feature.
