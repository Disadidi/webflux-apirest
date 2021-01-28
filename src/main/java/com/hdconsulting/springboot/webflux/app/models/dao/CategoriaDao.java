package com.hdconsulting.springboot.webflux.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.hdconsulting.springboot.webflux.app.models.documents.Categoria;

import reactor.core.publisher.Mono;

public interface CategoriaDao extends ReactiveMongoRepository<Categoria, String> {

	public Mono<Categoria> findByNombre(String nombre);
}
