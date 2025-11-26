# Pokédex – Spring Boot + Angular

Full‑stack Pokédex built with a Spring Boot backend and an Angular frontend.  
The backend wraps the public PokéAPI and adds caching, while the frontend provides a Pokédex‑style experience with search, random picks, and rich detail views. [attached_file:1]

---

## Screenshots

### Pokédex grid

<img width="1920" height="1020" alt="image" src="https://github.com/user-attachments/assets/db7f093e-e49a-4633-97bd-87cc602b82dd" />


### Pokémon detail

<img width="1920" height="1020" alt="image" src="https://github.com/user-attachments/assets/b958f721-9a4a-46e4-8ec7-e10c0a2e8697" />


---

## Features

- Search Pokémon by **name or id**.
- Rich details from PokéAPI `/pokemon/{id or name}`:
  - Official artwork and sprite.
  - Types, base stats, abilities, height and weight.
  - Sample of moves (truncated for readability). [attached_file:1]
- **Backend caching** using Caffeine (LRU) to speed up repeated lookups and reduce calls to PokéAPI. [attached_file:6][attached_file:1]
- Angular UI inspired by the official Pokédex:
  - Card grid with id, name, artwork, type badges.
  - Detail view with large artwork, blue info panel, stat bars, and weaknesses.
  - **Surprise Me!** button that jumps to a random Pokémon.
  - Random Pokémon grid on initial load.

---

## Tech Stack

**Backend (pokedex-backend)**

- Java 17+, Spring Boot.
- Spring WebFlux `WebClient` to call PokéAPI. [attached_file:6][attached_file:1]
- Spring Cache + Caffeine for in‑memory caching. [attached_file:6]
- Centralized REST error handling via `RestExceptionHandler`. [attached_file:5]

**Frontend (pokedex-ui)**

- Angular 19 (standalone components + SSR‑ready). [web:30]
- Angular `HttpClient` (with `withFetch()` for SSR compatibility). [web:39][web:70]
- Responsive layout with pure CSS.

---

## Backend Overview

**Main endpoint**

- `GET /api/pokemon/{nameOrId}`  
  Returns a compact `PokemonDto`:

  - `id`, `name`, `height`, `weight`
  - `types: string[]`
  - `spriteFront`, `artwork`
  - `stats: { [key: string]: number }`
  - `abilities: string[]`
  - `moves: string[]` (limited sample)
  - `cached: boolean`, `fetchedAt: string` [attached_file:2][attached_file:7]

All data is fetched from `https://pokeapi.co/api/v2/pokemon/{id or name}` and then mapped into this DTO. [attached_file:1][attached_file:2]

**Caching**

- `PokemonService.getPokemonByName` is annotated with `@Cacheable("pokemon")`. [attached_file:2]
- Caffeine cache configured via `CacheConfig` with max size and TTL controlled from `application.properties`. [attached_file:6]
- This follows PokéAPI’s recommendation to cache responses locally and avoid unnecessary traffic. [attached_file:1]

**Configuration**

`src/main/resources/application.properties`:
spring.application.name=pokedex-backend
server.port=8080

Cache config
cache.pokemon.ttl-seconds=3600
cache.pokemon.max-size=500

Vendor (PokéAPI) config
pokeapi.base-url=https://pokeapi.co/api/v2
pokeapi.timeout-ms=5000


**Run backend locally**
cd pokedex-backend
mvn clean package # or ./mvnw clean package
java -jar target/pokedex-backend-*.jar

API base URL: `http://localhost:8080/api/pokemon`.

---

## Frontend Overview

The Angular app calls only the Spring Boot backend.

**Service**

`src/app/pokemon.service.ts`:

- `getPokemonByName(nameOrId: string)` → `GET http://localhost:8080/api/pokemon/{nameOrId}` and returns `PokemonDto`.

**AppComponent behaviour**

- On startup:
  - Picks a set of random Pokémon ids (configurable `gridSize` and `maxPokemonId`).
  - Loads them via the backend and displays them as cards.
- Search:
  - User types a name or id, hits Enter / Search → fetches from backend and opens the detail view.
- Card click:
  - Clicking a card sets it as `selected` and scrolls to the detail panel.
- Surprise Me:
  - Picks a random id within `maxPokemonId`, loads via backend, shows in grid and opens detail.

**UI layout**

- Top bar with Poké Ball logo and “Pokédex” title.
- Centered search bar and Search button.
- Toolbar:
  - **Surprise Me!** button.
  - Sort dropdown (Lowest Number, Highest Number, Name A–Z).
- Grid section:
  - Responsive grid of cards with id, title‑cased name, artwork, and type chips.
- Detail section:
  - Left: large official artwork.
  - Center: blue info card (height, weight, abilities, types, cache info).
  - Right / bottom: stat bars and type / weaknesses cards, mimicking the official Charizard Pokédex layout. [web:131]

**Run frontend locally**
cd pokedex-ui
npm install
ng serve

Open `http://localhost:4200` in a browser.

---

## Running Full Stack

1. Start backend:
cd pokedex-backend
mvn clean package
java -jar target/pokedex-backend-*.jar


2. Start frontend:
cd pokedex-ui
npm install
ng serve


3. Visit `http://localhost:4200`:
- A random set of Pokémon appears on the home page.
- Use the search bar, click any card, or hit **Surprise Me!** to explore.

---

## Notes

- Pokémon data and images come from **PokéAPI** and the underlying Pokémon assets; they belong to their respective rights holders. [attached_file:1]
- The project is for educational/demo purposes and respects PokéAPI’s fair‑use guidelines by caching responses and avoiding excessive request rates. [attached_file:1]
