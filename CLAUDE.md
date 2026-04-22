# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn compile
mvn clean package -DskipTests

# Run tests
mvn test

# Run locally (requires env vars — see profiles below)
mvn spring-boot:run

# Docker
docker build -t user-saved-recipe-service .
docker run -p 8081:8081 user-saved-recipe-service
```

## Architecture

Spring Boot 3.5.3 + WebFlux (reactive) service on port 8081. All service methods return `Mono<T>`. JPA calls are blocking — they must be wrapped with `PgReactiveUtils.wrapMono()` (which runs them on `Schedulers.boundedElastic()`) to avoid blocking the event loop.

### Spring Profiles

The codebase has two mutually exclusive data backends controlled by a Spring profile:

| Profile | Backend | Key env vars |
|---|---|---|
| _(default)_ | PostgreSQL via Supabase/JPA | `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` |
| `dynamo` | AWS DynamoDB | `DYNAMO_REGION`, `DYNAMO_ENDPOINT` (optional, for local), `DYNAMO_USERS_TABLE`, `DYNAMO_RECIPES_TABLE`, `DYNAMO_USER_LISTS_TABLE` |

The `dynamo` profile excludes all JPA/datasource auto-configuration. All Supabase/JPA services are annotated `@Profile("!dynamo")`; all DynamoDB services are annotated `@Profile("dynamo")`.

### Service Layer Pattern

Each domain interface (`UserService`, `UserSavedRecipeService`, `RecipeService`) has two implementations:
- `Supabase*` — JPA-backed, uses Spring Data repositories
- `Dynamo*` — DynamoDB Enhanced Client-backed, uses plain `dynamo/repository/` classes

`RecipeDeduplicationFilter` / `DynamoRecipeDeduplicationFilter` normalize recipe URLs (strips query params and fragments, lowercases) before persisting, preventing duplicate recipe entries across users.

### Authentication

All endpoints require `X-User-Id` (internal user UUID) and `X-User-Roles` headers. Identity token validation and resolution to an internal UUID is handled by an upstream gateway — this service trusts `X-User-Id` as the authoritative user identity and uses it directly.

For the Supabase profile, `UserService.getUserById(UUID)` loads the full `User` entity (needed for JPA relationships and `displayName` in `UserListDto.createdByUser`). For the dynamo profile, no user table lookup occurs — a stub `User` is constructed with only the UUID set, so `createdByUser` will be null in list responses.

### DynamoDB Table Design

- **Recipes** — PK: `id` (UUID); GSI `urlIndex` on `url` (used for deduplication)
- **UserSavedRecipes** — PK: `userId` (internal UUID), SK: `listId` (UUID); GSI `listIdIndex` on `listId` (used for sharing lookups); `listName` is a regular attribute; `recipeIds` is an embedded StringSet. No join table. Set mutations use low-level `UpdateExpression ADD/DELETE` (via `DynamoDbClient`, not the enhanced client) for atomic set operations.

`listId` is a UUID generated at creation time. Path variables like `/lists/{listId}` use this UUID under the `dynamo` profile. `listName` uniqueness per user is enforced at the service layer (not by the key schema). `listName` is validated on creation: `^[a-zA-Z0-9 _\-]+$`, max 100 chars.

The `listIdIndex` GSI enables resolving any list by UUID without knowing the owner's `userId` — this supports sharing flows where a recipient is given a list UUID.
