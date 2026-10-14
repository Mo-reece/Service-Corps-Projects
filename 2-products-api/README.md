# Products API: Pagination, Sorting & Filtering

A Spring Boot REST API for browsing product data. `GET /products` supports **pagination**, **sorting by multiple
fields** and **filtering by category and price range**, and all three can be combined. The work happens in a single
**PostgreSQL** query, so the API never loads the whole table into memory.

## 🎥 Video walkthrough

**▶ [Watch the project 2 walkthrough](https://www.youtube.com/watch?v=QqoFnPxypn8)**

## Features

- Pagination with `page` and `size`, with page size capped at 100
- Sorting by one or more fields, e.g. `sort=category,asc&sort=price,desc`
- Optional filters: `category` (case-insensitive), `minPrice` and `maxPrice`
- Clear 400 errors for unknown sort fields or invalid numbers
- 20 sample products loaded on startup
- One command to run the whole stack with Docker

## Tech stack

Java 21 · Spring Boot 3.5 · Spring Data JPA · PostgreSQL 16 · Docker

## Quick start

Requires [Docker Desktop](https://www.docker.com/products/docker-desktop/).

```bash
docker compose up --build
```

The API runs at `http://localhost:8081`.

<details>
<summary>Run without Docker</summary>

Requires Java 21, Maven and a PostgreSQL database named `products` on `localhost:5433` (user and password `postgres`).

```bash
mvn spring-boot:run
```
</details>

## API

### `GET /products`

| Param      | Example           | Default  | Description |
|------------|-------------------|----------|-------------|
| `page`     | `page=0`          | `0`      | Page number, starting at 0 |
| `size`     | `size=5`          | `10`     | Items per page (max 100) |
| `sort`     | `sort=price,desc` | `id,asc` | `field,direction`. Repeat it to sort by several fields. Fields: `id`, `name`, `category`, `price` |
| `category` | `category=books`  | –        | Exact category, case-insensitive |
| `minPrice` | `minPrice=20`     | –        | Price ≥ value |
| `maxPrice` | `maxPrice=100`    | –        | Price ≤ value |

All parameters are optional.

### Examples

```bash
curl "localhost:8081/products"
curl "localhost:8081/products?page=1&size=5"
curl "localhost:8081/products?sort=category,asc&sort=price,desc"
curl "localhost:8081/products?category=electronics&minPrice=30&maxPrice=400&sort=price,asc"
```

Response:

```json
{
  "content": [
    { "id": 6, "name": "USB-C Hub",           "category": "Electronics", "price": 39.95 },
    { "id": 3, "name": "Mechanical Keyboard", "category": "Electronics", "price": 89.50 }
  ],
  "page": { "size": 10, "number": 0, "totalElements": 4, "totalPages": 1 }
}
```

## Project structure

```
src/main/java/com/example/products/
├── ProductsApplication.java   # Entry point
├── ProductController.java     # GET /products: query params, sort-field check
├── ProductRepository.java     # One JPQL query: optional filters + Pageable
└── Product.java               # JPA entity -> "product" table
src/main/resources/
├── application.yml            # Database, page size cap, JSON page format
└── data.sql                   # 20 sample products
```

## Design decisions

- **One query for every combination of filters.** Each condition is written as `(:param IS NULL OR ...)`, so a filter
  you don't send is simply skipped. That avoids one repository method per combination.
- **Spring builds paging and sorting for me.** The `page`, `size` and `sort` query parameters become a `Pageable`,
  and Spring Data adds `ORDER BY`, `LIMIT`/`OFFSET` and a count query.
- **Sort fields are whitelisted.** Only known columns can be sorted on, so bad input gets a clear 400 instead of a server error.
- **Page size is capped at 100**, so a client can't request millions of rows at once.
- **Prices are `BigDecimal`**, because money must be exact.

## Testing

Verified end to end against PostgreSQL in Docker with 12 checks:
- Default paging and custom page/size
- Single- and multi-field sorting
- Every filter on its own and combined with paging
- Mixed-case categories and empty results
- The page-size cap, unknown sort fields (400) and invalid numbers (400)
