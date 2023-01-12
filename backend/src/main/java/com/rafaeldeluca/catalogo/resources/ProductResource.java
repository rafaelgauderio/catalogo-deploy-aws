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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rafaeldeluca.catalogo.dto.ProductDTO;
import com.rafaeldeluca.catalogo.services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {
	
	@Autowired
	private ProductService service;	
	
	@GetMapping
	public ResponseEntity<Page <ProductDTO>> findAll(
			@RequestParam(value = "categoryId", defaultValue="0") Long categoryId,
			@RequestParam(value = "name"      , defaultValue="") String name,
			Pageable pageable								
			) {	
		//Parametros fica: page, size, sort
		//PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);		
		Page<ProductDTO> paginatedList = service.findAllPaged(categoryId, name, pageable);			
		return ResponseEntity.ok().body(paginatedList);
	}		
	
	
	@GetMapping(value="/{id}")
	public ResponseEntity <ProductDTO> findById(@PathVariable Long id) {		
		ProductDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	//@ valid tem que ser na frente do parametro a ser validado
	@PostMapping
	public ResponseEntity<ProductDTO> insertProduct(@Valid @RequestBody ProductDTO dto) {
		dto = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(dto.getId()).toUri();
		return ResponseEntity.created(uri).body(dto);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,@Valid @RequestBody ProductDTO dto) {
		dto = service.update(id,dto);
		return ResponseEntity.ok().body(dto);
	}	
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}	
	
	
}
