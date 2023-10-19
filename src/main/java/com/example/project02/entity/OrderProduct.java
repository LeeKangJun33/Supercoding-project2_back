package com.example.project02.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class OrderProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne

    @JoinColumn(name = "product_id")
    private Product product;

    private double price;

    private int count;

    private long userId;

    private String category;

    public static OrderProduct createOrderProduct(Product product, double productPrice, int amount) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setPrice(productPrice);
        orderProduct.setCount(amount);
        product.removeStock(amount);
        return orderProduct;
    }


}
