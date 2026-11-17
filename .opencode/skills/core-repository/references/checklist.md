# Checklist

Copy this list into the task notes and tick items as you go.

## Before writing code

- [ ] Endpoint path and HTTP method confirmed.
- [ ] Response shape confirmed: wrapped list, bare list, or single object.
- [ ] JSON field names, types and nullability confirmed.
- [ ] `{{Feature}}`, `{{Entity}}`, table name and cache key chosen.
- [ ] Cache TTL chosen (default 30 minutes).
- [ ] `RemoteResult` exists, or the bootstrap in `remote-result-migration.md` was applied.

## Data layer

- [ ] `{{EntityName}}Dto` / `{{Feature}}ResponseDto` created.
- [ ] Endpoint function added to the shared `RenEcommerceApiService` with the `If-None-Match`
      header.
- [ ] `{{Feature}}RemoteDataSource` + `Impl` + Hilt module created; the implementation delegates
      to `safeRemoteCall` (which rethrows `CancellationException`).
- [ ] `{{Entity}}` created with a non-null `@PrimaryKey`.
- [ ] `{{EntityName}}Dao` created with `observe`, `get`, `insert`, `delete` and transactional
      `replace`.
- [ ] `{{Entity}}Mapper` + `Impl` + Hilt module created with null-safe defaults.
- [ ] `CacheKeys.{{CACHE_KEY}}` and `CachePolicy.{{CACHE_KEY}}_TTL` added.
- [ ] `{{Feature}}Repository` + `Impl` + Hilt module created (colocated).

## Wiring

- [ ] `NetworkModule` untouched; the shared service already exists.
- [ ] `DatabaseModule`: `@Provides` for `{{EntityName}}Dao` and `.addMigrations(...)`.
- [ ] `RenEcommerceDatabase`: entity added, `version` bumped, DAO accessor added.
- [ ] `DatabaseMigrations.MIGRATION_{{from}}_{{to}}` created with SQL matching the entity.
- [ ] Generated `app/schemas/.../{{to}}.json` matches the migration SQL.

## Tests

- [ ] `{{Feature}}RemoteDataSourceTest`: 304, success with ETag, null body, server error, thrown
      exception.
- [ ] `{{Feature}}RepositoryTest`: valid cache, forced refresh, 304, success, error.

## Verification

- [ ] `./gradlew :app:testDebugUnitTest --tests "...{{Feature}}RepositoryTest"`
- [ ] `./gradlew :app:testDebugUnitTest --tests "...{{Feature}}RemoteDataSourceTest"`
- [ ] `./gradlew :app:assembleDebug`
- [ ] `./gradlew :app:lintDebug`
- [ ] `MEMORY.md` updated (status, decisions, learnings).
- [ ] Report to the user which feature-layer pieces are still missing.
