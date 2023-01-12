package com.rafaeldeluca.catalogo.resources;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rafaeldeluca.catalogo.dto.UserDTO;
import com.rafaeldeluca.catalogo.dto.UserInsertDTO;
import com.rafaeldeluca.catalogo.dto.UserUpdateDTO;
import com.rafaeldeluca.catalogo.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
	
	@Autowired
	private UserService service;	
	
	@GetMapping
	public ResponseEntity<Page <UserDTO>> findAll(Pageable pageable								
			) {				
		Page<UserDTO> paginatedList = service.findAllPaged(pageable);			
		return ResponseEntity.ok().body(paginatedList);
	}		
	
	
	@GetMapping(value="/{id}")
	public ResponseEntity <UserDTO> findById(@PathVariable Long id) {		
		UserDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
		
	@PostMapping
	public ResponseEntity<UserDTO> insertUser(@Valid @RequestBody UserInsertDTO dto) {
		UserDTO userDto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(userDto.getId()).toUri();
		return ResponseEntity.created(uri).body(userDto);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateDTO dto) {
		UserDTO userDto = service.update(id,dto);
		return ResponseEntity.ok().body(userDto);
	}	
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}		
	
}
