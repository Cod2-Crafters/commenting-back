package com.codecrafter.commenting.controller;

import com.codecrafter.commenting.domain.dto.ApiResponse;
import com.codecrafter.commenting.domain.enumeration.ApiStatus;
import com.codecrafter.commenting.domain.request.EmailCertificationRequest;
import com.codecrafter.commenting.service.MailSendService;
import com.codecrafter.commenting.service.MailVerifyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailSendService mailSendService;
    private final MailVerifyService mailVerifyService;

    private static final String EXTERNAL_API_URL = "https://commenting-front.vercel.app/api/verify-email";

    @Operation(summary = "인증 메일 전송",
        description = """
                        ★해당 메일에 인증코드/링크 전송</br>
                        {host}/api/mail/send-certification
                        """)
    @PostMapping("/send-certification")
    public ResponseEntity<ApiResponse> sendCertificationNumber(@Validated @RequestBody EmailCertificationRequest request,
                                                                                        HttpServletRequest httpServletRequest)
                                                                        throws MessagingException, NoSuchAlgorithmException {
        mailSendService.sendEmailForCertification(request.getEmail(), httpServletRequest);
        return ResponseEntity.ok(ApiResponse.success(mailSendService));
    }

    @Operation(summary = "메일 인증",
        description = """
                        ★메일인증 및 계정활성화</br>
                        인증시간제한있음
                        {host}/api/mail/verify?certificationNumber=000000&email=jayce@crafter.com
                        """)
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyCertificationNumber(   @RequestParam(name = "email") String email,
                                                                    @RequestParam(name = "certificationNumber") String certificationNumber) {
        ApiResponse response = mailVerifyService.verifyEmail(email, certificationNumber);
        String redirectUrl = String.format(EXTERNAL_API_URL + "?email=%s&isVerify=%s", email, response.status());

        URI redirectUri = URI.create(redirectUrl);
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(redirectUri).build();
    }
}
