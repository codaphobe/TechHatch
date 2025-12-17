package api.techhatch.com.service;

import api.techhatch.com.model.OtpVerification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class OtpMailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.application.name}")
    private String appName;

    @Value("classpath:/static/techhatch_logo.png")
    Resource resourceFile;

    public void sendOtpEmail(String toEmail, Context context, OtpVerification.OtpPurpose purpose) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            String htmlContent = templateEngine.process("otp-mail", context);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(getSubject(purpose));
            helper.setText(htmlContent, true);
            helper.addInline("logo", new ClassPathResource("/static/techhatch_logo.png"), "image/png");
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    private String getSubject(OtpVerification.OtpPurpose purpose) {
        return switch (purpose) {
            case REGISTRATION -> appName + " - Verify Your Email";
            case PASSWORD_RESET -> appName + " - Reset Your Password";
            case LOGIN -> appName + " - Login Verification";
        };
    }

    //Deprecated
    @SuppressWarnings("MalformedFormatString")
    private String buildEmailBody(String otpCode, OtpVerification.OtpPurpose purpose) {
        String action = switch (purpose) {
            case REGISTRATION -> "verify your email";
            case PASSWORD_RESET -> "reset your password";
            case LOGIN -> "login to your account";
        };

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                 color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                        .otp-box { background: white; border: 2px dashed #667eea; border-radius: 8px;
                                  padding: 20px; text-align: center; margin: 20px 0; }
                        .otp-code { font-size: 32px; font-weight: bold; letter-spacing: 8px;
                                   color: #667eea; margin: 10px 0; }
                        .info { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px;
                               margin: 20px 0; border-radius: 4px; }
                        .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>%s</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>You requested to %s. Please use the following One-Time Password (OTP):</p>
                
                            <div class="otp-box">
                                <p style="margin: 0; font-size: 14px; color: #666;">Your OTP Code:</p>
                                <div class="otp-code">%s</div>
                                <p style="margin: 0; font-size: 12px; color: #999;">Valid for 10 minutes</p>
                            </div>
                
                            <div class="info">
                                <strong>⚠️ Important:</strong>
                                <ul style="margin: 10px 0; padding-left: 20px;">
                                    <li>This OTP is valid for <strong>10 minutes</strong></li>
                                    <li>Do not share this code with anyone</li>
                                    <li>If you didn't request this, please ignore this email</li>
                                </ul>
                            </div>
                
                            <p>If the code doesn't work, you can request a new one from the verification page.</p>
                
                            <p>Best regards,<br><strong>%s Team</strong></p>
                        </div>
                        <div class="footer">
                            <p>This is an automated email. Please do not reply.</p>
                            <p>&copy; 2025 %s. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(appName, action, otpCode, appName, appName);
    }
}
