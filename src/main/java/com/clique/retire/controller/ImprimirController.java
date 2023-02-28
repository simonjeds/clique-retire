package com.clique.retire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.service.drogatel.ImpressaoPedidoService;
import com.clique.retire.util.WebUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/rest/imprimir", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ImprimirController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImprimirController.class);

	@Autowired
	private ImpressaoPedidoService impressaoPedidoService;

	@GetMapping("/imprimir-pedido")
	@ApiOperation("Realiza a geração da comanda de separação do pedido e envia para impressora caso necessário.")
	public ResponseEntity<BaseResponseDTO> imprimirPedido(
		@RequestParam Long numeroPedido, @RequestParam(required = false, defaultValue = "true") boolean enviarParaImpressora
	) throws Exception {
		return ok(impressaoPedidoService.imprimirPedido(numeroPedido, getCodigoUsuarioLogado(), enviarParaImpressora));
	}

	@GetMapping(value="/imprimir-termo-entrega")
	@ApiOperation(value = "Gera um relatório de termo de entrega", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> imprimirTermoEntrega(@ApiParam(value = "numeroPedido") @RequestParam("numeroPedido") Long numeroPedido) {
		byte[] pdfFile = null;

		try {
			pdfFile = impressaoPedidoService.imprimirTermoEntrega(numeroPedido);
		} catch (BusinessException e) {
			return erro(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return erro(OCORREU_UM_ERRO);
		}

		return ok(pdfFile);
	}

	@GetMapping(value="/imprimir-historico-nf")
	@ApiOperation(value = "Gera um relatório de histórico de atendimento de NF", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> imprimirHistoricoNF(
			@ApiParam(value = "chaveNotaFiscal") @RequestParam("chaveNotaFiscal") String chaveNotaFiscal, @RequestHeader(value = "IP", required = true) String ip) {
		byte[] pdfFile = null;

		try {
			pdfFile = impressaoPedidoService.imprimirHistoricoNF(chaveNotaFiscal, WebUtils.getClientIp());
		} catch (BusinessException e) {
			return erro(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return erro(OCORREU_UM_ERRO);
		}

		return ok(pdfFile);
	}

	@GetMapping("imprimir-danfe")
	public ResponseEntity<byte[]> imprimirDanfe(@RequestParam String chaveNota) {
		byte[] conteudoDanfe = impressaoPedidoService.imprimirDanfe(chaveNota);
		return ResponseEntity
			.status(HttpStatus.OK)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
			.body(conteudoDanfe);
	}

	@GetMapping("/imprimir-receita-digital")
	@ApiOperation("Realiza a busca de pedidos com receita dgital e envia para impressora.")
	public ResponseEntity<BaseResponseDTO> imprimirReceitaDigital(
		@RequestParam Long numeroPedido, @RequestHeader(value = "IP", required = true) String ip) {
		return ok(impressaoPedidoService.imprimirReceitaDigital(numeroPedido, WebUtils.getClientIp()));
	}
}
