package com.springdemo.controller;

import com.springdemo.dto.LoginRequestDTO;
import com.springdemo.dto.UserRequestDTO;
import com.springdemo.dto.UserResponseDTO;
import com.springdemo.dto.UserUpdateDTO;
import com.springdemo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController{
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	//Regisztrálás
	@PostMapping("/register")
	@Operation(summary = "Felhasználó regisztrálása")
	@ApiResponses({
	    @ApiResponse(responseCode = "201", description = "Sikeres regisztráció"),
	    @ApiResponse(responseCode = "400", description = "Hibás adatok"),
	    @ApiResponse(responseCode = "409", description = "A felhasználó már létezik")
	})
	public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(user));
	}
	
	//Bejelentkezés
	@PostMapping("/login")
	@Operation(
			summary = "Bejelentkezés",
			description = "A felhasználó bejelentkezése és JWT token generálása."
		)
		@ApiResponses({
			@ApiResponse(
					responseCode = "200",
			        description = "Sikeres bejelentkezés."
			),
			@ApiResponse(
			        responseCode = "401",
			        description = "Hibás felhasználónév vagy jelszó."
			)
		})
	public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO user) {
		return ResponseEntity.ok(userService.login(user));
	}
	
	//Kijelentkezés
	@PostMapping("/logout")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Kijelentkezés")
	@ApiResponse(responseCode = "200", description = "Sikeres kijelentkezés")
    public ResponseEntity<String> logout(Authentication authentication) {
        return ResponseEntity.ok(userService.logout(authentication) + "!");
    }
	
	//Felhasználó profilja
	@GetMapping("/profile")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Saját profil lekérése")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Sikeres lekérés"),
	    @ApiResponse(responseCode = "404", description = "A felhasználó nem található")
	})
	public ResponseEntity<UserResponseDTO> profileSelf(Authentication authentication){
		return ResponseEntity.ok(userService.profileSelf(authentication));
	}
	
	//Felhasználó harakiri
	@DeleteMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Saját felhasználó törlése")
	@ApiResponses({
	    @ApiResponse(responseCode = "204", description = "Sikeres törlés"),
	    @ApiResponse(responseCode = "404", description = "A felhasználó nem található")
	})
	public ResponseEntity<Void> deleteSelf(Authentication authentication){
		userService.deleteSelf(authentication);
		return ResponseEntity.noContent().build();
	}
	
	//Felhasználó törlése
	@DeleteMapping("/admin/delete/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Felhasználó törlése")
	@ApiResponses({
	    @ApiResponse(responseCode = "204", description = "Sikeres törlés"),
	    @ApiResponse(responseCode = "404", description = "A felhasználó nem található")
	})
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
			
		return ResponseEntity.noContent().build();
	}
	
	//Felhasználó frissítése
	@PutMapping("/admin/update/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Felhasználó frissítése")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Sikeres frissítés"),
	    @ApiResponse(responseCode = "400", description = "Hibás adatok"),
	    @ApiResponse(responseCode = "404", description = "A felhasználó nem található")
	})
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, 
			@Valid @RequestBody UserUpdateDTO user)  {
		return ResponseEntity.ok(userService.updateUser(id, user));
	}
	
	//Felhasználó keresése
	@GetMapping("/admin/search/{id}")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Felhasználó lekérése azonosító alapján")
	@ApiResponses({
	    @ApiResponse(responseCode = "200", description = "Sikeres lekérés"),
	    @ApiResponse(responseCode = "404", description = "A felhasználó nem található")
	})
	public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id){
		return ResponseEntity.ok(userService.getUser(id));
	}
	
	//Felhasználók lekérése ADMIN FUNKCIÓ
	//http://localhost:8080/users/admin/users?page=1&size=1&sort=username,asc
	@GetMapping("/admin/users")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(summary = "Összes felhasználó lekérése")
	@ApiResponse(responseCode = "200", description = "Sikeres lekérés")
	public ResponseEntity<Page<UserResponseDTO>> getAll(Pageable pageable){
		return ResponseEntity.ok(userService.getAll(pageable));
	}
}