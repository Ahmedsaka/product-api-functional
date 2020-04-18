package io.medalytics.productapifunctional.model;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
}
