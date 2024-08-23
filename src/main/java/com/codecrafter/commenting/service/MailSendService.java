package com.codecrafter.commenting.service;

import com.codecrafter.commenting.config.mail.CertificationGenerator;
import com.codecrafter.commenting.domain.response.EmailCertificationResponse;
import com.codecrafter.commenting.repository.CertificationNumberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSendService {

    private static final String MAIL_TITLE_CERTIFICATION = "코멘팅 회원가입 인증 메일입니다.";    // 메일제목

    private final JavaMailSender mailSender;
    private final CertificationNumberRepository certificationNumberDao;
    private final CertificationGenerator generator;
    private final SpringTemplateEngine templateEngine;

    public EmailCertificationResponse sendEmailForCertification(String email, HttpServletRequest request) throws NoSuchAlgorithmException,
        MessagingException {

        String certificationNumber = generator.createCertificationNumber();
        String domainName = getDomainName(request);
        // 메일 본문내용
        String content = """
                        <html>
                        <body>
                        <h2>코멘팅 회원가입 인증 메일입니다.</h2>
                        <p>안녕하세요!</p>
                        <p>아래 버튼을 클릭하여 회원가입을 완료해 주세요:)</p>
                        <a href='%s/api/mail/verify?certificationNumber=%s&email=%s' 
                           style='display: inline-block; padding: 10px 20px; font-size: 16px; color: #ffffff; background-color: #007bff; text-decoration: none; border-radius: 5px;'>
                           회원가입 인증하기
                        </a>
                        </body>
                        </html>
                        """.formatted(domainName, certificationNumber, email);
        certificationNumberDao.saveCertificationNumber(email, certificationNumber);
        sendMail(email, content);
        return new EmailCertificationResponse(email, certificationNumber);
    }

    private void sendMail(String email, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject(MAIL_TITLE_CERTIFICATION);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }


    private String getDomainName(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        return String.format("%s://%s:%d", scheme, serverName, serverPort);
    }

    public void sendEmailNotice(String email, HttpServletRequest httpServletRequest, String cause, String url, String content, String nickName /*원인 + 원인주소 + 내용 + 아이디 */) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        String domainName = getDomainName(httpServletRequest);
        Map<String, String> map = new HashMap<>();

        map.put("nickName", nickName);
        map.put("cause", cause);
        map.put("content", content);
        map.put("url", domainName + url); // 해당 주소로 갈 수 잇게 해야함

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email); // 메일 수신자
            mimeMessageHelper.setSubject("새로운 " + cause + "이 도착했어요"); // 메일 제목
            mimeMessageHelper.setText(setContext(map), true); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage);

            log.info("Succeeded to send Email");
        } catch (Exception e) {
            log.info("Failed to send Email");
            throw new RuntimeException(e);
        }
    }

    public String setContext(Map<String, String> map) {
        Context context = new Context();
        map.forEach(context::setVariable);
        return templateEngine.process("notification", context);
    }
}
