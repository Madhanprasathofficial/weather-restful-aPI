# Weather API

The Weather API is a Spring Boot application that provides real-time weather data for cities around the world using the [OpenWeatherMap API](https://openweathermap.org/api). It includes features like caching, rate limiting, API key management, and health checks to ensure reliability and scalability.

## Table of Contents
1. [Features](#features)
2. [API Endpoints](#api-endpoints)
3. [Setup Instructions](#setup-instructions)
4. [Dependencies](#dependencies)
5. [Error Handling](#error-handling)
6. [Testing](#testing)
7. [Swagger Documentation](#swagger-documentation)

---

## Features

### Core Functionality
- **Weather Data Retrieval**:
    - Fetches real-time weather data for a given city and country from the OpenWeatherMap API.
    - Implements caching to reduce redundant API calls (data is stored in an in-memory H2 database).

- **Rate Limiting**:
    - Enforces a limit of **5 requests per hour per API key** to prevent abuse.

- **API Key Management**:
    - Allows adding, deleting, listing, and validating API keys via dedicated endpoints.
    - Ensures that only valid API keys can access weather data.

- **Health Checks**:
    - Provides health check endpoints to verify the application's status and database connectivity.

### Additional Features
- **Error Handling**:
    - Gracefully handles invalid API keys, rate limit violations, and external API failures with meaningful error messages.

- **Swagger Integration**:
    - Provides interactive API documentation using **Swagger UI**.

---

## API Endpoints

### Weather Endpoints
- **GET `/api/weather/getWeather`**:
    - Retrieves weather data for a given city and country.
    - **Query Parameters**:
        - `city`: The name of the city (e.g., "London").
        - `country`: The country code (e.g., "UK").
        - `apiKey`: The API key for authentication.
    - **Response**:
      ```json
      {
        "description": "clear sky"
      }
      ```

- **GET `/api/weather/health`**:
    - Checks the health of the application and database.
    - **Response**:
      ```json
      {
        "status": "OK",
        "databaseHealthy": true
      }
      ```

### API Key Management Endpoints
- **POST `/api/key/add`**:
    - Adds a new API key.
    - **Query Parameters**:
        - `apiKey`: The API key to add.
    - **Response**:
      ```json
      "API key added successfully."
      ```

- **DELETE `/api/key/delete`**:
    - Deletes an existing API key.
    - **Query Parameters**:
        - `apiKey`: The API key to delete.
    - **Response**:
      ```json
      "API key deleted successfully."
      ```

- **GET `/api/key/list`**:
    - Lists all valid API keys.
    - **Response**:
      ```json
      ["key1", "key2", "key3"]
      ```

- **POST `/api/key/validate`**:
    - Validates an API key.
    - **Query Parameters**:
        - `apiKey`: The API key to validate.
    - **Response**:
      ```json
      {
        "isValid": true
      }
      ```

---

## Setup Instructions

### Prerequisites
**Java 17 or higher**: Ensure Java is installed (`java -version` to check).
**Gradle**: Install Gradle for building the project (`gradle -v` to check).
**OpenWeatherMap API Key**: Obtain a free API key from [OpenWeatherMap](https://openweathermap.org/api).
**List of keys** 
*   b2180c8ac8633b32549bb10ac4ca7730
*   e7dd890a480d1e9547cd9d92b2f803c7
*   5ceca6dbfe14418a07e12fc76ec7d1bb
*   147854e652b5b992ec688497963df829
*   bc6faa4243d1bf3acef6c4f5cd862c1f
* 
### Steps to Run the Application
1. **Clone the Repository**:
    ```
      git clone https://github.com/Madhanprasathofficial/weather-restful-aPI.git
       cd weather-restful-aPI
      ```
   git clone <repository-url>
   cd <project-directory>
2. **Build the Application :**
Use Gradle to clean and build the project:
```
   ./gradlew clean build
 ```
3. **Run the Application :**

You can start the server using one of the following methods:

Option 1: Using Gradle
Run the application with the bootRun task:
```
    ./gradlew bootRun
```
Option 2: Running the Main Class
Alternatively, you can run the main class directly:

```
   Run WeatherApiApplication
```
## Weather API Documentation

### GET /api/weather
**Description:** Retrieves weather data for a given city and country.

**Query Parameters:**
- `city` (string, required): The name of the city (e.g., "London").
- `country` (string, required): The country code (e.g., "UK").
- `apiKey` (string, required): The API key for authentication.

**Example Request:**
```bash
GET "http://localhost:8080/api/weather?city=London&country=UK&apiKey=b2180c8ac8633b32549bb10ac4ca7730"
```

**Response:**
```json
{
  "description": "clear sky"
}
```

---

## API Key Management Endpoints

### POST /api/key/add
**Description:** Adds a new API key.

**Query Parameters:**
- `apiKey` (string, required): The API key to add.

**Example Request:**
```bash
 POST "http://localhost:8080/api/key/add?apiKey=your_new_api_key"
```

**Response:**
```json
"API key added successfully."
```

---

### DELETE /api/key/delete
**Description:** Deletes an existing API key.

**Query Parameters:**
- `apiKey` (string, required): The API key to delete.

**Example Request:**
```bash
 DELETE "http://localhost:8080/api/key/delete?apiKey=your_api_key"
```

**Response:**
```json
"API key deleted successfully."
```

---

### GET /api/key/list
**Description:** Lists all valid API keys.

**Example Request:**
```bash
 GET "http://localhost:8080/api/key/list"
```

**Response:**
```json
["key1", "key2", "key3"]
```

---

### POST /api/key/validate
**Description:** Validates an API key.

**Query Parameters:**
- `apiKey` (string, required): The API key to validate.

**Example Request:**
```bash
POST "http://localhost:8080/api/key/validate?apiKey=your_api_key"
```

**Response:**
```json
{
  "valid": true
}
```
### Swagger API Docs
Access the complete API documentation here:
```bash
GET http://localhost:8080/v3/api-docs)
```