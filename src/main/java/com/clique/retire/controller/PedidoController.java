package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.RegistroPedidoDTO;
import com.clique.retire.service.drogatel.FilialService;
import com.clique.retire.service.drogatel.PedidoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping(value = "/rest/pedido", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
public class PedidoController extends BaseController {

	private final FilialService filialService;
	private final PedidoService pedidoService;

	@GetMapping("/reportar-novo-pedido")
	@ApiOperation("Reporta um novo pedido ao painel")
	public ResponseEntity<BaseResponseDTO> reportarNovoPedido(@RequestParam Integer codigoFilial) {
		filialService.atualizarNovosPedidos(codigoFilial, true);
		return ok(SUCCESS);
	}

	@GetMapping("/registrar-fase")
	@ApiOperation("Define a fase do pedido conforme informado no parâmetro")
	public ResponseEntity<BaseResponseDTO> registrarNovaFasePedido(@RequestParam Long numeroPedido, @RequestParam String fase) {
		Integer codigoUsuario = 1;
		log.info("Registrando fase pedido [{}] - fase [{}] - [Operação manual]", numeroPedido, fase);
		pedidoService.registrarNovaFasePedido(numeroPedido, fase, codigoUsuario);
		return ok(SUCCESS);
	}

	@PostMapping("/registrar-fase")
	@ApiOperation("Define a fase do pedido conforme informado no parâmetro")
	public ResponseEntity<BaseResponseDTO> registrarNovaFasePedido(
		@RequestBody List<Long> numeroPedido, @RequestParam String fase
	) {
		Map<String, Map<Long, String>> erro = new HashMap<>();
		Integer codigoUsuario = 1;
		log.info("Registrando fase pedido [{}] - fase [{}] - [Operação manual]", numeroPedido, fase);
		Map<Long, String> pedidosComErro = pedidoService.registrarNovaFasePedido(numeroPedido, fase, codigoUsuario);

		if (!pedidosComErro.isEmpty()) {
			erro.put("erro", pedidosComErro);
		}

		return ok(erro);
	}

	@GetMapping("/definir-fase")
	@ApiOperation("Define a fase do pedido conforme informado no parâmetro")
	public ResponseEntity<BaseResponseDTO> registrarFasePedido(
		@RequestParam Integer numeroPedido, @RequestParam String fase
	) {
		Integer codigoUsuario = getCodigoUsuarioLogado();
		log.info("Registrando fase pedido [{}] - fase [{}] - usuario - [{}]", numeroPedido, fase, codigoUsuario);
		pedidoService.definirPedidoFasePedido(numeroPedido, fase, codigoUsuario);
		return ok(SUCCESS);
	}

	@GetMapping("/ajustar-pedido-emissao-nf")
	public ResponseEntity<BaseResponseDTO> ajustarPedidoParaEmissaoNotaFiscal(@RequestParam Integer numeroPedido) {
		pedidoService.ajustarPedidoParaEmissaoNotaFiscal(numeroPedido);
		return ok(SUCCESS);
	}

	@GetMapping("/cancelar-transferencia")
	@ApiOperation("Cancelar pedido de transferência")
	public ResponseEntity<BaseResponseDTO> cancelarPedidoTransferencia(@RequestParam Integer numeroPedido) {
		log.info("Cancelando transferência do pedido [{}]", numeroPedido);
		pedidoService.cancelarTransferencia(numeroPedido);
		return ok(SUCCESS);
	}

	@PostMapping("/registrar-pedido")
	@ApiOperation("Registrar pedido")
	public ResponseEntity<BaseResponseDTO> registrarPedido(@RequestParam Long numeroPedido) throws JsonProcessingException {
		log.info("Registrando pedido [{}]", numeroPedido);
		RegistroPedidoDTO dto = pedidoService.registrarPedido(numeroPedido);
		return  dto.isReceitaDigital() ? accepted(dto) : ok(dto);
	}

	@GetMapping("/buscar-pedidos-pendente")
	@ApiOperation("Busca todos os pedidos pendentes")
	public ResponseEntity<BaseResponseDTO> obterPedidosPendentes(@RequestHeader String ip) {
		return ok(pedidoService.obterPedidosPendente());
	}
	
	@GetMapping("/buscar-pedidos-pendente-vinte-cinco-dias")
	@ApiOperation("Busca pedidos pendentes com mais de 25 dias da emissão de NF")
	public ResponseEntity<BaseResponseDTO> obterPedidosPendentes25Dias(@RequestHeader String ip, 
			@RequestParam(value = "page", required = false, defaultValue = "1") int pagina) {
		return ok(pedidoService.obterPedidosPendente25Dias(pagina));
	}
	
	@GetMapping("/entrega-pedidos-pendentes")
    @ApiOperation("Define a fase do pedido conforme informado no parâmetro")
    public ResponseEntity<BaseResponseDTO> entregaPedidoPendente(@RequestParam Long numeroPedido) {
        log.info("Entrega pedido pendente [{}] ", numeroPedido);
        pedidoService.entregaPedidoPendente(numeroPedido);
        return ok(SUCCESS);
    }	
}
