package com.rafaeldeluca.catalogo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rafaeldeluca.catalogo.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	//Método para buscar no database um usuario por email	
	User findByEmail(String email);	
	
	//busca por firstName query method
	List<User> findByFirstNameIgnoreCase(String firstName);

}
