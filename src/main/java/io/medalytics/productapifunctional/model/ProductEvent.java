package io.medalytics.productapifunctional.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductEvent {
    private Long eventId;
    private String eventType;
}
