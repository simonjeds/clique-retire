package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ImpressaoDTO;
import com.clique.retire.dto.PedidoFaltaDTO;
import com.clique.retire.service.drogatel.FilialService;
import com.clique.retire.service.drogatel.ImpressaoPedidoService;
import com.clique.retire.service.drogatel.RegistraFaltaService;
import com.clique.retire.util.WebUtils;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/registra-falta", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
public class RegistraFaltaController extends BaseController {

	private final FilialService filialService;
	private final RegistraFaltaService registraFaltaService;
	private final ImpressaoPedidoService impressaoPedidoService;

	@GetMapping("/buscar-pedido-apontar-falta")
	public ResponseEntity<BaseResponseDTO> buscarPedidoParaApontarFalta(@RequestParam Integer numeroPedido, @RequestHeader("ip") String ip) {
		Integer codigoLoja = filialService.buscarFilial(WebUtils.getClientIp());
		PedidoFaltaDTO pedidoFalta = numeroPedido == null || numeroPedido == 0
			? registraFaltaService.buscarPedidoParaApontamentoDeFaltaUsuario(codigoLoja)
			: registraFaltaService.buscarPedidoParaApontamentoDeFalta(numeroPedido, codigoLoja);
		return ok(pedidoFalta);
	}

	@PostMapping("/registrar-falta-produto-pedido")
	@ApiOperation("Registra a falta de produtos")
	public ResponseEntity<BaseResponseDTO> registrarFaltaProdutoPedido(@RequestBody PedidoFaltaDTO pedidoFalta) {
		PedidoFaltaDTO response = registraFaltaService.registrarFaltaProdutoPedido(pedidoFalta);
		return ok(response);
	}

	@PostMapping("/imprimir-devolucao-pedido-falta-araujo-tem")
    public ImpressaoDTO imprimirDevolucaoPedidoFaltaAraujoTem(@RequestBody PedidoFaltaDTO pedidoFalta) {
		ImpressaoDTO comanda = impressaoPedidoService.imprimirDevolucaoPedidoFaltaAraujoTem(pedidoFalta);
		registraFaltaService.inserirHistoricoApontamentoFaltaAraujoTem(pedidoFalta);
    	return comanda;
    }

}
