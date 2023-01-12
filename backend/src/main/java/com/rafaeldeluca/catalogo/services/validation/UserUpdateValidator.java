package com.rafaeldeluca.catalogo.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.rafaeldeluca.catalogo.dto.UserUpdateDTO;
import com.rafaeldeluca.catalogo.entities.User;
import com.rafaeldeluca.catalogo.repositories.UserRepository;
import com.rafaeldeluca.catalogo.resources.exceptions.FieldMessage;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	
	@Autowired
	private HttpServletRequest request; //guarda as info das requicições. Uso para pegar o id do User da requisição http.
	
	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserUpdateValid annotation) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		//Map(key,value) = Map(<id><1> == em requisições http tudo é String
		@SuppressWarnings("unchecked")
		var uriVariables = (Map<String,String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userIdFromHttpRequest = Long.parseLong(uriVariables.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();		
		
		User user = repository.findByEmail(dto.getEmail());
		
		//não pode atualizar o email de um usuario que não o id que venho da requisição
		if(user !=null && userIdFromHttpRequest != user.getId()) {
			
			list.add(new FieldMessage("Este email já existe no banco de dados. Informe outro.","email"));
		}
		
		for (FieldMessage fieldMessage : list) {
			
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(fieldMessage.getMessage()).addPropertyNode(fieldMessage.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
