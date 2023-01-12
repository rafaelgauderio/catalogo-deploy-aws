package com.rafaeldeluca.catalogo.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.rafaeldeluca.catalogo.dto.UserDTO;
import com.rafaeldeluca.catalogo.entities.User;
import com.rafaeldeluca.catalogo.repositories.UserRepository;
import com.rafaeldeluca.catalogo.resources.exceptions.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserDTO> {
	
	@Autowired
	private UserRepository repository;
	
	@Override	public void initialize(UserInsertValid annotation) {
	}

	@Override
	public boolean isValid(UserDTO dto, ConstraintValidatorContext context) {
		
		List<FieldMessage> list = new ArrayList<>();		
		
		// Colocar aqui os testes de validação, acrescentando objetos FieldMessage à lista
		
		User user = repository.findByEmail(dto.getEmail());
		// repository por default retorno null se não encontrar
		if(user !=null) {
			
			list.add(new FieldMessage("Este email já existe no banco de dados. Informe outro.","email"));
		}
		
		for (FieldMessage fieldMessage : list) {
			//inserir na lista de erro do beans validation
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(fieldMessage.getMessage()).addPropertyNode(fieldMessage.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
