package com.example.mercado.controller;

import com.example.mercado.model.Mercado;
import com.example.mercado.service.MercadoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/mercados")
@Slf4j
public class MercadoController {

    private MercadoService service;

    public MercadoController(MercadoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Mercado>> salvar(
            @RequestBody Mercado mercado) {
        return service.salvar(mercado)
                .map(atual -> ResponseEntity.ok().body(mercado));
    }
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Mercado>> atualizar(
            @RequestBody Mercado mercado,
            @PathVariable(value = "id") String id) {
        return service.atualizar(mercado, id)
                .map(atual -> ResponseEntity.ok().body(mercado));
    }
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Mercado>> buscarPorId(
            @PathVariable(value = "id") String id) {
        return service.buscarPorId(id)
                .map(mercado -> ResponseEntity.ok().body(mercado))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    @GetMapping("/nomes")
    public Mono<ResponseEntity<Flux<Mercado>>> buscarPorNomes(
            @RequestParam(value = "nome") String nome) {
        return service.buscarPorNomes(nome)
                .collectList()
                .map(mercados -> ResponseEntity.ok().body(Flux.fromIterable(mercados)))
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }
    @GetMapping()
    public Mono<ResponseEntity<Flux<Mercado>>> listarTodos() {
        return service.listarTodos()
                .collectList()
                .map(mercados -> ResponseEntity.ok().body(Flux.fromIterable(mercados)))
                .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remover(@PathVariable String id){
        return service.remover(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(
                        e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

}