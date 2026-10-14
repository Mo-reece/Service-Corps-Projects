# Service Corps Projects

Three standalone projects covering API security, product search, and backtracking.

## Projects

| Project | Summary | Walkthrough |
| --- | --- | --- |
| [1. RBAC with JWT](1-rbac-jwt/README.md) | Spring Boot API with JWT authentication, role-based access, and PostgreSQL. | [YouTube](https://www.youtube.com/watch?v=wfEX4y6hwto) |
| [2. Products API](2-products-api/README.md) | Spring Boot API demonstrating pagination, sorting, and filtering with PostgreSQL. | [YouTube](https://www.youtube.com/watch?v=QqoFnPxypn8) |
| [3. Generate Parentheses](3-generate-parentheses/README.md) | Java backtracking solution for generating valid parenthesis combinations. | |

## Run

Projects 1 and 2 require Docker Desktop. From each project's directory, run:

```bash
docker compose up --build
```

Project 1 serves on `http://localhost:8080`; project 2 serves on `http://localhost:8081`. Each project has its own README with setup details and API examples.

Project 3 requires Java 11 or newer and can be run from the repository root:

```bash
java 3-generate-parentheses/GenerateParentheses.java 3
```
