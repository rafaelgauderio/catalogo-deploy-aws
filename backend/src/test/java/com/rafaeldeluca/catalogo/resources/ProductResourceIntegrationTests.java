package com.rafaeldeluca.catalogo.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafaeldeluca.catalogo.dto.ProductDTO;
import com.rafaeldeluca.catalogo.tests.Factory;
import com.rafaeldeluca.catalogo.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIntegrationTests {

	@Autowired
	private MockMvc mockMvc;	

	private Long existId;
	private Long nonExistId;
	private Long dbIntegrityId;
	private Long countTotalProducts;
	
	private String username;
	private String password;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;


	@BeforeEach
	void setUp() throws Exception {
		
		username= "alex@gmail.com";
		password = "123456";

		existId = 2L;
		nonExistId = 50L;
		countTotalProducts = 25L;
		dbIntegrityId = 2L;


	}

	@Test
	public void findAllShouldReturnSortedPageWhenSortByName () throws Exception {

		ResultActions resulted =
				mockMvc.perform(get("/products?page=0&size=25&sort=name,asc")
						.accept(MediaType.APPLICATION_JSON));

		resulted.andExpect(status().isOk());
		resulted.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		resulted.andExpect(jsonPath("$.size").value(countTotalProducts));
		resulted.andExpect(jsonPath("$.totalPages").value(1));
		resulted.andExpect(jsonPath("$.first").value(true));
		resulted.andExpect(jsonPath("$.last").value(true));
		resulted.andExpect(jsonPath("$.content").exists());
		resulted.andExpect(jsonPath("$.content").exists());
		resulted.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		resulted.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		resulted.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
		resulted.andExpect(jsonPath("$.content[3].name").value("PC Gamer Boo"));
	}


	@Test
	public void updateShouldRetunrProductDTOWhenIdExists() throws Exception {

		String acccessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		Double expectedPrice = productDTO.getPrice();
		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();
		String expectedImgURL = productDTO.getImgURL();

		//atualiza
		ResultActions resulted = 
				mockMvc.perform(put("/products/{id}",existId)
						.header("Authorization", "Bearer " + acccessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		//Assertions
		resulted.andExpect(status().isOk());
		resulted.andExpect(jsonPath("$.id").value(existId));
		resulted.andExpect(jsonPath("$.price").value(expectedPrice));
		resulted.andExpect(jsonPath("$.name").value(expectedName));
		resulted.andExpect(jsonPath("$.description").value(expectedDescription));
		resulted.andExpect(jsonPath("$.imgURL").value(expectedImgURL));
	}




	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		String acccessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions resulted = 
				mockMvc.perform(put("/products/{id}",nonExistId)
						.header("Authorization", "Bearer " + acccessToken)
						.content(jsonBody)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON));

		//404 - not Found se passar um Id que não existe no update
		resulted.andExpect(status().isNotFound());	

	}

	@Test
	public void insertShouldReturnCreatedAndProductDTO () throws Exception {
		
		String acccessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		ResultActions result = mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + acccessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		result.andExpect(jsonPath("$.price").exists());
		result.andExpect(jsonPath("$.imgURL").exists());
	}	

	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		String acccessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

		ResultActions result = mockMvc.perform(delete("/products/{id}",existId)
				.header("Authorization", "Bearer " + acccessToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());		
	}

	@Test
	public void deleteShoulReturnNotFoundWhenIdDoesNotExists () throws Exception {
		
		String acccessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);

		ResultActions result = mockMvc.perform(delete("/products/{id}",nonExistId)
				.header("Authorization", "Bearer " + acccessToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());

	}

	//Product não precisa desse teste de violação de integrade relacional
	// O que não pode é exlucir um categoria que já tem um produto cadastrado.
	// Product se existir o Id, tem que conseguir deletar.
	/*
	@Test
	@Transactional(propagation = Propagation.NEVER)
	public void deleteShouldReturnDataBaseExceptionWheIdDbIntegrityId () throws Exception {

		ResultActions result = mockMvc.perform(delete("/products/{id}",dbIntegrityId)
				.accept(MediaType.APPLICATION_JSON));
		// http 400 - Database Exception
		result.andExpect(status().isBadRequest());
	}
	*/


}

