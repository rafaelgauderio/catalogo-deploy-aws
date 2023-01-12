package com.rafaeldeluca.catalogo.tests;

import java.time.Instant;

import com.rafaeldeluca.catalogo.dto.ProductDTO;
import com.rafaeldeluca.catalogo.entities.Category;
import com.rafaeldeluca.catalogo.entities.Product;

public class Factory {
	
	public static Product createProduct () {
		Product product = new Product(1L,"Table","New Tablet",2000.0,"https://img.com/imagem.png", Instant.parse("2022-10-25T12:00:00Z"));
		product.getCategories().add(new Category(2L,"Electronics"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());		
		
	}
	
	public static Category createCategory() {
		return new Category(2L,"Electronics");
	}
}
