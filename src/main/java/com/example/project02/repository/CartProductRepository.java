package com.example.project02.repository;

import com.example.project02.entity.Cart;
import com.example.project02.entity.CartProduct;
import com.example.project02.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    CartProduct findByCartIdAndProductId(Long cartId, Long productId);

    List<CartProduct> findByCart(Cart userCart);
    CartProduct findByIdAndCartId(Long cpId, Long cartId);

    @Query("select cp.product from CartProduct cp where cp.id = :cpId and cp.cart.id = :cartId")
    Product findProduct(@Param("cpId") Long cpId, @Param("cartId") Long cartId);

}
