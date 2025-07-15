# Qantas Code Challenge - Airport API

This project is a Spring Boot application that provides a RESTful API for querying airport information. It fetches data from an external Qantas API, transforms it into a simplified format, and allows users to filter the results.

## Design Decisions

### 1. Architecture: Controller-Service-Client
The application follows a standard three-layered architecture to ensure separation of concerns:
- **Controller (`AirportController`, `AirportAdminController`)**: Handles incoming HTTP requests, validates input (implicitly via Spring), and delegates to the service layer. It is responsible for the API contract.
- **Service (`AirportService`)**: Contains the core business logic. This includes data transformation, filtering, and managing the in-memory cache.
- **Client (`QantasApiClient`)**: Responsible for all communication with the external Qantas API. This isolates external dependencies, making the application easier to test and maintain.

### 2. Data Handling: In-Memory Cache on Startup
To meet the requirement to "prevent frequent calls to the external airports API," the application employs an in-memory caching strategy.
- **Loading**: Using the `@PostConstruct` annotation on a method in `AirportService`, the application fetches the entire list of airports from the external API exactly once when the application starts.
- **Storage**: The transformed, simplified airport data is stored in a `List<AirportDto>`. A `CopyOnWriteArrayList` is used for thread safety, which is suitable for read-heavy workloads like this one.
- **Trade-offs**: This approach is extremely fast for read operations as it avoids network latency. The main trade-offs are that the data is only as fresh as the last application restart, and the memory footprint grows with the size of the airport dataset. For this use case, these trade-offs are acceptable.

### 3. Data Transformation and Resilience
Two sets of Data Transfer Objects (DTOs) are used to decouple our API from the external source. The transformation logic in `AirportService` is written defensively to handle inconsistencies in the source data (e.g., missing or empty nested objects like `state`), preventing `NullPointerException`s and ensuring stable operation.

### 4. Optional Features
- **Partial Name Matching**: Implemented in the `AirportService` using `String.toLowerCase().contains()`, allowing for case-insensitive partial matches on the airport name.
- **CRUD Endpoints**: A separate `AirportAdminController` has been created to demonstrate the design of RESTful CRUD endpoints. It uses standard HTTP verbs (`POST`, `GET`, `PUT`, `DELETE`) and returns appropriate HTTP status codes (`201`, `200`, `204`, `404`). These endpoints are non-functional placeholders.

## Assumptions
- The external Qantas API (`https://api.qantas.com/flight/refData/airport`) is available and its JSON structure is stable.
- The entire airport dataset is small enough to fit comfortably in the application's memory.
- The airport data does not need to be updated in real-time; a refresh on application restart is sufficient.

## How to Run the Project

### Prerequisites
- Java 17 or later
- Apache Maven 3.6 or later

### Build
Navigate to the project's root directory and run the following Maven command to build the project and run tests:
```bash
mvn clean install
```

### Run
Once the project is built, you can run the application using:
```bash
java -jar target/airport-api-0.0.1-SNAPSHOT.jar
```
The application will start on port `8080` by default.

## API Endpoints

### Get Airports (with filtering)
Returns a list of airports. All filter parameters are optional.

- **URL**: `/api/v1/airports`
- **Method**: `GET`
- **Query Parameters**:
    - `countryCode` (e.g., `AU`)
    - `regionCode` (e.g., `AU`)
    - `stateCode` (e.g., `NSW`)
    - `cityCode` (e.g., `SYD`)
    - `airportName` (e.g., `Sydney`) - supports partial matching

#### Example `curl` commands:
```bash
# Get all airports
curl http://localhost:8080/api/v1/airports

# Get all airports in Australia (countryCode=AU)
curl http://localhost:8080/api/v1/airports?countryCode=AU

# Get all airports with "International" in their name
curl "http://localhost:8080/api/v1/airports?airportName=International"
```