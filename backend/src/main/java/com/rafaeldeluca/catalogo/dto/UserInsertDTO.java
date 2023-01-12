package com.rafaeldeluca.catalogo.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.rafaeldeluca.catalogo.services.validation.UserInsertValid;

@UserInsertValid
public class UserInsertDTO  extends UserDTO { 
	
	private static final long serialVersionUID =1L;
	
	@NotBlank (message = "Obrigatório preencher a senha")
	@Size (min = 6, max = 15, message = "Campo senha deve ter entre 6 e 15 caracteres")
	private String password;
	
	UserInsertDTO () {
		super();
	}
	
	public String getPassword () {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
