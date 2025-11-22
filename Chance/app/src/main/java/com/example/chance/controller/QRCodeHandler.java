package com.example.chance.controller;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.RGBLuminanceSource;

import java.io.ByteArrayOutputStream;

/**
 * QRCodeHandler handles generating and decoding QR codes for events and users.
 * It can encode event IDs, invitation tokens, or entrant info into QR format.
 */
public class QRCodeHandler {

    private static final int QR_SIZE = 512; // Size of generated QR bitmap

    // --- Generate QR Code for a given string (e.g., eventId or invitation token) ---
    public static Bitmap generateQRCode(String data) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);
        Bitmap bitmap = Bitmap.createBitmap(QR_SIZE, QR_SIZE, Bitmap.Config.RGB_565);

        for (int x = 0; x < QR_SIZE; x++) {
            for (int y = 0; y < QR_SIZE; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return bitmap;
    }

    // --- Convert QR bitmap to Base64 string (useful for storing in Firestore) ---
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    // --- Decode Base64 string back to bitmap ---
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // --- Decode QR code bitmap to extract text content ---
    public static String decodeQRCode(Bitmap qrBitmap) {
        try {
            int width = qrBitmap.getWidth();
            int height = qrBitmap.getHeight();
            int[] pixels = new int[width * height];
            qrBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(binaryBitmap).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public static String decodeQrCode(byte[] imageBytes, int imageWidth, int imageHeight) {
        try {
            // Convert the byte array (which should represent ARGB or RGB pixel data)
            // into an int[] that ZXing can understand.
            int[] pixels = new int[imageWidth * imageHeight];

            // Assuming imageBytes contains raw ARGB/RGB data (e.g., from ImageProxy YUV conversion).
            // If it's already ARGB_8888, you can wrap it directly. Otherwise, you may need
            // to convert YUV -> RGB first.
            for (int i = 0; i < pixels.length; i++) {
                int r = imageBytes[i * 3] & 0xFF;
                int g = imageBytes[i * 3 + 1] & 0xFF;
                int b = imageBytes[i * 3 + 2] & 0xFF;
                pixels[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }

            // Feed into ZXing
            RGBLuminanceSource source = new RGBLuminanceSource(imageWidth, imageHeight, pixels);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(binaryBitmap).getText();
        } catch (Exception e) {
            return null;
        }
    }

}
