package com.rafaeldeluca.catalogo.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rafaeldeluca.catalogo.entities.Category;
import com.rafaeldeluca.catalogo.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	
	// Se a category é null, então não foi informado o parâmetro, então tem que buscar todas produtos sem filtar por
	// 	categoriaId, e não retornar uma lista vazia
	// Para evitar repetições de categorias usar a cláusula DISTINCT
	// name LIKE '%rafael%' equivalente name LIKE CONCAT('%',:name,'%'); busca quem tem rafael em algum lugar da String
	// função TRIM para tirar espaços em brancos antes e depois da String
	
	
	@Query(
			"SELECT DISTINCT objeto FROM Product objeto "
			+ "INNER JOIN objeto.categories cats "
			+ "WHERE (COALESCE(:categories) IS NULL OR cats IN :categories) "
			+ "AND (LOWER(objeto.name) LIKE LOWER(CONCAT('%',:name,'%')) )"
			)
	Page<Product> search(List<Category> categories, String name, Pageable pageable);
	
	// resolvendo o problema das n + 1 consultas para buscar as categorias
		// JOIN FETCH só funciona com página, não funciona com lista
		
	@Query("SELECT objeto FROM Product objeto "
			+ "JOIN FETCH objeto.categories "
			+ "WHERE objeto IN :products")
	List<Product> findProductsWithCategories(List<Product> products);
	
	
	

}
