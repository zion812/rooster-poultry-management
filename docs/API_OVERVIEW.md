# Rooster Poultry Management – Backend API Overview

---

## 1. Introduction
This document provides a starting point for documenting the REST API endpoints exposed by the backend (`backend/server.js`). For full OpenAPI/Swagger integration, see the TODO section below.

---

## 2. Sample Endpoint Table

| Method | Endpoint              | Description                        |
|--------|-----------------------|------------------------------------|
| POST   | /api/predict-price    | Predict poultry price              |
| GET    | /api/health           | Health check/status                |
| POST   | /api/auth/login       | User login                         |
| POST   | /api/auth/register    | User registration                  |
| GET    | /api/marketplace      | List marketplace items             |
| POST   | /api/marketplace      | Add new item to marketplace        |
| ...    | ...                   | ...                                |

---

## 3. OpenAPI/Swagger Integration (TODO)
- Add `swagger-jsdoc` and `swagger-ui-express` to backend dependencies.
- Create `swagger.yaml` or `swagger.json` in backend directory.
- Document each endpoint with request/response schemas.
- Expose Swagger UI at `/docs` or `/swagger` endpoint.

---

## 4. Example: Predict Price Request

```
POST /api/predict-price
{
  "location": "Krishna District",
  "breed": "Broiler",
  "weight": 2.5
}
```
Response:
```
{
  "predictedPrice": 120.5,
  "currency": "INR"
}
```

---

## 5. Authentication & Security
- Most endpoints require JWT or Firebase Auth token
- Rate limiting and input validation enforced

---

## 6. Next Steps
- Complete endpoint inventory
- Add OpenAPI/Swagger docs
- Document request/response examples for all endpoints
