---
name: Reusable UI Component
description: Create or refactor a reusable, stateless Jetpack Compose component in RenEcommerce under app/src/main/java/com/reneprojects/components/<component>/{model,view} — presentation model, entry section composable, private item composables, theme-token styling, stable list keys, @Preview and a feature-layer handoff. Use when adding a UI component shared across screens, or when extracting a section/row/card/chip into components/.
---

# Reusable UI Component

Builds one **self-contained, stateless Compose component** in
`app/src/main/java/com/reneprojects/components/`, split into a presentation model (`model/`) and
Composables (`view/`).

The canonical examples are:

- `app/src/main/java/com/reneprojects/components/productcarousel/model/CardCarouselData.kt`
- `app/src/main/java/com/reneprojects/components/productcarousel/view/CarouselSections.kt`
- `app/src/main/java/com/reneprojects/components/productfilter/model/ItemFilterData.kt`
- `app/src/main/java/com/reneprojects/components/productfilter/view/ProductFilterSections.kt`

Read them before writing. Match their structure (package split, `internal` entry point, private
sub-composables, `IconType` enum, `@Preview` in the same file), but follow this skill where they
deviate (see `references/current-examples.md`).

## Scope

**In scope:** package layout, presentation model(s), entry composable, private item composables,
theme-token styling, accessibility semantics, string resources for fixed labels, `@Preview`,
compilation check, and the handoff notes for the feature layer.

**Out of scope:** feature integration (`LandingPageData`, `ProductMapper`, interactor,
`ProductsSection`), ViewModels, UI state, navigation, data layer, and tests. Do not edit files under
`feature/` or `core/`; end with a short handoff describing what the caller must do.

## Inputs to collect first

Ask the user before writing code. If an input is missing, do not guess.

| Input | Example | Notes |
| --- | --- | --- |
| `{{component}}` | `productfilter` | Lowercase folder/package name under `components/`. |
| `{{Component}}` | `ProductFilter` | PascalCase name; entry is `{{Component}}Section`. |
| `{{Item}}` | `ItemFilterData` | Item data class, only if the component renders a list. |
| Identity | `id: Int` / `slug: String` | Stable value used as the list key and in callbacks. |
| Display fields | `label`, `formattedPrice`, `imageUrl` | **Already formatted** strings; no domain logic. |
| Interactions | `onFilterSelected: (String?) -> Unit` | Callbacks receive ids, never composables or models. |
| Selection | `selectedSlug: String?` | `null` means "nothing selected". |
| Icons | furniture, beauty, … | Enum variants; the view maps them to drawables/colors. |
| Fixed labels | "View All" | User-visible literals that go to `res/values/strings.xml`. |
| Empty behavior | hide the section | Decide: caller hides it, or the composable renders nothing. |

## Package layout

```text
components/{{component}}/
├── model/
│   └── {{Component}}Data.kt        # section model (+ nested Item/Header/enum)
└── view/
    └── {{Component}}Sections.kt    # entry composable + private parts + @Preview
```

One model file per component. File name plural (`...Sections.kt`) and entry singular
(`{{Component}}Section`), like the existing components. Do not create one file per private helper.

## Workflow

1. Confirm the inputs table and read the canonical files.
2. Create the model from `templates/SectionModel.kt.tmpl` (list with header) or
   `templates/ItemModel.kt.tmpl` (flat, selectable list such as chips).
3. If the design needs a fixed literal, add it to `app/src/main/res/values/strings.xml` using the
   existing `ren_ecomme_*` prefix.
4. Create the view from `templates/Section.kt.tmpl` (header + horizontal cards) or
   `templates/SelectableRow.kt.tmpl` (single-selection chips). Keep private composables, icon
   mapping and the `@Preview` in the same file.
5. Review the checklist in `references/checklist.md`.
6. Compile:
   ```sh
   JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" \
     ./gradlew :app:assembleDebug
   ```
   `:app:lintDebug` is currently blocked by a pre-existing androidTest dependency issue; do not
   "fix" it as part of a component task.
7. Update `MEMORY.md` only with durable decisions (not the component internals) and report the
   feature-layer handoff.

## Model rules

- Presentation-only: no `Dto`, `Entity`, repository or interactor types, and no `android.*`,
  Compose types, `R`, `Color` or `Painter` references. Formatting happens before the model.
- `internal data class` with `val` properties only. Nested `Item`/`Header` types live inside the
  section class when they are only used by it (as in `CardCarouselData`).
- Icons are an `enum class IconType` in the model; the drawable and color mapping stays in the
  view. Never store a drawable id or `Color` in the model.
- Nullable means "the view renders less": `iconType: String? = null` → text-only chip.
- Keep one identity field (`id`, `slug`, `key`) for list keys and callbacks. Do not use the list
  index.
- No default values for required display data; defaults are acceptable for optional flags
  (`selected`, `iconType`).

## View rules

- Entry point: `internal fun {{Component}}Section(...)`. Same package as the file.
- Parameter order: data/state first, callbacks (`onX`), then `modifier: Modifier = Modifier` last.
- Apply `modifier` to the root of the entry composable: `Column(modifier = modifier...)`,
  `LazyRow(modifier = modifier...)`. Private leaves may omit it.
- Stateless: no ViewModel, repository, `NavController`, `LocalContext`, `Toast` or navigation.
  Report interactions through callbacks with ids. Toggle logic belongs to the caller.
- Private composables: `private fun` in the same file, named for the thing (`ProductCard`,
  `ProductFilterChip`, `CarouselHeader`), not `Item1`/`Child`.
- Lists: `LazyRow`/`LazyColumn` with `key = { it.id }`, `contentPadding =
  PaddingValues(horizontal = 16.dp)` and `Arrangement.spacedBy(8.dp)` to match the app's rhythm.
- Colors: **only** `MaterialTheme.colorScheme.*` (and `LocalContentColor.current` inside a
  `Surface`). Never `Color.White`, `Color(0x...)` or a raw `BorderStroke` color literal. Border →
  `colorScheme.outlineVariant`; selected → `primary`/`onPrimary`; surfaces → `surface`.
- Typography: only `MaterialTheme.typography.*`. Do not set raw `fontSize`/`sp`.
- Shape: `RoundedCornerShape` from the component's design (chips use 28.dp, icon badges 16.dp).
- Strings: fixed user-visible text via `stringResource(R.string...)`; labels come from the model.
  Never hardcode a display literal in a composable.
- Icons: decorative when the text already names the action → `contentDescription = null`. If the
  icon is the only content, accept a `contentDescription` parameter.
- Click targets: prefer `Surface(onClick = ..., shape = ..., color = ..., border = ...)` or
  `Modifier.selectable(...)` over `clickable` so ripple/role/toggle state come for free. For a
  multi-element clickable row, add `Modifier.semantics(mergeDescendants = true) {}`.
- Selected state: expose it through semantics (`Modifier.semantics { this.selected = selected }`
  or `selectable`), not only through color.
- Images: Coil `AsyncImage`, explicit size, `contentScale = ContentScale.Crop`, and
  `error = ColorPainter(MaterialTheme.colorScheme.onSurfaceVariant)`.
- Constrained text: `maxLines = 1` + `TextOverflow.Ellipsis`.
- Preview: exactly one per entry point, `private`, wrapped in
  `RenEcommerceTheme(dynamicColor = false) { Surface { ... } }`, with fake data and no-op
  callbacks. Cover the interesting variants (selected/unselected, icon/null icon).
- Separate sections with vertical padding (`Modifier.padding(vertical = 16.dp)`); let the parent
  decide backgrounds unless the component is designed to be sticky.

## Do / Don't

```kotlin
// Do
Surface(
    onClick = onClick,
    shape = RoundedCornerShape(28.dp),
    color = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface,
    border = if (selected) null
    else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.semantics { this.selected = selected },
) { /* content */ }

// Don't
Surface(
    modifier = Modifier.clickable(onClick = onClick),
    color = Color.White,
    border = BorderStroke(1.dp, Color(0xFFE1E5EA)),
) { /* content */ }
```

```kotlin
// Do — caller owns the toggle
onFilterSelected = viewModel::onFilterSelected

// Don't — component mutates selection state itself
if (selected) { selectedKey = null } else { selectedKey = key }
```

## Edge cases

- **Empty list** → the section renders nothing; prefer letting the caller hide it (as the landing
  page does with missing categories) rather than emitting an empty header.
- **Unknown icon variant** → map to `null` in the view and render the text only; never crash or
  fall back to a random drawable.
- **`null` selection** → no chip is highlighted. A tap always reports the tapped id; the caller
  decides whether it toggles off.
- **Long labels** → one line + ellipsis; never let a chip grow the row height.
- **Broken image URL** → `ColorPainter(colorScheme.onSurfaceVariant)` placeholder, fixed size so
  the layout does not jump.
- **Accessibility** → a selected chip must expose `selected = true`; a row that navigates as a unit
  must merge its descendants.
- **RTL/long lists** → rely on `LazyRow` and `Arrangement.spacedBy`; do not position with
  absolute offsets.

## Rules

- One component = one package under `components/`, with `model/` and `view/`.
- Entry composable is `internal`; everything else in the file is `private`.
- Stateless and hoisted: no state mutation, no ViewModel, no navigation, no Context reads.
- Theme tokens only; no literal colors, font sizes or dp for text.
- Every list item needs a stable `key`; every callback passes the item's identity.
- Every entry point needs a `@Preview` and must compile with `:app:assembleDebug`.
- Do not add dependencies, do not touch `feature/`, `core/`, `build/`, `.gradle/`, `.idea/` or
  `local.properties`.
- Report the handoff: model to map, `LandingPageData` entry to add, and callbacks to wire.

See `references/conventions.md` for the naming/parameter reference, `references/checklist.md` for
the ordered verification list, and `references/current-examples.md` for how the canonical files map
to these rules (including what not to copy).
