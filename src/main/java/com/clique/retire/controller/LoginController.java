package com.clique.retire.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.UsuarioDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.infra.service.TokenAuthenticationService;
import com.clique.retire.model.drogatel.Filial;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.service.cosmos.UsuarioCosmosService;
import com.clique.retire.service.drogatel.FilialService;
import com.clique.retire.service.drogatel.MetricaPedidoService;
import com.clique.retire.service.drogatel.ParametroCliqueRetireService;
import com.clique.retire.util.WebUtils;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/rest/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
public class LoginController extends BaseController {

	private final FilialService filialService;
	private final UsuarioCosmosService usuarioService;
	private final MetricaPedidoService metricaPedidoService;
	private final DrogatelParametroRepository parametroRepository;
	private final ParametroCliqueRetireService parametroCliqueRetireService;
	

	@GetMapping("/validar-matricula")
	public ResponseEntity<BaseResponseDTO> validarMatricula(@RequestParam String matricula) {
		Integer codUsuario = usuarioService.validarMatricula(matricula);
		String jwt = TokenAuthenticationService.addAuthentication(codUsuario.toString());
		String versao =	parametroRepository.findByNome(ParametroEnum.CLIQUE_RETIRE_VERSAO_PAINEL.getDescricao()).getValor();

		UsuarioDTO usuario = UsuarioDTO.builder()
			.matricula(matricula)
			.token(jwt)
			.versao(versao)
			.build();
		return ok(usuario);
	}

	@GetMapping("/buscar-ip")
	public ResponseEntity<BaseResponseDTO> buscarIp() {
		String ipCliente = WebUtils.getClientIpTCP();
		Integer filial = this.filialService.consultarFilialParaAcessoPainel(ipCliente);

		this.filialService.atualizarDadosFirebase(filial);
		return ok(ipCliente);
	}

	@GetMapping("/buscar-filial")
	@ApiOperation("Retorna o código da filial pelo IP")
	public ResponseEntity<BaseResponseDTO> getFilialAtualTCP() {
		Integer filial = filialService.consultarFilialParaAcessoPainel(WebUtils.getClientIpTCP());
		return ok(filial);
	}

	@GetMapping("/buscar-filial-ip")
	@ApiOperation("Retorna o código da filial pelo IP")
	public ResponseEntity<BaseResponseDTO> buscarFilialPorIp(@RequestParam String ip) {
		Filial filial = filialService.consultarFilial(ip);
		return ok(filial);
	}

	@GetMapping("/buscar-ips-filial")
	@ApiOperation("Retorna o código da filial pelo IP")
	public ResponseEntity<BaseResponseDTO> buscarIpsPorFilial(@RequestParam Integer idFilial) {
		List<String> ips = filialService.consultaIpsPorFilial(idFilial);
		return ok(ips);
	}

	@GetMapping("/buscar-filial-sensedia")
	@ApiOperation("Retorna o código da filial pelo IP")
	public ResponseEntity<BaseResponseDTO> getFilialAtual() {
		Integer filial = filialService.consultarFilialParaAcessoPainel(WebUtils.getClientIp());
		return ok(filial);
	}

	@GetMapping("/pesquisa-habilitada")
	@ApiOperation("Verificar se a pesquisa está habilitada")
	public ResponseEntity<BaseResponseDTO> pesquisaHabilitada() {
		Boolean isPesquisaHabilitada = Boolean.valueOf(parametroCliqueRetireService.buscarPorNome(ParametroEnum.AVALIACAO));
		return ok(isPesquisaHabilitada);
	}

	@GetMapping("/NFe-habilitada")
	@ApiOperation("Verificar se a pesquisa está habilitada")
	public ResponseEntity<BaseResponseDTO> verificarNFeHabilitada() {
		try {
			String ipCliente = WebUtils.getClientIp();
			return ok(parametroCliqueRetireService.isLojaHabilitadaParaNFe(ipCliente));
		} catch (Exception e) {
			return ok(false);
		}
	}

	@GetMapping("/calcular-metricas")
	@ApiOperation("Calcula as métricas por filial")
	public ResponseEntity<BaseResponseDTO> calcularMetricas() {
		metricaPedidoService.calcularMetricasDoPedido();
		return ok(SUCCESS);
	}

	@GetMapping("/atualizar-metricas")
	@ApiOperation("Chama o método que envia os dados para o firebase")
	public ResponseEntity<BaseResponseDTO> atualizarMetricas() {
		metricaPedidoService.gerarMetricasFiliais();
		return ok(SUCCESS);
	}

}
