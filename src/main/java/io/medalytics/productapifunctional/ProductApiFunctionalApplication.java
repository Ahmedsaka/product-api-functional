package io.medalytics.productapifunctional;

import io.medalytics.productapifunctional.handler.ProductHandler;
import io.medalytics.productapifunctional.model.Product;
import io.medalytics.productapifunctional.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ProductApiFunctionalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApiFunctionalApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ReactiveMongoOperations operations,ProductRepository productRepository){
        return args -> {
            Flux<Product> productFlux = Flux.just(
                    new Product(null, "Big Latte",2.99),
                    new Product(null, "Big Decaf",2.49),
                    new Product(null, "Green Tea",1.99),
                    new Product(null, "Cappuccino",3.19))
                    .flatMap(productRepository::save);

            productFlux
                    .thenMany(productRepository.findAll())
                    .subscribe(System.out::println);
        };
    }

    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {
        return route(GET("/products").and(accept(MediaType.APPLICATION_JSON)), handler::getProducts)
                .andRoute(POST("/products").and(contentType(MediaType.APPLICATION_JSON)), handler::saveProduct)
                .andRoute(GET("/products/events").and(accept(MediaType.TEXT_EVENT_STREAM)), handler::getProductEvents)
                .andRoute(DELETE("/products").and(accept(MediaType.APPLICATION_JSON)), handler::deleteAllProducts)
                .andRoute(GET("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::getProduct)
                .andRoute(PUT("/product/{id}").and(contentType(MediaType.APPLICATION_JSON)), handler::updateProduct)
                .andRoute(DELETE("/products/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::deleteProduct);
    }

}
