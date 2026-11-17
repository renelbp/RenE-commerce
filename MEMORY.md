# MEMORY.md — RenEcommerce
Project memory across sessions. About 50 lines max: summarize or remove what is no
longer useful.
## Current status
- Android app (Kotlin, Jetpack Compose, Material 3) with a working product catalog:
  remote API + Room cache + home screen (`HomePageActivity`).
- Landing page: fixed chip row from `GET /products/categories`. Tapping a chip enters the
  2-column grid through a shimmer skeleton (>= 350 ms) only when coming from the carousels;
  switching categories updates the grid in place. Tapping the active chip clears the filter.
  "Others" = categories outside the 4 supported.
- Grid cards: 4:5 image, height driven by content, badge below the title, both prices and
  rating (`components/discountbadge`, `components/productcard`).
- Carousel cards: 150 dp square image, badge over the image (top end), original price inline
  with the final one, rating below the price (hidden at 0).
- Empty-result UI is still pending: no message, just the chip row.
- Pending: splash, product details, empty state, login/register, cart, order flow.
## Decisions (and why)
- Layered architecture: UI → ViewModel (StateFlow) → interactor → repository → Retrofit/Room.
- Room-backed cache with TTL (`CacheManager`) and ETag; if the network fails, show local data.
- Hilt with interfaces + `*Impl` via `@Binds`; repositories use `Dispatchers.IO`.
- One shared `RenEcommerceApiService` and one `Retrofit`: new endpoints are functions.
- `safeRemoteCall` (`core/common/remote`) centralizes 304/ETag/errors for every data source.
- Two result types: `RemoteResult` (remote) and `RenEcommerceResult` (domain).
- Data layers follow the `core-repository` skill; components follow `reusable-ui-component`.
- One Room table per endpoint, manual migrations; DB version 2.
- Category chips: `CategorySlug` + `ProductMapper` (4 icon slugs + synthetic "Others").
- Filtering is client-side in `ProductsInteractor.observeProductListing`: combines Room with
  the selection, normalizes with `trim().lowercase(Locale.ROOT)`, "Others" = not in the 4,
  order by `id`. No endpoint per filter.
- Selection persists in `SavedStateHandle` (`"selected_category"`) and mirrors into
  `LandingUiState.selectedCategory`; cards live in `LandingUiState.filteredProducts`.
- Filter loading: the ViewModel only owns `LandingUiState.isFilterLoading` (data state); the
  350 ms minimum lives in `ProductsSection` with `rememberSaveable` so rotation does not replay
  the skeleton. Never inferred from an empty list.
- Prices: `ProductEntity.price` is the pre-discount price; final = `price * (1 - d/100)`.
  The badge shows `roundToInt()` and hides at 0. Both cards share `toDisplayPrices()`.
- Margins: grid `contentPadding` 16.dp horizontal; 8.dp between cards.
## Learnings and mistakes to avoid
- Room changes need schema + migration updates (`app/schemas`).
- Compose 1.12: `stickyHeader` exists in list and grid scopes, but the chip row is now a fixed
  header. Lazy states must live outside `AnimatedContent` or they restart on each transition.
- Material 3 `Card` does not clip its content: clip card images manually (top corners).
- `:app:lintDebug` fails pre-analysis: `ui-test-junit4` (androidTest) lacks a version because
  the Compose BOM is only in `implementation`. Pre-existing; assemble/tests unaffected.
- `ProductDao` has no `ORDER BY`: sort in Kotlin when order matters.
- Never nest lazy lists: filtered mode replaces the `LazyColumn` with the grid.
- Mapper output is covered through interactor tests using the real `ProductMapperImpl`.
- Mockito: use `anyList()`/`anyLong()`, never `any()` for non-null/primitive params.
- Do not commit `build/`, `.gradle/`, `.idea/` or `local.properties`; README may be stale.
## Next steps
- Decide and implement the empty-state UI for filtered categories.
- Finish splash and product details (card taps currently show a Toast).
- Add ViewModel tests (coverage is still minimal).
