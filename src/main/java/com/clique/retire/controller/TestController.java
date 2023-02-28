package com.clique.retire.controller;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ImpressaoDTO;
import com.clique.retire.dto.PedidoFaltaDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.service.drogatel.AvaliacaoService;
import com.clique.retire.service.drogatel.CupomFiscalService;
import com.clique.retire.service.drogatel.FilialService;
import com.clique.retire.service.drogatel.ImpressaoPedidoService;
import com.clique.retire.service.drogatel.ParametroCliqueRetireService;
import com.clique.retire.service.drogatel.PedidoService;
import com.clique.retire.service.drogatel.RegistraFaltaService;
import com.clique.retire.util.WebUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/rest/test", produces = "application/json;charset=UTF-8")
public class TestController extends BaseController {

	private static final Integer COD_USUARIO = 2747;
	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ImpressaoPedidoService impressaoPedidoService;

	@Autowired
	private CupomFiscalService cupomFiscalService;

	@Autowired
	private RegistraFaltaService registraFaltaService;

	@Autowired
	private FilialService filialService;

	@Autowired
	private ParametroCliqueRetireService parametroCliqueRetireService;

	@Autowired
	private AvaliacaoService avaliacaoService;

	@GetMapping(value="/imprimir-pedido", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] imprimirPedido(@ApiParam(value = "numeroPedido") Long numeroPedido) {
		try {
			ImpressaoDTO impressaoDTO = impressaoPedidoService.imprimirPedido(numeroPedido, COD_USUARIO, false);
			return Base64.getDecoder().decode(impressaoDTO.getDocumento());
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	@GetMapping(value="/imprimir-termo-entrega", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] imprimirTermoEntrega(@ApiParam(value = "numeroPedido") @RequestParam("numeroPedido") Long numeroPedido) {
		byte[] pdfFile = null;

		try {
			pdfFile = impressaoPedidoService.imprimirTermoEntrega(numeroPedido);
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}

		return pdfFile;
	}

	@GetMapping(value = "/consultar-pedido")
	public ResponseEntity<BaseResponseDTO> consultarPedido(
			@ApiParam("numeroPedido") @RequestParam("numeroPedido") Long numeroPedido) {
		return ok(pedidoService.buscarPorId(numeroPedido));
	}

	@GetMapping(value = "/buscar-filial")
	@ApiOperation(value = "Retorna o código da filial pelo IP", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> getFilialAtual(@RequestHeader String ip) {
		try {
			String ipCliente = WebUtils.getClientIp();

			return ok(filialService.consultarFilialParaAcessoPainel(ipCliente));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return erro(e.getMessage());
		}
	}

	@GetMapping(value = "/registrar-pedido")
	@ApiOperation(value = "Registra um pedido com a finalidade de testes", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> registrarPedido (
			@ApiParam("numeroPedido") @RequestParam("numeroPedido") Long numeroPedido) {

		try {
			cupomFiscalService.confirmarEmissaoCupom(numeroPedido, COD_USUARIO);
		} catch (Exception e) {
			e.printStackTrace();
			return erro(e.getMessage());
		}

		return ok("sucesso");
	}

	@GetMapping(value = "/pesquisa-habilitada")
	@ApiOperation(value = "Verificar se a pesquisa está habilitada", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> pesquisaHabilitada() {
		return ok(Boolean.valueOf(parametroCliqueRetireService.buscarPorNome(ParametroEnum.AVALIACAO)));
	}

	@RequestMapping("/registrar-falta-produto-pedido")
	@ApiOperation(value = "Registra a falta de produtos", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
	public ResponseEntity<BaseResponseDTO> registrarFaltaProdutoPedido(
			@ApiParam("pedido") @RequestBody PedidoFaltaDTO pedidoFalta) {

		try {
			registraFaltaService.registrarFaltaProdutoPedido(pedidoFalta);
		} catch (Exception e) {
			return erro(e.getMessage());
		}
		return ok("ok");
	}

	@GetMapping("/buscar-pedido-apontar-falta")
	public ResponseEntity<BaseResponseDTO> buscarPedidoParaApontarFalta(@RequestParam Integer numeroPedido, @RequestHeader String ip) {

		String ipCliente = WebUtils.getClientIp();
		Integer codigoLoja = filialService.buscarFilial(ipCliente);
		PedidoFaltaDTO pedido = registraFaltaService.buscarPedidoParaApontamentoDeFalta(numeroPedido, codigoLoja);
		return ok(pedido);
	}

	@GetMapping(value="/imprimir-historico-nf", produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] imprimirHistoricoNF(
			@ApiParam(value = "chaveNotaFiscal") @RequestParam("chaveNotaFiscal") String chaveNotaFiscal, @RequestHeader String ip) {
		byte[] pdfFile = null;

		try {
			pdfFile = impressaoPedidoService.imprimirHistoricoNF(chaveNotaFiscal, WebUtils.getClientIp());
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}

		return pdfFile;
	}

	@GetMapping(value = "/realizar-avaliacao")
	public ResponseEntity<BaseResponseDTO> salvarAvaliacao(@RequestParam("nota") Integer nota,
			@RequestParam("comentario") String comentario){
		try {
			avaliacaoService.salvarAvaliacao("172.16.72.64", COD_USUARIO, comentario, nota);
		} catch (Exception e) {
			return erro(e.getMessage());
		}
		return ok("ok");
	}

	@GetMapping(value = "/validar-avaliacao")
	public ResponseEntity<BaseResponseDTO> validarAvaliacao(){
		try {
			avaliacaoService.validarAvaliacao(COD_USUARIO);
		} catch (Exception e) {
			return erro(e.getMessage());
		}
		return ok("ok");
	}

	@GetMapping(value="/separacao-randomica")
	@ApiOperation(value = "Retorna um número de pedido, de forma randômica.", notes = "")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Sucesso"),
			@ApiResponse(code = 403, message = "Acesso negado"),
			@ApiResponse(code = 500, message = "Erro ao processar sua requisição") })
    public ResponseEntity<BaseResponseDTO> iniciarSeparacaoRandomica() {
		return ok(pedidoService.buscarNumeroPedidoRandomico());
	}
}