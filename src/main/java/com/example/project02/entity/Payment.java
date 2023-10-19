package com.example.project02.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String address;

    public void setOrder(Order order) {
        this.order = order;
        order.setPayment(this);
    }

    public static void createPayment(User user, Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAddress(user.getAddress());
    }


}
