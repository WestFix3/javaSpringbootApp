# ProjectSpringBootApp

Spring Boot alapú backend alkalmazás, amely **User** és **Todo** entitások kezelését valósítja meg.  
A projekt célja egy tiszta, rétegezett architektúra bemutatása, saját hibakezeléssel, biztonsági résszel és H2 adatbázis integrációval.

---

## Projekt felépítés

- **Model**  
  - `UserModel` – felhasználó entitás  
  - `TodoModel` – teendő entitás  

- **Repository**  
  - `UserRepository` – JPA interfész, CRUD műveletek  
  - `TodoRepository` – JPA interfész, CRUD műveletek  

- **Service**  
  - `UserService` – üzleti logika a User/Todo kezeléshez  
  - `TodoService` – üzleti logika a Todo kezeléshez  

- **Controller**  
  - `UserController` – REST végpontok a User/Todo műveletekhez  
  - `TodoController` – REST végpontok a Todo műveletekhez  

- **Exception**  
  - `InvalidTodoIdException` – érvénytelen ID esetén dobódik  
  - `TodoNotFoundException` – nem létező Todo esetén dobódik  
  - `GlobalExceptionHandler` – egységes JSON hiba válasz  

- **Security**  
  - Spring Security konfiguráció (basic auth / JWT előkészítés)  
  - Auth filterek és jogosultság kezelés  

- **Database**  
  - H2 in-memory adatbázis  
  - Spring Data JPA + Hibernate ORM  
  - Automatikus táblagenerálás az entitásokból  

---

## Használt technológiák

- **[Spring Boot]** – gyors backend fejlesztés  
- **[Spring Data JPA]** – adatbázis műveletek egyszerűsítése  
- **[Hibernate]** – ORM implementáció  
- **[H2 Database]** – teszteléshez ideális beépített DB  
- **[Spring Security]** – autentikáció és jogosultság kezelés  
- **[ResponseEntity]** – szabványos HTTP válaszok  

---

## Telepítés és futtatás
(A projekt Maven segítségével készült, így a futtatáshoz Maven szükséges.)
1. Klónozd a repót:
   ```bash
   https://github.com/WestFix3/javaSpringbootApp.git
   cd ProjectSpringBoot-app
