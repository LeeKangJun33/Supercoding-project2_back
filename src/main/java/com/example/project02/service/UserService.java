package com.example.project02.service;

import com.example.project02.converter.UserConverter;
import com.example.project02.dto.OrderResponse;
import com.example.project02.dto.UserInfoResponse;
import com.example.project02.dto.UserRequest;
import com.example.project02.entity.User;
import com.example.project02.repository.*;
import com.example.project02.service.sms.SmsCertificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    UserConverter userConverter = new UserConverter();
    private final SmsCertificationService smsCertificationService;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public User findUser(Long id) {
        User user = userRepository.findById(id).orElseGet(()->{
            return new User();
        });
        return user;
    }

    public void userInfoModification(User user) {
        User persistence =
                userRepository.findById(user.getId()).orElseThrow(()->{
                    return new IllegalArgumentException("회원 찾기 실패");
                });

    }

    public User findByEmail(String email) {
        var isUser = userRepository.findByEmail(email);
        return isUser.orElse(null);
    }

    public void signup(UserRequest userRequest) {
        isValidPassword(userRequest.getPassword());
        userRequest.setPassword(sha256(userRequest.getPassword()));
        userRequest.setAddress(userRequest.getAddress() + " " + userRequest.getDetailedAddress());
        User NewUser = userConverter.toEntity(userRequest);

        if(userRequest.getCertificationNumber() == null || userRequest.getCertificationNumber().isEmpty()){
            throw new RuntimeException("인증번호를 입력해주세요.");
        }

//        SmsCertification smsCertification = new SmsCertification(userRequest.getPhone(), userRequest.getCertificationNumber());
//        if (smsCertificationService.isVerify(smsCertification)) {
//            throw new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다.");
//        }
        userRepository.save(NewUser);
    }

    // 회원가입시 비밀번호 검증
    public void isValidPassword(String password) {
        // 비밀번호 길이 체크 (8 ~ 20 자)
        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("비밀번호는 8~20자 사이여야 합니다.");
        }

        boolean hasLetter = false;
        boolean hasDigit = false;

        // 영문자와 숫자의 조합인지 체크
        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
        }

        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException("비밀번호는 영문자와 숫자의 조합이어야 합니다.");
        }
    }

    public String sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            try (Formatter formatter = new Formatter()) {
                for (byte b : hash) {
                    formatter.format("%02x", b);
                }
                return formatter.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public User findByEmailAndPassword(String email, String password) {
        var isUser = userRepository.findFirstByEmailAndPasswordOrderByIdDesc(email, password);
        return isUser.orElse(null);
    }

    public ResponseEntity<?> validateUser(UserRequest userRequest) {
        Map<String, String> responseBody = new HashMap<>();

        if (findByEmail(userRequest.getEmail()) == null) {
            responseBody.put("error", "가입된 이메일이 아닙니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        } else if (findByEmailAndPassword(userRequest.getEmail(), userRequest.getPassword()) == null) {
            responseBody.put("error", "잘못된 비밀번호 입니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
        }

        return ResponseEntity.ok(responseBody);
    }

    public void withdrawal(long id) {
        var existingUser = userRepository.findByIdAndStatus(id, "REGISTERED");
        if (existingUser.isPresent()) {
            User deleteUser = existingUser.get();
            deleteUser.setStatus("delete");
            userRepository.save(deleteUser);
        }
    }

    public UserInfoResponse getUserInfo(Long userId) {

        Optional<User> user = Optional.ofNullable(userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("유저 없음")));


        if (user.isPresent()) {
            String name = user.get().getName();
            String email = user.get().getEmail();
            String address = user.get().getAddress();
            String phoneNumber = user.get().getPhone();

            List<OrderResponse> orderDetailByUserId = orderRepository.findOrderDetailByUserId(userId);

            return new UserInfoResponse(name, email, address, phoneNumber, orderDetailByUserId);

        } else throw new RuntimeException("신규 회원이면 장바구니 조회 안됨. 상품추가 후 조회가능");
    }
}
