package com.example.project02.controller;

import com.example.project02.dto.AuthInfo;
import com.example.project02.dto.UserInfoResponse;
import com.example.project02.entity.User;
import com.example.project02.dto.UserRequest;
import com.example.project02.service.JwtTokenService;
import com.example.project02.entity.Order;
import com.example.project02.service.OrderService;
import com.example.project02.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    private final OrderService orderService;

    @GetMapping("/myPage/{id}")
    public String myPage(@PathVariable("id") Long id, Model model) {
        List<Order> order = orderService.findByUserId(id);

        model.addAttribute("orders",order);
        return "user/myPage";
    }

    @GetMapping("/myPage/userInfo")
    public ResponseEntity<UserInfoResponse> userInfo(AuthInfo authInfo) {
        UserInfoResponse userInfo = userService.getUserInfo(authInfo.getUserId());

        return ResponseEntity.ok().body(userInfo);
    }
  
    @GetMapping("/myPage/userInfo2")
    public User userInfo2(AuthInfo authInfo) {

        long userId = authInfo.getUserId();

        return userService.findUser(userId);
    }

    @GetMapping("/test")
    public String signupTest(){
        return "signup";
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody UserRequest userRequest) {
        Map<String, Object> response = new HashMap<>();

        User user = userService.findByEmail(userRequest.getEmail());
        if (user == null) {
            userService.signup(userRequest);

            response.put("status", "success");
            response.put("message", "회원가입이 성공적으로 되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "이미 가입된 정보입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequest userRequest) {
        ResponseEntity<?> errorResponse = validateUser(userRequest);
        if (errorResponse != null) return errorResponse;
        HttpHeaders headers = jwtTokenService.createToken(userRequest);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "로그인되었습니다.");
        return ResponseEntity.status(200).headers(headers).body(responseBody);

    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(AuthInfo authInfo) {

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "로그아웃되었습니다.");
        return ResponseEntity.status(200).body(responseBody);
    }

    @PatchMapping("/withdrawal")
    public ResponseEntity<?> withdrawal(AuthInfo authInfo) {
        userService.withdrawal(authInfo.getUserId());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "회원 탈퇴가 성공적으로 되었습니다.");
        return ResponseEntity.status(200).body(responseBody);
    }


    // 회원 정보 검증
    public ResponseEntity<?> validateUser(UserRequest userRequest) {
        userRequest.setPassword(userService.sha256(userRequest.getPassword()));
        User user = userService.findByEmailAndPassword(userRequest.getEmail(), userRequest.getPassword());
        Map<String, String> responseBody = new HashMap<>();

        if (user == null) {
            return userService.validateUser(userRequest);
        } else if (Objects.equals(user.getStatus(), "delete")) {
            responseBody.put("error", "유효한 회원이 아닙니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }
        return null;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
