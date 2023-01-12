package com.rafaeldeluca.catalogo.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.rafaeldeluca.catalogo.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO  extends UserDTO { 
	
	private static final long serialVersionUID =1L;	
	
	
	

}
