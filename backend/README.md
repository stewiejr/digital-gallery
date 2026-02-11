# Digital Gallery Backend (Spring Boot + PostgreSQL)

## Prereqs
- Java 17+
- PostgreSQL 13+

## Configure
Create a PostgreSQL database:

```
CREATE DATABASE digital_gallery;
```

Set environment variables (see `.env.example`):
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXP_MINUTES`
- `CORS_ALLOWED_ORIGINS`

## Run

```
mvn spring-boot:run
```

## API base
`http://localhost:8080/api`

### Auth
- `POST /auth/register`
- `POST /auth/login`

### Users
- `GET /users/{id}`
- `GET /users/by-username?username=...`
- `GET /users/search?q=...`
- `PATCH /users/{id}`

### Artworks
- `GET /artworks`
- `GET /artworks/{id}`
- `GET /artworks/by-user/{userId}`
- `GET /artworks/search?q=...`
- `POST /artworks`

### Exhibitions
- `GET /exhibitions`
- `GET /exhibitions/{id}`
- `POST /exhibitions`

### Comments
- `GET /comments/artworks/{artworkId}`
- `POST /comments/artworks/{artworkId}`
- `GET /comments/users/{userId}`
- `POST /comments/users/{userId}`

### Payments
- `GET /payments/users/{userId}`
- `GET /payments/users/{userId}/{paymentId}`
- `POST /payments/users/{userId}`

## Notes
- Use `Authorization: Bearer <token>` for protected routes (POST/PATCH).
- Image uploads remain on Imgur; send `imageUrl` from the client.
