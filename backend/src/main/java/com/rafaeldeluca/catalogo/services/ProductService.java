package com.rafaeldeluca.catalogo.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rafaeldeluca.catalogo.dto.CategoryDTO;
import com.rafaeldeluca.catalogo.dto.ProductDTO;
import com.rafaeldeluca.catalogo.entities.Category;
import com.rafaeldeluca.catalogo.entities.Product;
import com.rafaeldeluca.catalogo.repositories.CategoryRepository;
import com.rafaeldeluca.catalogo.repositories.ProductRepository;
import com.rafaeldeluca.catalogo.services.exceptions.DataBaseException;
import com.rafaeldeluca.catalogo.services.exceptions.ResourceNotFoundException;


@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Long categoryId, String name, Pageable pageable) {
		//getOne instancia o objeto em memória, sem tocar o bando de dados
		// não pode passar um categoryId = zero, então faz um if para retornar null
		// Category category = (categoryId==0) ? null : categoryRepository.getOne(categoryId);
		List<Category> categories = (categoryId==0) ? null : Arrays.asList(categoryRepository.getOne(categoryId));		
		// função TRIM para tirar espaços em brancos antes e depois da String
		Page <Product> paginatedList = repository.search(categories,name.trim(),pageable);
		
		//chamada seca do método auxiliar apenas para buscar as categorias
		repository.findProductsWithCategories(paginatedList.getContent());
		return paginatedList.map(x-> new ProductDTO(x, x.getCategories()));
		
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {		
		
		Optional<Product> object = repository.findById(id);
		Product entity = object.orElseThrow(() -> new ResourceNotFoundException("Entidade não foi encontrada!"));
		return new ProductDTO(entity, entity.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto,entity);				
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		
		try {
			Product entity = repository.getOne(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
		} catch (EntityNotFoundException error) {
			throw new ResourceNotFoundException("Id desse produto não foi encontrado " + id);
		}
		
		
	}
	
	public void delete(Long id) {
		
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException erro) {
			throw new ResourceNotFoundException("Id não encontrado " + id);
		} catch (DataIntegrityViolationException erro) {
			//objeto está associando com um outro objeto que não pode deleter, da um erro de integração
			throw new DataBaseException("Violação de integridade de banco de dados!");
		}	
				
	}
	//método auxiliar para não criar vários set no método insert e update
	//mas o id não é setado e nem atualizado. É auto increment
	private void copyDtoToEntity(ProductDTO dto, Product entity) {	
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgURL());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for(CategoryDTO catDTO : dto.getCategories()) {			
			
			Category category = categoryRepository.getOne(catDTO.getId());
			entity.getCategories().add(category);
		}
		
		
	}

}
