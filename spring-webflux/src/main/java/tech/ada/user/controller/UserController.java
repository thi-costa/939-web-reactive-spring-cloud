package tech.ada.user.controller;

import tech.ada.user.exception.UserNotFoundException;
import tech.ada.user.model.User;
import tech.ada.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    // 2nd alternativa ResponseEntity Mono/Flux externos flexível 3 formas
    // https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-ann-responseentity

    private UserService service;

    public UserController(UserService clienteService) {
        this.service = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<User>> salvar(@RequestBody User user) {
        return service.salvar(user)
            .map(atual -> ResponseEntity.ok().body(atual));
    }

    @GetMapping // TODO nao retorna noContent quando lista vazia
    public Mono<ResponseEntity<Flux<User>>> listar() {
        return service.listar()
            .collectList()
            .map(users -> ResponseEntity.ok().body(Flux.fromIterable(users)) )
            .switchIfEmpty(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getById(@PathVariable String id) {
        return service.buscarPorId(id)
            .map(atual -> ResponseEntity.ok().body(atual))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/usernames")
    public Flux<ResponseEntity<User>> getById(@RequestParam("user1") String user1, @RequestParam("user2") String user2) {
        return service.buscarPorUsernames(user1, user2)
            .map(atual -> ResponseEntity.ok().body(atual))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/test")
    public Flux<ResponseEntity<User>> test() {
        return Flux.just(ResponseEntity.ok().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> atualizar(@RequestBody UserRequest user, @PathVariable String id) {
        return service.atualizar(user.create(), id)
            .map(atual -> ResponseEntity.ok().body(atual))
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}") // TODO notFound nao funciona quando ID inexistente
    public Mono<ResponseEntity<Void>> remover(@PathVariable String id) {
        return service.remover(id)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .onErrorResume(e -> {
                switch (e) {
                    case UserNotFoundException u -> {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    default -> throw new IllegalStateException("unexpected value: " + e);
                }
            });
        // onErrorResume apenas exemplo > Pattern Matching for instanceof and Switch in Java
        // prefiram simplificar controller e centralizar tratar fluxos em exception handler
        // FIXME consertem ao excluir um ID inexistente que retorne not found
    }

}