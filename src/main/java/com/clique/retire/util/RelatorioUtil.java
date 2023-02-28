package com.clique.retire.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Component
public class RelatorioUtil {

	private static final String LOGO_ARAUJO_PNG = "classpath:templates/logo.png";
	private static final String EMOJI_FELIZ = "classpath:templates/smile.png";
	private static final String EMOJI_TRISTE = "classpath:templates/sad.png";
	private static final String PALMAS = "classpath:templates/palmas.png";
	private static final String TESOURA = "classpath:templates/tesoura.png";
	private static final String EXCLAMACAO = "classpath:templates/exclamacao.png";
	private static final String EXCLAMACAO_CINZA = "classpath:templates/exclamacao_cinza.png";
    private static final String CORTE_AQUI = "classpath:templates/corteaqui.png";

	@Autowired
	private ResourceLoader resourceLoader;

	public InputStream getLogoAraujo(){
		try {
			return resourceLoader.getResource(LOGO_ARAUJO_PNG).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getEmojiFeliz(){
		try {
			return resourceLoader.getResource(EMOJI_FELIZ).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getEmojiTriste(){
		try {
			return resourceLoader.getResource(EMOJI_TRISTE).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getPalmas(){
		try {
			return resourceLoader.getResource(PALMAS).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public InputStream getTesoura(){
		try {
			return resourceLoader.getResource(TESOURA).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getExclamacao(){
		try {
			return resourceLoader.getResource(EXCLAMACAO).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    public InputStream getCorteAqui(){
        try {
			return resourceLoader.getResource(CORTE_AQUI).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public InputStream getExclamacaoCinza() {
		try {
			return resourceLoader.getResource(EXCLAMACAO_CINZA).getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gera um relatorio a partir dos parametros passados
	 * @param template
	 * @param destinationPath
	 * @param listData
	 * @param parametros
	 * @return
	 * @throws IOException 
	 * @throws JRException 
	 * @throws Exception
	 */
	public byte[] gerarRelatorio(String template, String destinationPath,
			List<?> listData, Map<String, Object> parametros) throws IOException, JRException {

		Resource resourceTemplate = resourceLoader.getResource(template);
		byte[] relatorio = null;

		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){

			JasperReport jasperReport = JasperCompileManager.compileReport(resourceTemplate.getInputStream());
			JasperPrint jasperPrint = JasperFillManager.
					fillReport(jasperReport, parametros, new JRBeanCollectionDataSource(listData));

			if(StringUtils.isNotBlank(destinationPath)) {
				ByteArrayOutputStream bosDestinationPath = new ByteArrayOutputStream();
				Resource resourceDestinationPath = resourceLoader.getResource(destinationPath);
				IOUtils.copy(resourceDestinationPath.getInputStream(), bosDestinationPath);
				SimpleOutputStreamExporterOutput fos = new SimpleOutputStreamExporterOutput(bosDestinationPath);
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(fos);
				exporter.exportReport();
			}

			JasperExportManager.exportReportToPdfStream(jasperPrint, bos);

			relatorio = bos.toByteArray();

		}

		return relatorio;
	}

	public JasperPrint gerarJasper(String template,
			List<?> listData, Map<String, Object> parametros ) throws JRException, IOException{

		Resource resourceTemplate = resourceLoader.getResource(template);
		JasperReport jasperReport = JasperCompileManager.compileReport(resourceTemplate.getInputStream());
		return JasperFillManager.
				fillReport(jasperReport, parametros, new JRBeanCollectionDataSource(listData));

	}

	public byte[]  export (List<JasperPrint> jasperPrints ) throws IOException, JRException {
		byte[] relatorio = null;

		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()){
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));

			exporter.exportReport();
//			JasperExportManager.exportReportToPdfStream(jasperPrints, bos)

			relatorio = bos.toByteArray();
		}

		return relatorio;


	}







}
