package com.example.project02.service;

import com.example.project02.dto.SelectProductRequest;
import com.example.project02.entity.*;
import com.example.project02.repository.*;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderProductRepository orderProductRepository;
    private final CartProductRepository cartProductRepository;

    public List<OrderProduct> findUserOrderProducts(Long userId) {
        return orderProductRepository.findByUserId(userId);
    }

    public List<Order> findByUserId(Long id) {
        return orderRepository.findByUserId(id);
    }

    @Transactional
    public void order(Long userId, SelectProductRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("조회되지 않는 회원"));

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() ->
                new RuntimeException("조회 불가"));

        if (cart.getTotalCount() == 0) {
            throw new RuntimeException("장바구니가 비어있습니다");
        }

        List<Long> cartProductIdList = request.getCartProductIdList();

        for (Long cartProductId : cartProductIdList) {
            CartProduct cartProduct = cartProductRepository.findByIdAndCartId(cartProductId, cart.getId());
            if (cartProduct == null) {
                throw new RuntimeException("조회되지 않는 id입니다.");
            }
        }

        if(cartProductIdList.isEmpty()) {
            cart.getCartProducts()
                    .stream()
                    .map(cartProduct -> {
                        Product product = cartProduct.getProduct();
                        double price = product.getPrice();
                        int amount = cartProduct.getAmount();
                        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, price*amount, amount);
                        orderProductRepository.save(orderProduct);

                        return Order.createOrder(user, orderProduct);
                    })
                    .forEach(order -> {
                        orderRepository.save(order);
                        Payment.createPayment(user, order);
                    });
            cartClear(cart);
            cartReset(cart);
            cartRepository.save(cart);
        } else {
            List<CartProduct> ordersProducts = cart.getCartProducts()
                    .stream()
                    .filter(cartProduct -> cartProductIdList.contains(cartProduct.getId()))
                    .collect(Collectors.toList());

            ordersProducts
                    .stream()
                    .map(cartProduct -> {
                        Product product = cartProduct.getProduct();
                        double price = product.getPrice();
                        int amount = cartProduct.getAmount();
                        OrderProduct orderProduct = OrderProduct.createOrderProduct(product, price*amount, amount);
                        orderProductRepository.save(orderProduct);

                        return Order.createOrder(user, orderProduct);
                    })
                    .forEach(order -> {
                        orderRepository.save(order);
                        Payment.createPayment(user, order);
                    });

            List<CartProduct> newCartProducts = cart.getCartProducts()
                    .stream()
                    .filter(cartProduct -> !cartProductIdList.contains(cartProduct.getId()))
                    .collect(Collectors.toList());

            cartClear(cart);
            cart.getCartProducts().addAll(newCartProducts);

            try {
                Integer totalAmount = cartRepository.calculateTotalAmountByCartId(cart.getId());
                cart.setTotalCount(totalAmount);

                Double totalPrice = cartRepository.calculateTotalPriceByCartId(cart.getId());
                cart.setTotalPrice(totalPrice);

                cartRepository.save(cart);
            } catch (NullPointerException e) {
                cartReset(cart);
                cartRepository.save(cart);
            }


        }
    }

    private void cartReset(Cart cart) {
        cart.setTotalPrice(0);
        cart.setTotalCount(0);
    }

    private void cartClear(Cart cart) {
        cart.getCartProducts().clear();
    }
}



