package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.mail.CertificationGenerator;
import com.codecrafter.commenting.domain.response.EmailCertificationResponse;
import com.codecrafter.commenting.repository.CertificationNumberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSendService {

    private static final String MAIL_TITLE_CERTIFICATION = "코멘팅 회원가입 인증 메일입니다.";    // 메일제목
    private static final Object DOMAIN_NAME = "http://localhost:8080";

    private final JavaMailSender mailSender;
    private final CertificationNumberRepository certificationNumberDao;
    private final CertificationGenerator generator;

    public EmailCertificationResponse sendEmailForCertification(String email) throws NoSuchAlgorithmException,
        MessagingException {

        String certificationNumber = generator.createCertificationNumber();
        // 메일 본문내용
        String content = String.format("%s/api/mail/verify?certificationNumber=%s&email=%s   링크를 클릭해주세요.", DOMAIN_NAME, certificationNumber, email);
        certificationNumberDao.saveCertificationNumber(email, certificationNumber);
        sendMail(email, content);
        return new EmailCertificationResponse(email, certificationNumber);
    }

    private void sendMail(String email, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject(MAIL_TITLE_CERTIFICATION);
        helper.setText(content);
        mailSender.send(mimeMessage);
    }
}
