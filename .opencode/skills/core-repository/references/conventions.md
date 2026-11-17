# Conventions

Everything below mirrors the existing `products` feature. If the code and this file disagree, the
code wins — and this file should be fixed.

## Placeholders

| Placeholder | Meaning | Example |
| --- | --- | --- |
| `{{feature}}` | package segment, lowercase plural | `categories` |
| `{{Feature}}` | PascalCase plural (service, datasource, repository) | `Categories` |
| `{{Entity}}` | PascalCase singular entity | `CategoryEntity` |
| `{{EntityName}}` | PascalCase singular without `Entity` | `Category` |
| `{{entity}}` | camelCase singular (constructor params) | `category` |
| `{{table}}` | Room table name, lowercase plural | `categories` |
| `{{endpoint}}` | path relative to `BASE_URL` | `categories` |
| `{{itemsField}}` | JSON field that wraps the list | `categories` |
| `{{CACHE_KEY}}` | constant name in `CacheKeys` / `CachePolicy` | `CATEGORIES` |
| `{{cacheKey}}` | cache key value | `categories:all` |
| `{{from}}` / `{{to}}` | Room version step | `1` / `2` |

## Naming

| Piece | Pattern |
| --- | --- |
| DTO item | `{{EntityName}}Dto` |
| DTO envelope | `{{Feature}}ResponseDto` |
| Endpoint function | `get{{Feature}}Response` in the shared `RenEcommerceApiService` |
| Remote result | shared `RemoteResult<T>` |
| Data source | `{{Feature}}RemoteDataSource` / `{{Feature}}RemoteDataSourceImpl` |
| Entity | `{{Entity}}` |
| DAO | `{{EntityName}}Dao` |
| Entity mapper | `{{Entity}}Mapper` / `{{Entity}}MapperImpl` |
| Repository | `{{Feature}}Repository` / `{{Feature}}RepositoryImpl` |
| Test | `{{Feature}}RemoteDataSourceTest`, `{{Feature}}RepositoryTest` |

## Visibility and style

- Repository interface: public, like `ProductRepository`.
- Everything else: `internal`, as in the products feature.
- Files keep their abstraction, implementation and Hilt module together with
  `/** region ... */` … `/** endregion ... */` markers and KDoc on public members.
- Modules are colocated with what they bind: `@Module @InstallIn(SingletonComponent::class)`,
  `@Binds` plus `@Singleton` for implementations, `@Provides` only in `core/di`.
- Constructor injection with `@Inject`; no manual singletons.

## Cache

- Key format: `"<feature-plural>:all"`, one key per list endpoint.
- TTL lives in `CachePolicy` and mirrors `PRODUCTS_TTL` (30 minutes) unless the data changes
  faster.
- `NotModified` refreshes the cache expiry with the **previous** ETag; `Success` stores the new one;
  `Error` never touches the cache or the database.

## Tests

- JUnit4 + Mockito + Robolectric (`@RunWith(RobolectricTestRunner::class)`,
  `@Config(manifest = Config.NONE)`), `runBlocking`, no Mockito-Kotlin.
- Test names read as `action_expectedBehavior_whenCondition`.
- Assert results with `is RenEcommerceResult.Success` / `is RemoteResult.Error` and
  `assertEquals` on the payload.

## Do not

- Add a second `Retrofit` or a new ApiService, or change `BASE_URL`. New endpoints are functions in
  `core/network/api/RenEcommerceApiService.kt`.
- Add pagination without approval.
- Put DTOs, entities or DAOs under `feature/`; they belong to `core/feature/`.
- Map domain/UI models here; that is the interactor's job.
- Skip the migration or the version bump when adding a table.
