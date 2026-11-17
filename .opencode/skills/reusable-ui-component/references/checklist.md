# Checklist — reusable UI component

Ordered verification for the `reusable-ui-component` skill. Stop and ask if an input is missing.

## Before writing

- [ ] Inputs confirmed: `{{component}}`, `{{Component}}`, identity field, display fields,
      callbacks, selection state, icons, fixed labels, empty behavior.
- [ ] Read `components/productcarousel/` and `components/productfilter/` (model + view).
- [ ] Decided the shape: header + horizontal list (`Section.kt.tmpl`) or flat selectable row
      (`SelectableRow.kt.tmpl`).

## Model

- [ ] File under `components/{{component}}/model/`.
- [ ] `internal data class`, `val` only, nested `Item`/`Header` when exclusive to the section.
- [ ] No `Dto`, `Entity`, repository, interactor, Compose, `R`, `Color` or `Painter` references.
- [ ] Stable identity field present and documented.
- [ ] Icons modelled as an `enum class IconType`; no drawable ids or colors in the model.
- [ ] Nullability reflects rendering (`iconType = null` → text only).

## View

- [ ] File under `components/{{component}}/view/`, entry `internal fun {{Component}}Section(...)`.
- [ ] Parameter order: data → state → callbacks → `modifier: Modifier = Modifier` last.
- [ ] Root applies `modifier`.
- [ ] No ViewModel, repository, `NavController`, `LocalContext`, `Toast` or navigation.
- [ ] Private children named after the thing (`{{Component}}Card`, `{{Component}}Chip`).
- [ ] `LazyRow`/`LazyColumn` with `key`, 16.dp `contentPadding`, 8.dp spacing.
- [ ] Every color from `MaterialTheme.colorScheme`; grep the file for `Color(` and `Color.White`
      before finishing.
- [ ] Typography from `MaterialTheme.typography`; no raw `fontSize`/`sp`.
- [ ] Fixed literals via `stringResource`; new keys added to `res/values/strings.xml` with the
      `ren_ecomme_` prefix.
- [ ] Click targets use `Surface(onClick)`/`selectable`; grouped rows merge semantics.
- [ ] Selection exposed through semantics (`selected`), not only color.
- [ ] `AsyncImage` has explicit size, `ContentScale.Crop` and an `error` painter.
- [ ] Constrained text has `maxLines` + `TextOverflow.Ellipsis`.
- [ ] Unknown `IconType` maps to `null` instead of crashing.

## Preview

- [ ] Exactly one `@Preview` per entry point, `private`, in the same file.
- [ ] Wrapped in `RenEcommerceTheme(dynamicColor = false) { Surface { ... } }`.
- [ ] Fake data only; no repositories, network or Room.
- [ ] Covers the variants that change rendering (selected/unselected, icon/none).

## Verification and handoff

- [ ] `JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew :app:assembleDebug`
      passes.
- [ ] Do not run/fix `:app:lintDebug` (blocked by a pre-existing androidTest BOM issue).
- [ ] No changes under `feature/`, `core/`, `build/`, `.gradle/`, `.idea/` or `local.properties`.
- [ ] No new dependencies.
- [ ] Handoff reported: which model to map, which section to add to `LandingPageData`, which
      callbacks to wire in the screen/ViewModel.
- [ ] `MEMORY.md` updated only with durable decisions.
