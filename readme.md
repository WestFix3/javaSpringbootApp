# ProjectSpringBootApp

Spring Boot alapú backend REST API, amely **User** és **Task** entitások kezelését valósítja meg.

A projekt célja egy tiszta, rétegezett backend architektúra bemutatása, amely tartalmazza a REST API-k működését, adatbázis-kezelést, DTO-k használatát, JWT alapú autentikációt, jogosultságkezelést, JPA entitáskapcsolatokat, validációt, paginationt, sortingot és egységes hibakezelést.

---

## Főbb funkciók

- User regisztráció és bejelentkezés
- User CRUD műveletek
- Task CRUD műveletek
- User és Task közötti JPA kapcsolat
- JWT alapú autentikáció
- Szerepkör alapú jogosultságkezelés
- BCrypt alapú jelszóhash-elés
- DTO-k használata request és response adatokhoz
- Request validáció
- Saját exceptionök
- Globális exception handling
- Egységes JSON hibaüzenetek
- HTTP státuszkódok megfelelő használata
- Pagination
- Sorting
- OpenAPI / Swagger API dokumentáció
- H2 adatbázis
- Spring Data JPA és Hibernate ORM
- Maven alapú projektfelépítés

---

## Projekt architektúrája

A projekt rétegezett architektúrát használ:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

### Model

- `UserModel` – felhasználó entitás
- `TaskModel` – feladat entitás

A `User` és `Task` entitások között JPA kapcsolat található, így egy felhasználóhoz több Task tartozhat.

### DTO

- `UserRequestDTO` – User létrehozásához és módosításához szükséges adatok
- `UserResponseDTO` – API válaszban visszaadott User adatok

A DTO-k segítségével az API réteg és az adatbázisban használt entitások elkülönülnek egymástól.

Ez többek között lehetővé teszi, hogy érzékeny adatok, például a jelszó, ne kerüljenek vissza az API response-ba.

### Repository

- `UserRepository`
- `TaskRepository`

A repository réteg a Spring Data JPA `JpaRepository` interfészére épül.

A Spring Data JPA derived query-k segítségével például User alapján is lekérhetők a hozzá tartozó Taskok.

### Service

- `UserService`
- `TaskService`

A Service réteg tartalmazza az üzleti logikát, és kapcsolatot biztosít a Controller és Repository réteg között.

### Controller

- `UserController`
- `TaskController`

A Controller réteg fogadja a HTTP kéréseket, majd továbbítja azokat a megfelelő Service rétegnek.

---

## Exception Handling

A projekt saját exceptionöket és globális hibakezelést használ.

Példák:

- `UserAlreadyExistsException`
- User not found
- Task not found
- Invalid request data

A hibák egységes JSON formátumban kerülnek visszaadásra.

Példa:

```json
{
    "status": 404,
    "message": "User not found!"
}
```

A projekt a megfelelő HTTP státuszkódokat használja, például:

- `400 Bad Request` – hibás kérés vagy adat
- `401 Unauthorized` – hitelesítés szükséges vagy sikertelen
- `403 Forbidden` – nincs megfelelő jogosultság
- `404 Not Found` – a keresett erőforrás nem található
- `409 Conflict` – például már létező username vagy email
- `500 Internal Server Error` – váratlan szerveroldali hiba

---

## Security

A projekt Spring Security segítségével kezeli a hitelesítést és jogosultságokat.

### Security komponensek

- `SecurityConfig`
- `JwtService`
- `JwtAuthenticationFilter`
- `CustomUserDetailsService`
- `AuthenticationManager`
- `DaoAuthenticationProvider`
- `PasswordEncoder`

### JWT Authentication

A bejelentkezés során a felhasználó hitelesítése után JWT token kerül generálásra.

Egyszerűsített folyamat:

```text
Login
  ↓
AuthenticationManager
  ↓
AuthenticationProvider
  ↓
CustomUserDetailsService
  ↓
UserRepository
  ↓
Password ellenőrzés
  ↓
JWT token generálása
```

A későbbi védett HTTP kéréseknél:

```text
HTTP Request
  ↓
JWT Authentication Filter
  ↓
JWT ellenőrzése
  ↓
SecurityContext
  ↓
Controller
```

A token a request `Authorization` headerében kerül továbbításra:

```text
Authorization: Bearer <JWT_TOKEN>
```

### Password Security

A felhasználói jelszavak BCrypt segítségével vannak hash-elve.

A rendszer nem a felhasználó eredeti jelszavát tárolja az adatbázisban.

### Roles

A projekt szerepköröket is kezel, például:

- `USER`
- `ADMIN`

Ezek segítségével különböző jogosultságok adhatók az egyes felhasználóknak.

---

## Database

A projekt JPA és Hibernate segítségével kommunikál az adatbázissal.

Használt technológiák:

- H2 Database
- Spring Data JPA
- Hibernate ORM
- JPA Entity mapping
- Entity relationships

A Java objektumok és az adatbázis táblái közötti leképezést a JPA/Hibernate végzi.

Például a User és Task kapcsolat:

```text
User
  │
  └── 1 : N ── Task
```

---

## Pagination és Sorting

A projekt támogatja az adatok lapozását és rendezését.

Például:

```text
?page=0&size=10
```

Az első oldalt kéri 10 elemmel.

Rendezés például:

```text
?sort=username
```

Ez különösen nagyobb mennyiségű adat kezelése esetén hasznos.

---

## OpenAPI / Swagger

A projekt OpenAPI dokumentációval rendelkezik.

A Swagger UI segítségével az API endpointjai:

- dokumentálhatók
- áttekinthetők
- tesztelhetők

A dokumentáció segítségével az API használata egyszerűbben megismerhető.

---

# Használt technológiák

## Programming Language

- **Java**

## Backend Framework

- **Spring Boot**
- **Spring Web**
- **Spring MVC**

## Data Access

- **Spring Data JPA**
- **JPA**
- **Hibernate ORM**
- **JpaRepository**
- **Derived Query Methods**

## Database

- **H2 Database**
- **MySQL Connector**

## Security

- **Spring Security**
- **JWT (JSON Web Token)**
- **JJWT**
- **BCrypt**
- **AuthenticationManager**
- **DaoAuthenticationProvider**
- **SecurityContext**
- **OncePerRequestFilter**

## API Development

- **REST API**
- **HTTP methods**
  - GET
  - POST
  - PUT
  - DELETE
- **ResponseEntity**
- **HTTP status codes**
- **Request parameters**
- **Request body**
- **Path variables**
- **DTO pattern**

## Validation & Error Handling

- **Spring Validation**
- **Custom Exceptions**
- **Global Exception Handling**
- **@RestControllerAdvice**
- **@ExceptionHandler**
- **JSON error responses**

## API Documentation

- **OpenAPI**
- **Swagger UI**

## Build Tool

- **Maven**

## Development

- **Eclipse IDE**
- **Git**
- **GitHub**

---

## API használata

Példák:

```text
POST /users/register
POST /users/login
```

A védett endpointok JWT token segítségével használhatók:

```text
Authorization: Bearer <JWT_TOKEN>
```

A Task endpointok a Task CRUD műveleteit biztosítják, valamint lehetőség van a felhasználóhoz tartozó Taskok kezelésére.

---

## Projekt futtatása

### Repository klónozása

```bash
git clone https://github.com/WestFix3/javaSpringbootApp.git
cd javaSpringbootApp
```

### Projekt megnyitása

A projekt Maven alapú, ezért Maven projektként nyitható meg Java fejlesztői környezetben.

### Alkalmazás indítása

Az alkalmazás a Spring Boot main class futtatásával indítható.

---

## Projekt célja

A projekt egy gyakorlati Spring Boot backend alkalmazásként készült.

A projekt során alkalmazott főbb backend koncepciók:

- REST API fejlesztés
- rétegezett architektúra
- Dependency Injection
- IoC
- Spring Security
- JWT authentication
- Role-based authorization
- Password hashing
- JPA és Hibernate
- Entity relationships
- DTO pattern
- Repository pattern
- Service layer
- Exception handling
- Validation
- Pagination
- Sorting
- API documentation
- Git és GitHub használata

A projekt folyamatosan fejleszthető további backend funkciókkal.