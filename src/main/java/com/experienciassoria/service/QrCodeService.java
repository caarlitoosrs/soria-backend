package com.experienciassoria.service;

import com.experienciassoria.exception.ResourceNotFoundException;
import com.experienciassoria.model.ExperienciaUID;
import com.experienciassoria.repository.ExperienciaUIDRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class QrCodeService {

    private final ExperienciaUIDRepository experienciaUIDRepository;
    private static final int QR_CODE_SIZE = 300;
    private static final String IMAGE_FORMAT = "PNG";

    public QrCodeService(ExperienciaUIDRepository experienciaUIDRepository) {
        this.experienciaUIDRepository = experienciaUIDRepository;
    }

    /**
     * Genera un código QR para un UID específico
     * El QR contiene el UID como texto plano para que el frontend lo pueda escanear
     * 
     * @param uidId ID del ExperienciaUID
     * @return Base64 encoded PNG image del QR code
     */
    public String generarQrCode(UUID uidId) {
        log.info("Generando QR code para UID: {}", uidId);
        
        ExperienciaUID experienciaUID = experienciaUIDRepository.findById(uidId)
                .orElseThrow(() -> new ResourceNotFoundException("UID no encontrado"));

        String uid = experienciaUID.getUid();
        log.info("Generando QR code con UID: {}", uid);

        try {
            // Configurar hints para el QR code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            // Crear el QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(uid, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);

            // Convertir a imagen
            BufferedImage qrImage = new BufferedImage(QR_CODE_SIZE, QR_CODE_SIZE, BufferedImage.TYPE_INT_RGB);
            qrImage.createGraphics();

            Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, QR_CODE_SIZE, QR_CODE_SIZE);
            graphics.setColor(Color.BLACK);

            // Dibujar el QR code
            for (int x = 0; x < QR_CODE_SIZE; x++) {
                for (int y = 0; y < QR_CODE_SIZE; y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }

            // Convertir a Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, IMAGE_FORMAT, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.info("QR code generado exitosamente para UID: {}", uid);
            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("Error al generar QR code para UID: {}", uid, e);
            throw new RuntimeException("Error al generar el código QR", e);
        }
    }
}


