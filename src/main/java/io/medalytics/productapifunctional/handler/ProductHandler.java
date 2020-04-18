package io.medalytics.productapifunctional.handler;

import io.medalytics.productapifunctional.model.Product;
import io.medalytics.productapifunctional.model.ProductEvent;
import io.medalytics.productapifunctional.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class ProductHandler {

    private ProductRepository repository;

    @Autowired
    public ProductHandler(ProductRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String id = request.pathVariable("id");

        Mono<Product> productMono = this.repository.findById(id);

        return productMono
                    .flatMap(product ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(productMono, Product.class)
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> saveProduct(ServerRequest request){
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono
                .flatMap(product ->
                        ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                        .body(repository.save(product), Product.class)
                        );
    }

    public Mono<ServerResponse> updateProduct(ServerRequest request){
        Mono<Product> existingProductMono = repository.findById("id");
        Mono<Product> productMono = request.bodyToMono(Product.class);

        return productMono.zipWith(existingProductMono,
                (product, existingProduct) ->
                        new Product(existingProduct.getId(), product.getName(), product.getPrice())
        )
                .flatMap(product ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                .body(repository.save(product), Product.class)
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request){
        String id = request.pathVariable("id");
        Mono<Product> productMono = repository.findById(id);
        Mono<ServerResponse> notFound = ServerResponse.notFound().build();

        return productMono
                .flatMap(product ->
                        ServerResponse.ok()
                            .build(repository.delete(product))
                        )
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> deleteAllProducts(ServerRequest request){
        return ServerResponse.ok()
                .build(repository.deleteAll());
    }

    public Mono<ServerResponse> getProductEvents(ServerRequest request) {
        Flux<ProductEvent> productEventFlux = Flux.interval(Duration.ofSeconds(1))
                .map(val ->
                        new ProductEvent(val, "ProductEvent")
                );

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(productEventFlux, ProductEvent.class);
    }

    public Mono<ServerResponse> getProducts(ServerRequest request){
        Flux<Product> productFlux = repository.findAll();
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productFlux, Product.class);
    }
}
