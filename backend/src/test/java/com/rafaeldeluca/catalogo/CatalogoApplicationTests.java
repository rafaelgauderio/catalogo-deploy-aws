package com.rafaeldeluca.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CatalogoApplicationTests {

	@Test
	void contextLoads() {
		/* contenxto em um aplicação Spring são todos os componentes, infraestrutura básica de um projeto Spring.
		* fica lento carregar ele em todo o texto unitário
		* Carrendo o contexto da aplicação é lento, é usado em TESTE DE INTEGRAÇÃO.
		*/
	}
	

}
