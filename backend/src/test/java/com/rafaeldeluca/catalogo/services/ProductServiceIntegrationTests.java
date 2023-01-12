package com.rafaeldeluca.catalogo.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.rafaeldeluca.catalogo.dto.ProductDTO;
import com.rafaeldeluca.catalogo.repositories.ProductRepository;
import com.rafaeldeluca.catalogo.services.exceptions.ResourceNotFoundException;

//carregar o contenxto da aplicação
@SpringBootTest
@Transactional // um teste não pode depender da execução de um teste anterior, transactional = necessário dar um rollback depois da cada operação.
public class ProductServiceIntegrationTests {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long existId;
	private Long nonExistId;
	private Long countTotalProducts;
	
	@BeforeEach
	void setUp ( ) throws Exception {
		existId =2L;
		nonExistId = 50L; //vai até o 25L
		countTotalProducts = 25L;		
	}
	
	@Test
	public void deleteShouldDelteResourceWhenIdExists () {
		
		service.delete(existId);
		
		//método count retorna a quantidade total de registro no database
		Assertions.assertEquals(24, repository.count());
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
		Assertions.assertTrue(countTotalProducts - 1 == repository.count() );
	}
	
	@Test
	public void deteleShouldThrowResourceNotFoundExceptionWhenIdDoesntExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistId);
		});
	}
	
	@Test
	public void fillAllPagedShouldReturnPageWhenPage0AndSize20() {
		
		PageRequest pageResquest = PageRequest.of(0, 20);
		
		Page<ProductDTO> finalResult = service.findAllPaged(0L,"",pageResquest);
		
		Assertions.assertFalse(finalResult.isEmpty());
		Assertions.assertTrue(finalResult.hasContent());
		Assertions.assertEquals(0, finalResult.getNumber());
		Assertions.assertEquals(20, finalResult.getSize());
		Assertions.assertEquals(countTotalProducts, finalResult.getTotalElements());
		Assertions.assertEquals(25L, finalResult.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesnotExist() {
		
		//somente tem 2 paginas se a primeira tem 20 e a segunda 5 elementos
		PageRequest pageResquest = PageRequest.of(5, 20);
		
		Page<ProductDTO> finalResult = service.findAllPaged(0L,"",pageResquest);
		
		Assertions.assertTrue(finalResult.isEmpty());
		Assertions.assertFalse(finalResult.hasContent());
		
	}
	
	//testando ordenação por nome
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByName () {
		PageRequest pageRequest = PageRequest.of(0, 25,Sort.by("name"));
		
		Page<ProductDTO> finalResult = service.findAllPaged(0L,"",pageRequest);
		
		Assertions.assertFalse(finalResult.isEmpty());
		Assertions.assertEquals("Macbook Pro", finalResult.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", finalResult.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", finalResult.getContent().get(2).getName());
		Assertions.assertEquals("PC Gamer Boo", finalResult.getContent().get(3).getName());
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortByPrice () {
		PageRequest pageRequest = PageRequest.of(0, 25,Sort.by("price"));
		
		Page<ProductDTO> finalResult = service.findAllPaged(0L,"",pageRequest);
		
		Assertions.assertFalse(finalResult.isEmpty());
		Assertions.assertEquals(90.50, finalResult.getContent().get(0).getPrice());
		Assertions.assertEquals(100.99, finalResult.getContent().get(1).getPrice());		
	}	
	
	
}
