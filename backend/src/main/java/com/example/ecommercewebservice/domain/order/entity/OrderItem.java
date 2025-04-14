package com.example.ecommercewebservice.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

import com.example.ecommercewebservice.domain.product.entity.Product;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    // Setter for bidirectional relationship with Order
    public void setOrder(Order order) {
        this.order = order;
    }
} 