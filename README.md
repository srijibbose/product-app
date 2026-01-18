# Country-Based Product API

A simple Kotlin Ktor service that manages products and applies discounts based on business rules.

## Features
- **Create Products**: Define products with a base price and country.
- **Get Products**: Retrieve products by country with calculated VAT and discounts.
- **Apply Discount**: Atomic and idempotent discount application using MongoDB.

## Prerequisites
- Java 21+
- Docker & Docker Compose (for containerized run)
- MongoDB (for local non-docker run)

## Build and Run

### Using Docker Compose (Recommended)
This will start both the Application and MongoDB.

```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

### Running Locally (Gradle)
1. Ensure a MongoDB instance is running locally on port `27017`.
2. Build and run the service:

```bash
./gradlew build
./gradlew run
```

## API Testing (Curl Examples)

### 1. Create a Product
```bash
curl -i -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Gaming Laptop", "basePrice": 1500.0, "country": "Sweden"}'
```

*(Copy the `id` from the response for the next steps)*

### 2. Get Products (Sweden)
Check the price (VAT 25% for Sweden).
```bash
curl -i -X GET "http://localhost:8080/products?country=Sweden"
```

### 3. Apply Discount
Replace `{id}` with the actual product ID.
```bash
curl -i -X PUT "http://localhost:8080/products/{id}/discount" \
  -H "Content-Type: application/json" \
  -d '{"discountId": "SUMMER2025"}'
```

### 4. Create a Discount
You can create new discount types.
```bash
curl -i -X POST http://localhost:8080/discounts \
  -H "Content-Type: application/json" \
  -d '{"discountId": "SPECIAL10", "percent": 10.0}'
```

### 5. Verify Discount
Call the GET endpoint again to see the updated `finalPrice` and `discounts` list.
```bash
curl -i -X GET "http://localhost:8080/products?country=Sweden"
```

## Architecture
See [ARCHITECTURE.md](ARCHITECTURE.md) for design details and sequence diagrams.
