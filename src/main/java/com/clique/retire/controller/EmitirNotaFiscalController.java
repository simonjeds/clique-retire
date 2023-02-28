package com.clique.retire.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.PedidoNotaFiscalDTO;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.ResponseSAPConsultaApiDTO;
import com.clique.retire.service.drogatel.EmitirNotaFiscalService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(value = "/rest/emitir-nota-fiscal", produces = "application/json;charset=UTF-8")
public class EmitirNotaFiscalController extends BaseController {

	protected static final String ERRO_STATUS_CONTINGENCIA = "Erro ao verificar contingência para impressão da nota fiscal. Favor entrar em contato com o administrador.";

	private final EmitirNotaFiscalService emitirNotaFiscalService;

	@GetMapping("/consultar-pedido")
	public ResponseEntity<BaseResponseDTO> consultarPedido(@RequestParam Integer chavePedido, @RequestHeader(value = "IP", required = true) String ip) {
		return ok(emitirNotaFiscalService.consultarPedidoParaEmissaoNotaFiscalSap(chavePedido));
	}

	@GetMapping("/atualizar-vencimento-curto")
	public ResponseEntity<BaseResponseDTO> atualizarVencimentoCurto(
			@ApiParam("codItemPedido") @RequestParam("codItemPedido") Integer codItemPedido,
			@ApiParam("dataValidade") @RequestParam("dataValidade") String dataValidade,
			@ApiParam("dataSeparada") @RequestParam("dataSeparada") String dataSeparada){

		try {
			Integer codUsuario = getCodigoUsuarioLogado();
			emitirNotaFiscalService.atualizarVencimentoCurto(codUsuario, codItemPedido, dataValidade, dataSeparada);
			return ok("ok");
		} catch (BusinessException e) {
			return erro(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return erro(OCORREU_UM_ERRO);
		}
	}

	@GetMapping("/consultar-pedidos-notas-fiscais")
	public ResponseEntity<BaseResponseDTO> consultarNotasFiscais(
		@RequestParam String filtro, @RequestParam("comboStatus") Integer codStatus, @RequestHeader(value = "IP", required = true) String ip) {
		List<PedidoNotaFiscalDTO> pedidoNotaFiscal = emitirNotaFiscalService.consultarPedidoComHistoricoPendenciasNotasFiscais(filtro, codStatus);
		return ok(pedidoNotaFiscal.isEmpty() ? "Nenhum dado encontrado." : pedidoNotaFiscal);
	}

	@GetMapping("/habilitar-fluxo-sap")
	@ApiOperation("Verificar se o fluxo do SAP está ativo")
	public  ResponseEntity<BaseResponseDTO> consultarHabilitarFluxoSAP() {
		return ok(emitirNotaFiscalService.consultarHabilitarFluxoSAP());
	}

	@GetMapping("/consultar-status-contingencia")
	public ResponseEntity<BaseResponseDTO> consultarStatusContingencia(
			@ApiParam("numeroPedido") @RequestParam String numeroPedido) {

		try {
			ResponseSAPConsultaApiDTO statusContingencia = emitirNotaFiscalService.consultarStatusContingencia(numeroPedido);
			return ok(statusContingencia);
		} catch (BusinessException e) {

			return erro(ERRO_STATUS_CONTINGENCIA);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return erro(ERRO_STATUS_CONTINGENCIA) ;
		}
	}

	@PostMapping("/emitir-ordem-venda")
	public ResponseEntity<BaseResponseDTO> solicitarEmissaoNotaFiscal(@RequestParam String numeroPedido,@RequestHeader(value = "IP", required = true) String ip) {
		try {
			return ok(emitirNotaFiscalService.solicitarEmissaoNotaFiscal(numeroPedido));
		} catch (Exception e) {
			if (!(e instanceof BusinessException)) {
				log.error(e.getMessage(), e);
			}
			return erro(this.montarRetornoErroPBM(e.getMessage()));
		}
	}

}
