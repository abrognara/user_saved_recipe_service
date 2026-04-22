# User Saved Recipe Service

## Running Locally with DynamoDB

### Prerequisites
- Docker
- AWS CLI (`brew install awscli`)

### 1. Start DynamoDB Local

```bash
docker compose up -d
```

### 2. Create tables

```bash
AWS_ACCESS_KEY_ID=local AWS_SECRET_ACCESS_KEY=local ./scripts/setup-local-dynamo.sh
```

### 3. Run the service

```bash
AWS_ACCESS_KEY_ID=local AWS_SECRET_ACCESS_KEY=local \
  SPRING_PROFILES_ACTIVE=dynamo,local \
  mvn spring-boot:run

> DynamoDB Local runs on port **8001** (port 8000 is commonly used by other local services).
```

The service will be available at http://localhost:8081

### Example requests

All endpoints require `X-User-Id` (a UUID) and `X-User-Roles` headers.

```bash
# Create a list — response is the list name; use GET /lists to retrieve the listId UUID
curl -X POST http://localhost:8081/api/v1/lists \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000001" \
  -H "X-User-Roles: user" \
  -d '{"listName": "Italian Food", "isPublic": false}'

# Get all lists — each list includes its listId UUID
curl http://localhost:8081/api/v1/lists \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000001" \
  -H "X-User-Roles: user"

# Add a recipe to a list — {listId} is the UUID from the list response
curl -X PUT "http://localhost:8081/api/v1/lists/{listId}/saved" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000001" \
  -H "X-User-Roles: user" \
  -d '{"url": "https://example.com/pasta-carbonara", "title": "Pasta Carbonara"}'

# Get saved recipes in a list
curl "http://localhost:8081/api/v1/lists/{listId}/saved" \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000001" \
  -H "X-User-Roles: user"

# Delete a recipe from a list
curl -X DELETE "http://localhost:8081/api/v1/lists/{listId}/saved/{recipeId}" \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000001" \
  -H "X-User-Roles: user"

# Delete a list
curl -X DELETE "http://localhost:8081/api/v1/lists/{listId}" \
  -H "X-User-Id: 00000000-0000-0000-0000-000000000001" \
  -H "X-User-Roles: user"
```

---

## Docker

### Build the Docker image

```
docker build -t user-saved-recipe-service .
```

### Run the Docker container

```
docker run -p 8081:8081 user-saved-recipe-service
```

The service will be available at http://localhost:8081

---

## API Specifications

### Base URL
```
http://localhost:8081/api/v1
```

### Authentication

All endpoints require the following headers set by the upstream gateway:

| Header | Description |
|---|---|
| `X-User-Id` | Internal user UUID |
| `X-User-Roles` | User roles (e.g. `user`) |

### User List Endpoints

#### Create a list
**POST** `/lists`

**Request body:**
```json
{
  "listName": "Italian Food",
  "isPublic": false
}
```

`listName` must match `^[a-zA-Z0-9 _\-]+$`, max 100 characters. Names are unique per user (case-insensitive).

**Response:** the list name as a string.

---

#### Get all lists for the user
**GET** `/lists`

**Response:**
```json
[
  {
    "listId": "a1b2c3d4-...",
    "listName": "Italian Food",
    "createdByUser": "Jane",
    "isPublic": false,
    "creationTimestamp": 1703123456789
  }
]
```

`listId` is the UUID to use in all subsequent requests for this list.

---

#### Add a recipe to a list
**PUT** `/lists/{listId}/saved`

Saves a recipe to the list. If the recipe URL has been seen before (normalized, case-insensitive), the existing recipe record is reused rather than creating a duplicate.

**Request body:**
```json
{
  "url": "https://example.com/pasta-carbonara",
  "title": "Pasta Carbonara",
  "description": "A classic Roman pasta dish.",
  "author": "Marcella Hazan",
  "prep_time": "15 minutes",
  "cook_time": "20 minutes",
  "total_time": "35 minutes",
  "servings": "4",
  "rating_average": 4.8,
  "rating_count": 312,
  "keywords": ["pasta", "italian", "quick"],
  "ingredient_groups": [
    {
      "group_name": null,
      "ingredients": ["200g spaghetti", "100g guanciale", "2 eggs"]
    }
  ],
  "instruction_groups": [
    {
      "group_name": null,
      "steps": ["Boil pasta.", "Fry guanciale.", "Mix eggs with cheese."]
    }
  ],
  "notes": "Use Pecorino Romano for best results."
}
```

**Response:** the recipe name as a string.

---

#### Get saved recipes in a list
**GET** `/lists/{listId}/saved`

**Response:** array of recipe objects (same shape as the add recipe request body, plus `id`).

---

#### Delete a recipe from a list
**DELETE** `/lists/{listId}/saved/{recipeId}`

**Response:** the deleted `recipeId` as a string.

---

#### Delete a list
**DELETE** `/lists/{listId}`

**Response:** the deleted `listId` as a string.

---

### Error Responses

| Status | Meaning |
|---|---|
| 400 | Invalid request body or list name format |
| 404 | List or recipe not found |
| 409 | List with that name already exists for this user |
| 500 | Server error |
