package tech.ada.pagamento.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.ada.pagamento.model.*;
import tech.ada.pagamento.repository.TransacaoRepository;

@Service
public class PagamentoService {

    private TransacaoRepository transacaoRepository;

    public PagamentoService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public Mono<Comprovante> pagar(Pagamento pagamento) {

        WebClient webClient = WebClient.create("http://localhost:8080");
        Flux<Usuario> usuarios = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/usernames") // http://..users/usernames?users=bob,alice
                        .queryParam("users", pagamento.getParamUsuarios())
                        .build())
                .retrieve().bodyToFlux(Usuario.class);

        Mono<Comprovante> comprovanteMono = Flux.zip(usuarios, usuarios.skip(1))
                .map(tupla -> new Transacao(
                        tupla.getT1().getUsername(),
                        tupla.getT2().getUsername(),
                        pagamento.getValor()))
                .last()
                .flatMap(tx -> transacaoRepository.save(tx))
                .map(tx -> new Comprovante(
                        tx.getId(),
                        tx.getPagador(),
                        tx.getRecebedor(),
                        tx.getValor(),
                        tx.getData()))
                .flatMap(cmp -> {
                    return salvar(cmp);
                });

        return comprovanteMono;
    }

    private Mono<Comprovante> salvar(Comprovante cmp) {
        WebClient webClient = WebClient.create("http://localhost:8080");
        Mono<Comprovante> monoComprovante = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/pagamentos")
                        .build())
                .bodyValue(cmp)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve().bodyToMono(Comprovante.class);

        monoComprovante.log();

        return Mono.from(monoComprovante);
    }

}