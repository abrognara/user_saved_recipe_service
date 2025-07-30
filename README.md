# User Saved Recipe Service

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

## API Specifications

### Base URL
```
http://localhost:8081/api/recipes
```

### Endpoints

#### 1. Create Folder
**POST** `/folders`

Creates a new recipe folder for the user.

**Request Body:**
```json
{
  "folderName": "Breakfast",
  "createdByUser": "user-123",
  "creationTimestamp": 1703123456789
}
```

**Response:**
```json
"Breakfast"
```

#### 2. Get All Folders
**GET** `/folders`

Retrieves all recipe folders for the user.

**Response:**
```json
[
  {
    "folderName": "Breakfast",
    "createdByUser": "user-123",
    "creationTimestamp": 1703123456789,
    "savedRecipes": []
  },
  {
    "folderName": "Lunch",
    "createdByUser": "user-123",
    "creationTimestamp": 1703123456790,
    "savedRecipes": [
      {
        "recipeName": "Pasta Carbonara"
      }
    ]
  }
]
```

#### 3. Add Recipe to Folder
**POST** `/folders/{folderName}/recipes`

Adds a recipe to a specific folder.

**Path Parameters:**
- `folderName` (string): Name of the folder

**Request Body:**
```json
{
  "recipeName": "Pasta Carbonara"
}
```

**Response:**
```json
"Success"
```

#### 4. Delete Recipe from Folder
**DELETE** `/folders/{folderName}/recipes/{recipeName}`

Removes a recipe from a specific folder.

**Path Parameters:**
- `folderName` (string): Name of the folder
- `recipeName` (string): Name of the recipe to delete

**Response:**
```json
"Success"
```

#### 5. Delete Folder
**DELETE** `/folders/{folderName}`

Deletes a folder and all its recipes.

**Path Parameters:**
- `folderName` (string): Name of the folder to delete

**Response:**
```json
"Success"
```

### Error Responses

All endpoints may return the following error responses:

**400 Bad Request** - Invalid request format
**404 Not Found** - Folder or recipe not found
**409 Conflict** - Folder already exists
**500 Internal Server Error** - Server error

### Example Usage

#### Create a folder and add recipes:

```bash
# Create a folder
curl -X POST http://localhost:8081/api/recipes/folders \
  -H "Content-Type: application/json" \
  -d '{
    "folderName": "Italian Food",
    "createdByUser": "user-123",
    "creationTimestamp": 1703123456789
  }'

# Add a recipe to the folder
curl -X POST http://localhost:8081/api/recipes/folders/Italian%20Food/recipes \
  -H "Content-Type: application/json" \
  -d '{
    "recipeName": "Pasta Carbonara"
  }'

# Get all folders
curl -X GET http://localhost:8081/api/recipes/folders

# Delete a recipe
curl -X DELETE http://localhost:8081/api/recipes/folders/Italian%20Food/recipes/Pasta%20Carbonara

# Delete the folder
curl -X DELETE http://localhost:8081/api/recipes/folders/Italian%20Food
```

### Notes

- All endpoints use the mock user ID "user-123"
- Folder names are case-sensitive
- Recipe names are case-sensitive
- The service uses in-memory storage (data is lost on restart) 