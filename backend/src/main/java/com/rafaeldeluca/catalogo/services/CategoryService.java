package com.rafaeldeluca.catalogo.services;

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
import com.rafaeldeluca.catalogo.entities.Category;
import com.rafaeldeluca.catalogo.repositories.CategoryRepository;
import com.rafaeldeluca.catalogo.services.exceptions.DataBaseException;
import com.rafaeldeluca.catalogo.services.exceptions.ResourceNotFoundException;


@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page <Category> paginatedList = repository.findAll(pageable);		
		return paginatedList.map(x-> new CategoryDTO(x));
		
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {		
		
		Optional<Category> object = repository.findById(id);
		Category entity = object.orElseThrow(() -> new ResourceNotFoundException("Entidade não foi encontrada!"));
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		//não toca no banco de dados, instancia um objeto provisório com o id informado
		//só movimenta o banco de dados quando for dar o update no banco
		try {
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException error) {
			throw new ResourceNotFoundException("Id dessa categoria não foi encontrado " + id);
		}		
		
		
	}
	//Não pode deletar um categoria que já tem produto cadastrado
	//erro de integridade referencial
	public void delete(Long id) {
		
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException erro) {
			throw new ResourceNotFoundException("Id não encontrado " + id);
		} catch (DataIntegrityViolationException erro) {
			throw new DataBaseException("Violação de integridade de banco de dados!");
		}
		
		
				
	}	

}
