package com.rafaeldeluca.catalogo.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.rafaeldeluca.catalogo.entities.Product;
import com.rafaeldeluca.catalogo.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private Long existId;
	private Long nonExistId;
	private Long countTotalProducts = 25L;
	
	@BeforeEach
	void setUp() throws Exception {
		this.existId = 2L;
		this.nonExistId = 50L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {	
		
		repository.deleteById(2L);
		
		//optinal tem que retornar vazio se o produto de id correspondente foi deletado
		Optional<Product> optional = repository.findById(existId);
		Assertions.assertFalse(optional.isPresent());
		
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists () {	
		
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistId);
		});
		
	}
	
	@Test
	public void findByIdShouldReturnOptionalNotEmptyWhenIdExists () {
		//tem que retornar um optional não nulo se for com id existente		
		Optional<Product> optinal = repository.findById(existId);
		
		Assertions.assertFalse(optinal.isEmpty());
		Assertions.assertTrue(optinal.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnOptinalEmptyWhenIdNotExists() {
		//tem que retornar um optional não VAZIO se for com id inexistente
		Optional<Product> optinal = repository.findById(nonExistId);
		
		Assertions.assertFalse(optinal.isPresent());
		Assertions.assertTrue(optinal.isEmpty());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementProductWhenIdIsNull () {
		Product product = Factory.createProduct();
		product.setId(null);
		
		//tem que ser null o id antes de salvar no banco
		Assertions.assertNull(product.getId());
		//tem que salvar o produto no banco e retorna o id
		product = repository.save(product);
		//depois de inserir no banco o id não pode ser null
		Assertions.assertNotNull(product.getId());
		//vefificar se o produto é igual a 26, pois tinha 25 no sed inicial
		Assertions.assertEquals(this.countTotalProducts + 1, product.getId());		
	}
	

}
