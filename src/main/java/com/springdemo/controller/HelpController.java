package com.springdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/help")
public class HelpController {
	
	@GetMapping
	@Operation(summary = "API használati útmutató")
	@ApiResponse(responseCode = "200", description = "Sikeres lekérés")
	public ResponseEntity<String> help() {
        String help = """
                
                =========================
                API HASZNÁLATA
                =========================

                User API:
                GET /help/user

                Task API:
                GET /help/task
                
                Példák:
                GET /help/peldak

                """;

        return ResponseEntity.ok(help);
    }

	@GetMapping("/users")
	@Operation(summary = "User API használati útmutató")
	@ApiResponse(responseCode = "200", description = "Sikeres lekérés")
    public ResponseEntity<String> userHelp() {
        String help = """
                
                =========================
                USER API
                =========================

                REGISZTRÁCIÓ
                POST /users/register

                Body:
                {
                    "username": "asd",
                    "password": "asd",
                    "email": "asd@asd.com"
                }


                BEJELENTKEZÉS
                POST /users/login

                Body:
                {
                    "username": "asd",
                    "password": "asd"
                }

                Sikeres login után JWT tokent kapsz.

                A védett endpointokhoz:
                Authorization: Bearer <TOKEN>


                SAJÁT PROFIL
                GET /users/profile


                KIJELENTKEZÉS
                POST /users/logout


                SAJÁT FELHASZNÁLÓ TÖRLÉSE
                DELETE /users/me


                =========================
                ADMIN
                =========================

                ÖSSZES FELHASZNÁLÓ
                GET /users/admin/users


                FELHASZNÁLÓ KERESÉSE
                GET /users/admin/search/{id}


                FELHASZNÁLÓ MÓDOSÍTÁSA
                PUT /users/admin/update/{id}

                Body:
                {
                    "username": "ujnev",
                    "email": "uj@email.com"
                }


                FELHASZNÁLÓ TÖRLÉSE
                DELETE /users/admin/delete/{id}

                Az admin endpointokhoz ADMIN jogosultság szükséges.

                """;

        return ResponseEntity.ok(help);
    }

	@GetMapping("/tasks")
	@Operation(summary = "Task API használati útmutató")
	@ApiResponse(responseCode = "200", description = "Sikeres lekérés")
    public ResponseEntity<String> taskHelp() {
        String help = """
                
                =========================
                TASK API
                =========================

                FELADAT LÉTREHOZÁSA
                POST /tasks/create

                Body:
                {
                    "title": "Bevásárlás",
                    "description": "Tej és kenyér",
                    "priority": "HIGH",
                    "dueDate": "2026-09-15"
                }


                SAJÁT FELADATOK LEKÉRÉSE
                GET /tasks/all


                FELADAT MÓDOSÍTÁSA
                POST /tasks/{id}

                Példa:
                POST /tasks/5

                Body:
                {
                    "title": "Bevásárlás",
                    "description": "Tej, kenyér és tojás",
                    "priority": "HIGH",
                    "dueDate": "2026-09-16"
                }


                FELADAT TÖRLÉSE
                DELETE /tasks/{id}

                Példa:
                DELETE /tasks/5


                FELADAT BEFEJEZETT ÁLLAPOTÁNAK MÓDOSÍTÁSA
                PATCH /tasks/{id}/completed?completed=true

                Példa:
                PATCH /tasks/5/completed?completed=true

                Vagy:

                PATCH /tasks/5/completed?completed=false


                =========================
                ADMIN
                =========================

                ÖSSZES FELADAT LEKÉRÉSE
                GET /tasks/admin/all

                Az admin endpointhez ADMIN jogosultság szükséges.

                """;

        return ResponseEntity.ok(help);
    }
    
	@GetMapping("/peldak")
	@Operation(summary = "API példák lekérése")
	@ApiResponse(responseCode = "200", description = "Sikeres lekérés")
    public ResponseEntity<String> peldak(){

        String pelda = "User létrehozás:\r\n"
                + "{\r\n"
                + "    \"username\": \"asd\",\r\n"
                + "    \"password\": \"asd\",\r\n"
                + "    \"email\": \"asd@asd.com\"\r\n"
                + "}\r\n"
                + "\r\n"
                + "Task létrehozás:\r\n"
                + "{\r\n"
                + "    \"title\": \"cim\",\r\n"
                + "    \"description\": \"valami szöveg\",\r\n"
                + "    \"priority\": \"alap\",\r\n"
                + "    \"dueDate\": \"2026-09-15\"\r\n"
                + "}\r\n"
                + "\r\n"
                + "User to admin:\r\n"
                + "http://localhost:8080/h2-console\r\n"
                + "\r\n"
                + "UPDATE users\r\n"
                + "SET role = 'ADMIN'\r\n"
                + "WHERE username = 'asd';\r\n"
                + "\r\n"
                + "Swagger UI:\r\n"
                + "http://localhost:8080/swagger-ui/index.html\r\n"
                + "\r\n"
                + "OpenAPI JSON:\r\n"
                + "http://localhost:8080/v3/api-docs";

        return ResponseEntity.ok(pelda);
    }
}
