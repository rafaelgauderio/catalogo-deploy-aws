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

import com.rafaeldeluca.catalogo.dto.CategoryDTO;
import com.rafaeldeluca.catalogo.services.CategoryService;

@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

	@Autowired
	private CategoryService service;

	//@RequestParam = dado opcional
	@GetMapping
	public ResponseEntity<Page <CategoryDTO>> findAll(Pageable pageable) {				

		Page<CategoryDTO> paginatedList = service.findAllPaged(pageable);			
		return ResponseEntity.ok().body(paginatedList);
	}	


	//PathVariable para dado obrigatório
	@GetMapping(value="/{id}")
	public ResponseEntity <CategoryDTO> findById(@PathVariable Long id) {		
		CategoryDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}

	//codigo http 200 = success
	//codigo http 201 = recurso criado
	@PostMapping
	public ResponseEntity<CategoryDTO> insertCategory(@Valid @RequestBody CategoryDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	
	@PutMapping(value = "/{id}")													//@ valid tem que ser na frente do parametro a ser validado
	public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,@Valid  @RequestBody CategoryDTO dto) {
		dto = service.update(id,dto);
		return ResponseEntity.ok().body(dto);
	}

	//codigo http 204 = avisa para a aplicação que não tem corpo na resposta
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}



}
