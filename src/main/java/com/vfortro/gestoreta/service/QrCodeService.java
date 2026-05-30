package com.vfortro.gestoreta.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {


    public byte[] generateQrCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        // Configura la matriz de bits con el texto y el formato QR
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        try (ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream()) {
            // Escribe la matriz como una imagen PNG en el flujo de salida
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    public String generateQrCodeBase64(String text, int width, int height) throws Exception {
        byte[] qrBytes = generateQrCode(text, width, height);
        String base64 = Base64.getEncoder().encodeToString(qrBytes);
        // Retornamos con el prefijo correcto para HTML
        return "data:image/png;base64," + base64;
    }
}
