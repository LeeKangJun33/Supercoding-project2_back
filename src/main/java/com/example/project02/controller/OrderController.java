package com.example.project02.controller;

import com.example.project02.dto.AuthInfo;
import com.example.project02.dto.SelectProductRequest;
import com.example.project02.repository.CartRepository;
import com.example.project02.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartRepository cartRepository;

    @Operation(summary = "주문 요청", description = "장바구니를 조회했을 때 나오는 cartProductId를 입력, null이면 장바구니 전체 상품 주문")
    @PostMapping("/payments")
    public ResponseEntity<Map<String, String>> order(AuthInfo authInfo, @RequestBody SelectProductRequest request) {

       orderService.order(authInfo.getUserId(), request);

        String response = "결제 되었습니다";

        Map<String, String> message = new HashMap<>();
        message.put("message", response);

        return ResponseEntity.ok().body(message);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
