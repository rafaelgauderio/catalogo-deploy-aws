package com.rafaeldeluca.catalogo.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.rafaeldeluca.catalogo.dto.CategoryDTO;
import com.rafaeldeluca.catalogo.entities.Category;
import com.rafaeldeluca.catalogo.repositories.CategoryRepository;
import com.rafaeldeluca.catalogo.resources.exceptions.FieldMessage;

public class CategoryInsertValidator implements ConstraintValidator<CategoryInsertValid, CategoryDTO> {
	
	@Autowired
	private CategoryRepository repository;
	
	@Override
	public void initialize(CategoryInsertValid annotation) {
	}

	@Override
	public boolean isValid(CategoryDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> lista = new ArrayList<>();	
		
		Category category = repository.findByName(dto.getName());	
		
		if(category !=null) {
			
			lista.add(new FieldMessage("Esta categoria já existe no banco de dados. Informe outra.","Categoria"));
		}
		
		for (FieldMessage fieldMessage : lista) {
			
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(fieldMessage.getMessage()).addPropertyNode(fieldMessage.getFieldName())
					.addConstraintViolation();
		}
		return lista.isEmpty();
	}
}
