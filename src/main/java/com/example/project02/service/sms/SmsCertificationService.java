package com.example.project02.service.sms;

import com.example.project02.dao.SmsCertification;
import com.example.project02.exception.AuthenticationNumberMismatchException;
import com.example.project02.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Random;

import static com.example.project02.service.sms.coolSmsConstants.SMS_TYPE;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsCertificationService {

    private final SmsCertification smsCertification;
    private final AppProperties appProperties;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(appProperties.getCoolSmsKey(), appProperties.getCoolSmsSecret(), "https://api.coolsms.co.kr");
    }

    public String makeRandomNumber() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public String makeSmsContent(String certificationNumber) {
        SmsMessageTemplate content = new SmsMessageTemplate();
        return content.builderCertificationContent(certificationNumber);
    }

    public HashMap<String, String> makeParams(String to, String text) {
        HashMap<String, String> params = new HashMap<>();
        params.put("from", appProperties.getCoolSmsFromPhoneNumber());
        params.put("type", SMS_TYPE);
        params.put("to", to);
        params.put("text", text);
        return params;
    }

    public void sendSms(String phone) {
        String randomNumber = makeRandomNumber();
        String content = makeSmsContent(randomNumber);

        Message message = new Message();
        message.setFrom(appProperties.getCoolSmsFromPhoneNumber());
        message.setTo(phone);
        message.setText(content);

        SingleMessageSendingRequest sendingRequest = new SingleMessageSendingRequest(message);
        SingleMessageSentResponse response = messageService.sendOne(sendingRequest);

        smsCertification.createSmsCertification(phone, randomNumber);
    }


    public void verifySms(com.example.project02.dto.SmsCertification requestDto) {
        if (isVerify(requestDto)) {
            throw new AuthenticationNumberMismatchException("인증번호가 일치하지 않습니다.");
        }
        smsCertification.removeSmsCertification(requestDto.getPhone());
    }

    public boolean isVerify(com.example.project02.dto.SmsCertification requestDto) {
        return !(smsCertification.hasKey(requestDto.getPhone()) &&
                smsCertification.getSmsCertification(requestDto.getPhone())
                        .equals(requestDto.getCertificationNumber()));
    }
}