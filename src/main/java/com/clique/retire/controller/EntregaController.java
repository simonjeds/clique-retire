package com.clique.retire.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.LocalizarPedidoFiltroDTO;
import com.clique.retire.dto.PedidoServicoDTO;
import com.clique.retire.service.drogatel.EntregaPedidoService;
import com.clique.retire.util.WebUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/rest/entrega", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class EntregaController extends BaseController {

	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	private final EntregaPedidoService entregaPedidoService;

	@GetMapping("/buscar-pedido-entrega")
	@ApiOperation("Busca pedido na base do drogatel para entrega pelo código VTEX ou número do pedido")
	public ResponseEntity<BaseResponseDTO> buscaPedidoParaEntrega(
		@RequestParam String filtro, @RequestParam boolean isParceiro
	) {
		String ipCliente = WebUtils.getClientIp();
		return ok(entregaPedidoService.buscarPedidoParaEntrega(filtro, ipCliente, isParceiro));
	}

	@GetMapping("/buscar-pedidos")
	@ApiOperation("Busca pedidos na base do drogatel conforme filtro")
	public ResponseEntity<BaseResponseDTO> buscarPedidos(LocalizarPedidoFiltroDTO filtro) {
		return ok(entregaPedidoService.buscarPedidos(filtro));
	}

	@GetMapping("/buscar-status-pedido")
	@ApiOperation("Busca os status de um pedido")
	public ResponseEntity<BaseResponseDTO> buscarStatusPedido() {
		return ok(entregaPedidoService.buscarStatusPedido());
	}
	
	@GetMapping("/buscar-tipos-pedido")
	@ApiOperation("Busca os tipos de pedido")
	public ResponseEntity<BaseResponseDTO> buscarTipoPedido() {
		return ok(entregaPedidoService.buscarTipoPedido());
	}

	@GetMapping("/confirmar-entrega-pedido")
	@ApiOperation("Confirma a entrega de um pedido pelo código ecommerce")
	public ResponseEntity<BaseResponseDTO> confirmarEntregaPedido(@RequestParam Long numeroPedido) throws JsonProcessingException {
		String ipCliente = WebUtils.getClientIp();
		Integer codigoUsuario = getCodigoUsuarioLogado();

		log.info("Confirmando entrega para o pedido: {} - ip {} - codigoUsuario: {}", numeroPedido, ipCliente, codigoUsuario);
		entregaPedidoService.entregarPedido(numeroPedido, ipCliente, codigoUsuario, true);
		return ok(SUCCESS);
	}
	
	@PostMapping("/gerar-expedicao-pedido-servico")
	@ApiOperation("Gera a expedição de um pedido de serviço para a busca de receita médica na casa do cliente")
	public ResponseEntity<BaseResponseDTO> gerarExpedicaoPedidoServico(@RequestBody List<PedidoServicoDTO> listaPS) throws JsonProcessingException {
		log.info("Gerando expedição para busca de receita na casa do cliente");
		entregaPedidoService.gerarExpedicaoPedidoServico(listaPS);
		return ok(SUCCESS);
	}

	@GetMapping("/confirmar-entrega-pedido-pin")
	@ApiOperation("Confirma a entrega de um pedido pelo código ecommerce e código PIN do parceiro")
	public ResponseEntity<BaseResponseDTO> confirmarEntregaPedidoPIN(
		@RequestParam Long numeroPedido, @RequestParam String pin
	) throws JsonProcessingException {
		String ipCliente = WebUtils.getClientIp();
		Integer codigoUsuario = getCodigoUsuarioLogado();

		log.info(
			"Confirmando entrega com PIN para o pedido: {} - ip {} - codigoUsuario: {} - pin: {}",
			numeroPedido, ipCliente, codigoUsuario, pin
		);

		BaseResponseDTO response = this.entregaPedidoService.entregarPedidoComPin(numeroPedido, pin, ipCliente, codigoUsuario,true);
		HttpStatus httpStatus = HttpStatus.valueOf(Integer.parseInt(response.getStatus()));
		return ResponseEntity.status(httpStatus).body(response);
	}
	
	@GetMapping(value = "/super-vendedor/confirmar-entrega-pedido")
	@ApiOperation(value = "Confirma a entrega de um pedido pelo código ecommerce", notes = "")
	public ResponseEntity<BaseResponseDTO> confirmarEntregaPedidoSuperVendedor(
			@ApiParam(value = "numeroPedido") @RequestParam("numeroPedido") Long numeroPedido) throws JsonProcessingException {

		Integer codigoUsuario = getCodigoUsuarioLogado();
		log.info("Confirmando entrega para o pedido {} de Super Vendedor. CodigoUsuario: {}", numeroPedido, codigoUsuario);
		entregaPedidoService.entregarPedido(numeroPedido, null, codigoUsuario, false);
		return ok(SUCCESS);
	}
	
	@GetMapping(value = "/confirmar-entrega-pedido-motociclista")
	@ApiOperation(value = "Confirma a entrega de um pedido pelo código ecommerce", notes = "")
	public ResponseEntity<BaseResponseDTO> confirmarEntregaPedidoViaMotociclista(HttpServletRequest request,
			@ApiParam(value = "numeroPedido") @RequestParam("numeroPedido") Long numeroPedido,
			@ApiParam(value = "dataEntrega") @RequestParam("dataEntrega") String dtEntrega) throws ParseException {
		
		log.info("[confirmar-entrega-pedido-motociclista] numeroPedido: " + numeroPedido);
		Date dataEntrega = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(dtEntrega);
		entregaPedidoService.confirmarEntregaPedidoMotociclista(numeroPedido, dataEntrega);
		return ok(SUCCESS);
	}
	
	@GetMapping(value = "/confirmar-pedido-nao-entregue-motociclista")
	@ApiOperation(value = "Confirma a entrega de um pedido pelo código ecommerce", notes = "")
	public ResponseEntity<BaseResponseDTO> confirmarPedidoNaoEntregueMotociclista(HttpServletRequest request,
			@ApiParam(value = "numeroPedido") @RequestParam("numeroPedido") Long numeroPedido,
			@ApiParam(value = "motivo") @RequestParam("motivo") String motivo) {
		
		log.info("[confirmar-pedido-nao-entregue-motociclista] numeroPedido: " + numeroPedido);
		entregaPedidoService.confirmarPedidoNaoEntregueMotociclista(numeroPedido, motivo);
		return ok(SUCCESS);
	}

	@GetMapping(value = "/retornar-pedido-expedicao")
	@ApiOperation(value = "Retorna o pedido para a fase Aguardando Expedição", notes = "")
	public ResponseEntity<BaseResponseDTO> retornarPedidoExpedicao(HttpServletRequest request,
																   @ApiParam(value = "numeroPedido") @RequestParam("numeroPedido") Long numeroPedido,
																   @ApiParam(value = "motivo") @RequestParam("motivo") String motivo) {
		log.info("[retornar-pedido-expedicao] numeroPedido: " + numeroPedido);
		entregaPedidoService.retornarPedidoParaExpedicao(numeroPedido, motivo);
		return ok(SUCCESS);
	}
}
