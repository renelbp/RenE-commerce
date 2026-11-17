---
name: Core Repository
description: Add a new core data layer for an API endpoint in RenEcommerce — DTOs, an endpoint function in the shared RenEcommerceApiService, remote data source, Room entity/DAO/migration, entity mapper, TTL+ETag cache, repository, Hilt wiring and JUnit/Mockito tests. Use when creating a new repository under app/src/main/java/com/reneprojects/core/feature/ that calls an endpoint of the existing API and persists the result in Room.
---

# Core Repository

Creates one vertical slice of the **data layer** for a new endpoint of the API already configured
in `NetworkModule` (`https://dummyjson.com/`).

## Scope

**In scope:** DTOs, a new function in the shared `RenEcommerceApiService`, remote data source (with
`RemoteResult`), Room entity, DAO, entity mapper, cache key + TTL, repository, Hilt modules,
database registration, migration, tests.

**Out of scope:** interactor, UI mapper, ViewModel, UI state, screens, navigation. Stop after the
repository layer and report what the feature layer still needs. Do not create a new Retrofit
instance or a new ApiService: the base URL is shared, so the endpoint is one more function in
`RenEcommerceApiService`.

## Inputs to collect first

Ask the user before writing code. If something is missing, do not guess.

| Input | Example | Notes |
| --- | --- | --- |
| Endpoint | `categories` | Relative to `BASE_URL`. Include `@Path`/`@Query` params if any. |
| Response shape | `{ "categories": [...] }` | Wrapped list, bare list, or single object. |
| Item fields | `slug`, `name`, `url` | JSON names, types, nullability. |
| List field | `categories` | JSON field that wraps the list, if the response is wrapped. |
| `{{Feature}}` | `Categories` | PascalCase plural: datasource, repository, response DTO. |
| `{{Entity}}` | `CategoryEntity` | PascalCase singular + `Entity`. |
| Cache TTL | 30 min | Mirror `PRODUCTS_TTL` unless the data changes faster. |

**Pagination is out of scope.** The default pattern downloads the full list and replaces the
table. If the endpoint is paginated (`limit`/`skip`/`total`), stop and confirm with the user.

## Canonical example — read before writing

These files are the source of truth. Match their structure, visibility and style:

- `app/src/main/java/com/reneprojects/core/feature/products/repository/ProductRepository.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/remote/datasource/ProductsRemoteDataSource.kt`
- `app/src/main/java/com/reneprojects/core/network/api/RenEcommerceApiService.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/local/dao/ProductDao.kt`
- `app/src/main/java/com/reneprojects/core/feature/products/mapper/ProductEntityMapper.kt`
- `../../../app/src/main/java/com/reneprojects/core/common/cachemanager/constants`
- `app/src/main/java/com/reneprojects/core/di/DatabaseModule.kt` and `NetworkModule.kt`
- `app/src/test/java/com/reneprojects/feature/products/domain/interactor/ProductsInteractorTest.kt`

## One-time bootstrap: shared `RemoteResult`

New data sources use the shared `com.reneprojects.core.common.remote.RemoteResult<T>` instead of a
per-feature sealed interface. Check whether it exists:

```text
app/src/main/java/com/reneprojects/core/common/remote/RemoteResult.kt
```

If it does not, follow `references/remote-result-migration.md` **before** creating the first new
data source. That guide also migrates the existing products data source, so the codebase only has
one result type.

## Package layout

Under `app/src/main/java/com/reneprojects/core/feature/{{feature}}/`:

```text
{{feature}}/
├── local/
│   ├── dao/{{EntityName}}Dao.kt
│   └── entity/{{Entity}}.kt
├── mapper/{{Entity}}Mapper.kt
├── remote/
│   ├── datasource/{{Feature}}RemoteDataSource.kt
│   └── dto/
│       ├── {{EntityName}}Dto.kt
│       └── {{Feature}}ResponseDto.kt
└── repository/{{Feature}}Repository.kt
```

Match the existing file split: entity and DAO each in their own file, while the mapper, data source
and repository bundle abstraction, implementation and Hilt module using the existing
`/** region ... */` … `/** endregion ... */` markers. Do not split those into extra files.

## Workflow

1. Confirm the inputs table above and read the canonical files.
2. Ensure `RemoteResult` exists (bootstrap step).
3. Create the DTOs from `templates/Dto.kt.tmpl` and `templates/ResponseDto.kt.tmpl`.
4. Add the endpoint function from `templates/ApiFunction.kt.tmpl` to the shared
   `core/network/api/RenEcommerceApiService.kt`.
5. Create the data source from `templates/RemoteDataSource.kt.tmpl`; delegate the HTTP handling
   to `core/common/remote/safeRemoteCall`.
6. Create the entity from `templates/Entity.kt.tmpl` and the DAO from `templates/Dao.kt.tmpl`
   (full list + transactional `replace`).
7. Create the mapper from `templates/EntityMapper.kt.tmpl`.
8. Add the cache key and TTL in `core/common/constants/CacheConstants.kt`.
9. Create the repository from `templates/Repository.kt.tmpl`.
10. Wire DI and the database:
    - `NetworkModule`: no change. It already provides the shared `RenEcommerceApiService`.
    - `DatabaseModule`: `@Provides` for `{{EntityName}}Dao` via `database.{{entity}}Dao()`.
    - `RenEcommerceDatabase`: add `{{Entity}}::class` to `entities`, add the abstract DAO
      accessor and bump `version`.
    - Add the migration (next section) and register it on the builder.
11. Create the tests from `templates/RemoteDataSourceTest.kt.tmpl` and
    `templates/RepositoryTest.kt.tmpl`, under
    `app/src/test/java/com/reneprojects/core/feature/{{feature}}/`.
12. Run the verification commands, then update `MEMORY.md` (see `AGENTS.md`).

## Wiring snippets

`core/network/api/RenEcommerceApiService.kt` — add the function, keep the existing ones:

```kotlin
@GET("{{endpoint}}")
suspend fun get{{Feature}}Response(
    @Header("If-None-Match") eTag: String? = null
): Response<{{Feature}}ResponseDto>
```

`core/di/DatabaseModule.kt` — DAO provider plus the migration:

```kotlin
@Provides
fun provide{{EntityName}}Dao(database: RenEcommerceDatabase): {{EntityName}}Dao =
    database.{{entity}}Dao()
```

```kotlin
return Room.databaseBuilder(...)
    .addMigrations(DatabaseMigrations.MIGRATION_{{from}}_{{to}})
    .build()
```

`core/database/RenEcommerceDatabase.kt`:

```kotlin
@Database(
    entities = [ProductEntity::class, CacheMetaDataEntity::class, {{Entity}}::class],
    version = {{to}},
    exportSchema = true
)
internal abstract class RenEcommerceDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cacheMetadataDao(): CacheMetadataDao
    abstract fun {{entity}}Dao(): {{EntityName}}Dao
}
```

`core/database/migrations/DatabaseMigrations.kt` (new file) — one migration per version step, so
several new tables added together share a single migration:

```kotlin
internal object DatabaseMigrations {
    val MIGRATION_{{from}}_{{to}} = object : Migration({{from}}, {{to}}) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `{{table}}` (
                    `id` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )
        }
    }
}
```

Room validates the schema at runtime: the SQL must match the entity exactly (nullability,
defaults, primary key). Build once with `./gradlew :app:assembleDebug`, then compare the SQL with
the generated `app/schemas/com.reneprojects.core.database.RenEcommerceDatabase/{{to}}.json`.
A mismatch crashes on first open, it is not a compile error.

`core/common/constants/CacheConstants.kt`:

```kotlin
internal object CacheKeys {
    const val PRODUCTS = "products:all"
    const val {{CACHE_KEY}} = "{{cacheKey}}"
}

internal object CachePolicy {
    val PRODUCTS_TTL = 30.minutes.inWholeMilliseconds
    val {{CACHE_KEY}}_TTL = 30.minutes.inWholeMilliseconds
}
```

## Tests

Follow the existing style: JUnit4 + Mockito + Robolectric, `runBlocking`, no Mockito-Kotlin.

- Data source: `304 → NotModified`, `2xx + body → Success(data, eTag)`, `2xx + null body → Error`,
  non-2xx → `Error(code, message)`, thrown exception → `Error(-1, ...)`. These cases exercise
  `safeRemoteCall` through the feature data source, so it does not need its own test file.
- Repository: valid cache → no remote call; forced refresh → remote call; `NotModified` →
  `updateCache` with the previous ETag and no DAO write; `Success` → mapper + `replace` +
  `updateCache` with the new ETag; `Error` → `RenEcommerceResult.Error` and no DAO write.

Build a `Retrofit` `Response` with `Response.success(body, Headers.headersOf("ETag", "\"abc\""))`
or `Response.error(500, "".toResponseBody(...))`. For `304` and a null body, use a mocked
`Response`: `Response.error` rejects 3xx codes.

## Verification

```sh
./gradlew :app:testDebugUnitTest --tests "com.reneprojects.core.feature.{{feature}}.repository.{{Feature}}RepositoryTest"
./gradlew :app:testDebugUnitTest --tests "com.reneprojects.core.feature.{{feature}}.remote.datasource.{{Feature}}RemoteDataSourceTest"
./gradlew :app:assembleDebug
./gradlew :app:lintDebug
```

## Rules

- Reuse the shared `Retrofit` and the shared `RenEcommerceApiService`; never create another
  instance of either, and never change `BASE_URL`.
- Keep the repository API shaped like `ProductRepository`: `observeX(): Flow<List<{{Entity}}>>` and
  `suspend fun loadXData(forceRefresh: Boolean = false): RenEcommerceResult<Unit>`.
- Cache is mandatory: TTL check, conditional request with the stored ETag, `304` handling.
- Run network/database work in `withContext(Dispatchers.IO)`.
- Keep `internal` visibility for implementations; the repository interface stays public like
  `ProductRepository`.
- Use `safeRemoteCall` in every data source: it maps HTTP concerns to `RemoteResult` and rethrows
  `CancellationException` before the generic catch.
- Do not touch the feature/UI layer, `build/`, `.gradle/`, `.idea/` or `local.properties`.
- Do not add dependencies.
- Every new table requires a version bump **and** a migration with matching SQL.

## Edge cases

- **Empty response body** → `RemoteResult.Error(response.code(), "Empty response body")`.
- **Missing ETag header** → `Success(data, eTag = null)`; still refresh the cache entry.
- **`304` with no local data** → return `Success(Unit)` and keep whatever Room has; never crash.
- **Server returns an empty list** → the transactional replace wipes the table. If that can happen,
  confirm with the user before syncing an empty payload.
- **Nullable JSON fields** → map with defaults (`.orEmpty()`, `?: 0`, `?: 0.0`) like
  `ProductEntityMapper`.
- **Single-object response** → not the default pattern; confirm the shape and adapt the DAO before
  continuing.
- **Table name** → lowercase plural, matching the entity; quote it in SQL with backticks.
- **Category or slug fields** → store the raw API value; normalization belongs to the domain layer.

See `references/checklist.md` for the ordered checklist and `references/conventions.md` for
naming.
