package com.clique.retire.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Component
public class QrCodeUtil {

	private static final String PNG = "PNG";

	/**
	 * Gera uma imagem contendo um qrcode a partir do texto passado
	 * @param text
	 * @param width
	 * @param height
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	public InputStream generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, PNG, bos);
		return new ByteArrayInputStream(bos.toByteArray());
	}
}