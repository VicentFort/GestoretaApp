package com.vfortro.gestoreta.service;

import com.vfortro.gestoreta.model.inventory.Loan;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private Dotenv dotEnv = Dotenv.load();

    public void sendLoanReminderMail(String to, String subject, Long loanId, String fallaName) {
        try {
            // 1. Pasar las variables que el HTML necesita (${loanId} y ${fallaName})
            Context context = new Context();
            context.setVariable("loanId", loanId);
            context.setVariable("fallaName", fallaName);

            // 2. "ReturnLoan" es el nombre del archivo HTML sin el .html
            String htmlContent = templateEngine.process("ReturnLoan", context);

            // 3. Crear el correo de tipo MIME
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // 'true' indica que es HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            // Maneja la excepción según tus necesidades (logs, etc.)
            System.err.println("Error al enviar email de préstamo: " + e.getMessage());
        }
    }

    public void sendLoanCreationMail(String to, String subject, Loan loan) {
        try {
            String loanId = loan.getLoanId()+"";
            String fallaName = loan.getFalla().getName();
            String amount = loan.getAmount()+"";
            String loanDate = loan.getAcquisitionDate().toString();
            String returnDate = loan.getIdealReturnDate().toString();
            String item = loan.getItem().getName();
            Context context = new Context();
            context.setVariable("loanId", loanId);
            context.setVariable("fallaName", fallaName);
            context.setVariable("amount", amount);
            context.setVariable("loanDate", loanDate);
            context.setVariable("returnDate", returnDate);
            context.setVariable("item", item);

            String htmlContent = templateEngine.process("CreatedLoan", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // 'true' indica que es HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            // Maneja la excepción según tus necesidades (logs, etc.)
            System.err.println("Error al enviar email de préstamo: " + e.getMessage());
        }
    }

    public void sendLoanReturnedMail(String to, String subject, Loan loan) {
        try {
            String loanId = loan.getLoanId()+"";
            String contactName = loan.getContact().getName();
            String fallaName = loan.getFalla().getName();
            String amount = loan.getAmount()+"";
            String loanDate = loan.getAcquisitionDate().toString();
            String returnDate = loan.getIdealReturnDate().toString();
            String item = loan.getItem().getName();
            Context context = new Context();
            context.setVariable("loanId", loanId);
            context.setVariable("fallaName", fallaName);
            context.setVariable("amount", amount);
            context.setVariable("loanDate", loanDate);
            context.setVariable("returnDate", returnDate);
            context.setVariable("itemName", item);
            context.setVariable("contactName", contactName);

            String htmlContent = templateEngine.process("ReturnedLoan", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // 'true' indica que es HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            // Maneja la excepción según tus necesidades (logs, etc.)
            System.err.println("Error al enviar email de préstamo: " + e.getMessage());
        }
    }
}
