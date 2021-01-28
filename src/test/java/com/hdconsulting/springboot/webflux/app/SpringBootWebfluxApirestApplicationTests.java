package com.hdconsulting.springboot.webflux.app;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.hamcrest.core.IsEqual;
import org.hamcrest.text.IsEmptyString;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hdconsulting.springboot.webflux.app.models.documents.Categoria;
import com.hdconsulting.springboot.webflux.app.models.documents.Producto;
import com.hdconsulting.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringBootWebfluxApirestApplicationTests {
	
	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ProductoService service;

	@Test
	void listarTest() {
		
		client.get()
		.uri("/api/v2/productos")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()//pour lancer
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Producto.class)
		//.hasSize(9) //
		.consumeWith(response -> {
			List<Producto> productos = response.getResponseBody();
			productos.forEach(p -> {
				System.out.println(p.getNombre());
			});
			// assertThat(productos.size() > 0).isTrue();
		})
		;
	}
	
	@Test
	void verTest() {
		
		String nombre = "Sony Camara HD Digital";
		Producto producto = service.buscarPorNombre(nombre).block();
		
		client.get()
		.uri("/api/v2/productos", Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()//pour lancer
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		/*.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo(nombre)*/
		.expectBody(Producto.class)
		.consumeWith(response -> {
			Producto p = response.getResponseBody();

			Assertions.assertThat(p.getId()).isNotEmpty();
			Assertions.assertThat(p.getNombre()).isEqualTo(nombre);
		});
	}
	
	@Test
	void crearTest() {
		
		Categoria categoria = service.findCategoriaByNombre("Meubles").block();
		String productoNombre = "Mesa comedor"; 
		Producto producto = new Producto(productoNombre, 100.0, categoria);
		
		client.post().uri("/api/v2/productos")
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(producto), Producto.class)
		.exchange() //envoyer la requete
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith(response -> {
			Producto p = response.getResponseBody();
			
			Assertions.assertThat(p.getId()).isNotEmpty();
			Assertions.assertThat(p.getNombre()).isEqualTo(productoNombre);
			Assertions.assertThat(p.getCategoria().getNombre()).isEqualTo("Meubles");
		});
	}
	
	@Test
	void editarTest() {
		Producto producto = service.findByNombre("Sony Notebook").block();
		Categoria categoria = service.findCategoriaById("Electronico").block();
		
		Producto productoEditado = new Producto("Asus Notebook", 700.00, categoria);
		
		client.put().uri("/api/v2/productos{id}", Collections.singletonMap("id", producto.getId()))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(productoEditado), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith(response -> {
			Producto p = response.getResponseBody();
			
			Assertions.assertThat(p.getId()).isNotEmpty();
		})
	}
		

}
