# Conventions — reusable UI components

Reference for the `reusable-ui-component` skill. The canonical files are
`components/productcarousel/` and `components/productfilter/`.

## Naming

| Thing | Rule | Example |
| --- | --- | --- |
| Package | lowercase, one word, under `components/` | `components/productfilter` |
| Model file | `<Component>Data.kt` or `<Item>.kt` | `CardCarouselData.kt`, `ItemFilterData.kt` |
| View file | `<Component>Sections.kt` (plural) | `ProductFilterSections.kt` |
| Entry composable | `internal fun <Component>Section(...)` | `ProductFilterSection` |
| Flat row entry | `internal fun <Component>Row(...)` | `CategoryRow` |
| Private item | `<Component>Card` / `<Component>Chip` / `<Item>Card` | `ProductFilterChip`, `ProductCard` |
| Header | `<Component>Header` | `CarouselHeader` |
| Icon enum | nested `IconType` in the model | `ItemFilterData.IconType` |
| Preview | `private fun <Component>SectionPreview()` | `ProductFilterSectionPreview` |
| String key | `ren_ecomme_<component>_<purpose>` | `ren_ecomme_carousel_view_all_label` |

## Entry-point signature

```kotlin
@Composable
internal fun <Component>Section(
    data: <Component>Data,          // 1. data/state
    selectedKey: String?,          // 2. optional selection state
    onItemClick: (String) -> Unit, // 3. callbacks, named onX, ids only
    modifier: Modifier = Modifier, // 4. modifier last, always defaulted
)
```

- Callbacks carry **ids/keys**, never models, `NavController` or `ViewModel`.
- Boolean state flags are named as facts (`selected`, `isRefreshing`), not commands.
- Do not add default values to required data; only optional flags get defaults.
- Pass `modifier` through to the root layout of the entry composable.

## Visibility and state

- Entry point `internal`; everything else in the file `private`.
- No `remember`/`MutableState` for business state. UI-only ephemeral state (for example an
  expanded flag) may use `rememberSaveable` and must be documented in the component KDoc.
- Selection, refresh, pagination and visibility are **hoisted**: the caller passes values and
  receives callbacks.
- No `LocalContext`/`Toast`/`NavController`/repository inside a component.

## Styling tokens

| Visual | Token |
| --- | --- |
| Selected background | `MaterialTheme.colorScheme.primary` |
| Selected foreground | `MaterialTheme.colorScheme.onPrimary` |
| Unselected background | `MaterialTheme.colorScheme.surface` |
| Unselected foreground | `MaterialTheme.colorScheme.onSurface` |
| Border | `MaterialTheme.colorScheme.outlineVariant` |
| Accent text/link | `MaterialTheme.colorScheme.primary` |
| Image fallback | `ColorPainter(MaterialTheme.colorScheme.onSurfaceVariant)` |
| Text | `MaterialTheme.typography.*` only |

No `Color.White`, `Color(0x...)`, `BorderStroke(..., Color(...))`, raw `fontSize` or raw `sp`.
Brand colors live in `ui/theme/Color.kt` and reach components through the theme.

## Lists

```kotlin
LazyRow(
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
) {
    items(data.items, key = { it.id }) { item -> /* ... */ }
}
```

- Always a stable `key` (API id or slug). Never the index.
- Keep the app rhythm: 16.dp side padding, 8.dp item spacing, 16.dp vertical section padding.
- Do not nest scrollables in the same direction.

## Interaction and accessibility

- Prefer `Surface(onClick = ...)` or `Modifier.selectable(...)` over bare `clickable`: it brings
  ripple, minimum touch target and role.
- Expose selection with semantics: `Modifier.semantics { this.selected = selected }` or
  `Modifier.selectable(selected, onClick, role = Role.Tab)`.
- A row that navigates as one unit: `Modifier.semantics(mergeDescendants = true) {}`.
- Decorative icon next to a label: `contentDescription = null`.
- Icon-only control: accept a `contentDescription` parameter and pass the string resource.
- Keep at least 48.dp touch targets for interactive items.

## Strings and images

- Fixed literals → `res/values/strings.xml` + `stringResource(R.string...)`.
- Data labels (`label`, `title`, `formattedPrice`) come pre-formatted in the model.
- `AsyncImage` with explicit size, `ContentScale.Crop`, and an `error` painter.
- Product images are decorative when a title is displayed → `contentDescription = null`.
- Constrain text: `maxLines = 1`, `TextOverflow.Ellipsis`.

## Previews

```kotlin
@Preview
@Composable
private fun <Component>SectionPreview() {
    RenEcommerceTheme(dynamicColor = false) {
        Surface {
            <Component>Section(/* fake data, no-op callbacks */)
        }
    }
}
```

- One preview per entry point, `private`, fake data, no network/Room.
- Include the variants that change rendering (selected/unselected, icon/no icon).
- Previews must compile; never reference real repositories.

## Formatting

- Follow the existing file layout: imports, KDoc, entry composable, private composables, helpers,
  preview at the bottom.
- Use the `MaterialTheme.colorScheme` / `MaterialTheme.typography` imports the canonical files use.
- No unused imports; run a build before reporting.
