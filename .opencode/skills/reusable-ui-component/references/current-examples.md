# Current examples → rules

How the canonical files map to this skill, and which parts are **legacy** and must not be copied
into new components.

## `productcarousel` — the closest match to the templates

Files:

- `components/productcarousel/model/CardCarouselData.kt`
- `components/productcarousel/view/CarouselSections.kt`

Matches:

- `internal data class CardCarouselData` with nested `CarouselHeader`, `ItemCard` and
  `enum class IconType` — presentation-only, no Android/Compose types.
- Entry `CarouselSection(section, navigateToProductCategory, navigateToProductDetails)` is
  stateless; navigation is expressed as `(Int) -> Unit` callbacks.
- Private parts in the same file: `CarouselHeader`, `HeaderTitleSection`, `ViewAllSection`,
  `ProductCard`.
- Theme tokens: `colorScheme.primary/secondary/tertiary`, `typography.titleLarge`, surfaces,
  `CardDefaults`.
- Coil `AsyncImage` with `ContentScale.Crop` and `ColorPainter(colorScheme.onSurfaceVariant)`.
- `@Preview` wrapped in `RenEcommerceTheme(dynamicColor = false) { Surface { ... } }`.
- Fixed label from `stringResource(R.string.ren_ecomme_carousel_view_all_label)`.
- `semantics(mergeDescendants = true)` on the header and on the "view all" tap target.

Deviations (do better in new code):

| Deviation | Why it matters | New rule |
| --- | --- | --- |
| No `modifier: Modifier = Modifier` on the entry composable | Callers cannot adjust padding/width or apply sticky offsets | Accept and apply `modifier` last |
| `@Composable private fun getIconAndColor(...): Pair<Int, Color>` | A mapping function as Composable hides theme reads | Map the drawable in a plain function; take the color from `MaterialTheme` at the call site or a private `@Composable` that returns a small data holder |
| `fun ProductCarouselsPreview()` is public | Leaks test-only API | Previews are `private` |
| `Pair<Int, Color>` as a pseudo-model | `Color` is a Compose type; keeping it in a Pair is hard to read | Use named private fields or read tokens directly |
| Header label capitalized in the view (`replaceFirstChar`) | Presentation formatting hides a data contract | Capitalize in the mapper/model; the view renders the label as-is |

## `productfilter` — good model, legacy styling

Files:

- `components/productfilter/model/ItemFilterData.kt`
- `components/productfilter/view/ProductFilterSections.kt`

Matches:

- Flat presentation model `ItemFilterData(slug, label, iconType)` with a nullable `IconType`;
  `null` renders a text-only chip ("Others").
- Identity `slug` is used as the LazyRow `key` and in `onFilterSelected`.
- Icon mapping lives in the view (`getFilterIcon`), not in the model.
- `maxLines = 1` + `TextOverflow.Ellipsis`.
- `@Preview` with the theme and all the variants.

Deviations (**do not copy**):

| Deviation | Why it matters | New rule |
| --- | --- | --- |
| `Color.White`, `Color(0xFFFF1F1F)`, `Color(0xFFE1E5EA)`, `Color(0xFF1D2433)` | Ignores dark theme and the brand tokens; the colors drift from `ui/theme` | Use `colorScheme.primary/onPrimary/surface/onSurface/outlineVariant` |
| `Modifier.clickable` on `Surface` | Loses the Surface ripple/role and makes the selected state invisible to a11y | `Surface(onClick = ...)` + `Modifier.semantics { selected = ... }` |
| No `modifier` parameter on the entry composable | Cannot be embedded in a sticky header without wrapping | Accept `modifier` last |
| `modifier.background(MaterialTheme.colorScheme.background)` inside the section | The caller (`stickyHeader` Surface) already paints the background; double painting breaks translucency | Let the parent own backgrounds unless the component is meant to be sticky |
| Selected tint duplicated on icon and text | Two sources of truth for the same state | Set `contentColor` on `Surface` and let `LocalContentColor` tint both |
| Hardcoded `"Others"` label in the mapper | Not the component's concern, but it bypasses `strings.xml` | Fixed labels belong to resources; data labels come from the model |

## Integration handoff (out of scope, but mention it)

After creating a component, the feature layer still needs:

1. A mapper function from domain/entity data to the component model (for example in
   `ProductMapper`).
2. A new entry in the screen's section model (`LandingPageData` or equivalent).
3. Rendering in the screen (`ProductsSection`) with `selected...` and callbacks wired to the
   ViewModel.
4. Hoisted state for selection toggles; the component only reports the tapped id.

Do not perform these steps inside the component task; report them instead.
