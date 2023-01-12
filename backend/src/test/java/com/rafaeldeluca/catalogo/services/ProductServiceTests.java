package com.rafaeldeluca.catalogo.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;


import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.rafaeldeluca.catalogo.dto.ProductDTO;
import com.rafaeldeluca.catalogo.entities.Category;
import com.rafaeldeluca.catalogo.entities.Product;
import com.rafaeldeluca.catalogo.repositories.CategoryRepository;
import com.rafaeldeluca.catalogo.repositories.ProductRepository;
import com.rafaeldeluca.catalogo.services.exceptions.DataBaseException;
import com.rafaeldeluca.catalogo.services.exceptions.ResourceNotFoundException;
import com.rafaeldeluca.catalogo.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	//testes de unidade
	// testar a classe espefífica sem carregar o outro componente de qual ela depende
	// Preciso mocar as dependências para fazer os testes de unidade
	// Se carregar outros componentes, não será um teste de unidade, será um teste de integração.
	// service não vai ter acesso ao banco de dados real para fazer os testes
	// Ao criar um Mock é necessário configurar o comportamento simulado desse mock
	// A classe ProductService vai ter que mocar o comportamento da classe ProductRepository
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long integrityId;
	private PageImpl<Product> page; 	//tipo concreto que representa uma página de dados
	private Product product;
	private Product productReturn;
	private ProductDTO productDTO;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception {
		//id que existe no projeto vai do 1L até o 25L
		this.existingId = 2L;
		this.nonExistingId = 50L;
		this.integrityId = 5l;
		//id que não pode ser deletado que vai der erro de integridade de banco de dados. Porque deletou um produto que tenha venda
		// ou deletou um categoria que tem produto cadastrado.		
		product = Factory.createProduct();
		productReturn = Factory.createProduct();
		category = Factory.createCategory();
		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));
		
		//comportamento simulado para não fazer nada quando chamar um médoto deleteByid
		Mockito.doNothing().when(repository).deleteById(existingId);
		doNothing().when(repository).deleteById(existingId);
		//comportamento para um Id que não existe
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(integrityId);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(integrityId);
		
		//quando o método não é void, primeira vai ter o when e depois a action		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(productReturn);
		
		//findById com id que existe retorna um Optinal NÃO vazio
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(productReturn));
		//findById com id que existe retorna um Optinal vazio
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		
		// resolvendo o problema de paginatedList is null	
		Mockito.when(repository.search(any(),any(), any())).thenReturn(page);
		
		Mockito.when(repository.getOne(existingId)).thenReturn(productReturn);		
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);		
		
		}
	
		
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		/* a camada de serviço apenas vai chamar o método deleteByiD da camada de repository.
		* e a camada de repository que vai comunicar com o banco e deletar, mas camada de serviço não ve isso.
		* service.delete(ExistingId);
		* Não deve disparar exceção de informar um Id que existe */	
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
		verify(repository,times(1)).deleteById(existingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist () {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
		verify(repository,times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void delteShouldThrowResouceNotFoundExceptionWhenIntegrityId() {
		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(integrityId);
			});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(integrityId);
	}
	
	@Test
	public void findAllPagedShouldReturnPageOfProducts() {
		
		Pageable pageable = PageRequest.of(0, 12);
		Page<ProductDTO> pageOfProducts = service.findAllPaged(0L,"",pageable);
		
		Assertions.assertNotNull(pageOfProducts);
		//Mockito.verify(repository, Mockito.times(1)).findAll(pageable);	
	}
	
	@Test
	public void findByIdShouldRetunrProductDTOWhenIdExist() {
		
		//productDTO = new ProductDTO(product, product.getCategories());
		productDTO = service.findById(existingId);
		
		Assertions.assertNotNull(productDTO);
		Mockito.verify(repository,Mockito.times(1)).findById(existingId);		
		
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenNonExistId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		Mockito.verify(repository,Mockito.times(1)).findById(nonExistingId);	
		
	}
	
	@Test void updateShouldReturnProductDTOWhenIdExist() {		
		
		ProductDTO result = service.update(existingId, productDTO);
		Assertions.assertNotNull(result);
		
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenNonExistId() {		
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, productDTO);
		});		
		
	}
	
	@Test
	public void insertShouldAutoincrementProductWhenIdIsNull () {		
		
		productDTO.setId(null);		
		Assertions.assertNull(productDTO.getId());	
		
		ProductDTO result  = service.insert(productDTO);	
		Assertions.assertNotNull(result.getId());	
	}


}
