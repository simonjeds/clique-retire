package com.clique.retire.service.drogatel;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.FilialDTO;
import com.clique.retire.enums.FirebaseFieldEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.Filial;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.CancelamentoPedidoRepository;
import com.clique.retire.repository.drogatel.FilialRepositoryCustom;
import com.clique.retire.util.FirebaseUtil;
import com.clique.retire.util.ThreadUtils;

@Service
public class FilialService {

	private static final String FILIAL_NAO_CADASTRADA = "Não foi possível verificar a filial. Contate o administrador do sistema.";
	private static final String ESTACAO_DE_TRABALHO_NAO_AUTORIZADA = "Estação de trabalho não autorizada a acessar o Painel clique e retire. Entre em contato com o Administrador do sistema.";

	private final FirebaseUtil firebaseUtil;
	private final PedidoService pedidoService;
	private final CronometroService cronometroService;
	private final SinalizadorService sinalizadorService;
	private final FilialRepositoryCustom filialRepositoryCustom;
	private final CancelamentoPedidoRepository cancelamentoPedidoRepository;
	private final ControleIntranetRepositoryCustom controleIntranetRepository;

	public FilialService(FirebaseUtil firebaseUtil, @Lazy PedidoService pedidoService,
			CronometroService cronometroService, SinalizadorService sinalizadorService,
			FilialRepositoryCustom filialRepositoryCustom, CancelamentoPedidoRepository cancelamentoPedidoRepository,
			ControleIntranetRepositoryCustom controleIntranetRepository
	) {
		this.firebaseUtil = firebaseUtil;
		this.pedidoService = pedidoService;
		this.cronometroService = cronometroService;
		this.sinalizadorService = sinalizadorService;
		this.filialRepositoryCustom = filialRepositoryCustom;
		this.cancelamentoPedidoRepository = cancelamentoPedidoRepository;
		this.controleIntranetRepository = controleIntranetRepository;
	}

	public Integer buscarFilial(String ip) {
		Integer codigoFilial = controleIntranetRepository.findFilialByIp(ip);
		if (Objects.isNull(codigoFilial))
			throw new EntidadeNaoEncontradaException(FILIAL_NAO_CADASTRADA);
		
		return codigoFilial;
	}

	@Transactional("drogatelTransactionManager")
	public Integer consultarFilialParaAcessoPainel(String ipCliente) {
		Integer codigoFilial = controleIntranetRepository.findFilialByIp(ipCliente);
		if (codigoFilial == null || codigoFilial == 0) {
			throw new EntidadeNaoEncontradaException(ESTACAO_DE_TRABALHO_NAO_AUTORIZADA);
		}

		filialRepositoryCustom.findFilialById(codigoFilial);
		return codigoFilial;
	}

	public void atualizarNovosPedidos(Integer filial, boolean apenasNovosPedidos) {
		Integer novosPedidos = filialRepositoryCustom.buscarQuantidadeNovosPedidos(filial);
		firebaseUtil.updateCustomValueByLoja(filial, FirebaseFieldEnum.NOVOS_PEDIDOS, novosPedidos);
		sinalizadorService.sinalizarLojaLuxaFor(filial, novosPedidos);

		if (!apenasNovosPedidos) {
			this.atualizarDadosFirebase(filial, FirebaseFieldEnum.PEDIDOS_CANCELADOS, FirebaseFieldEnum.PEDIDOS_PENDENTES);
		}
	}

	public void atualizarDadosFirebase(Integer codigoFilial, FirebaseFieldEnum... fields) {
		Map<FirebaseFieldEnum, Object> dados = new EnumMap<>(FirebaseFieldEnum.class);

		List<FirebaseFieldEnum> fieldsList = Arrays.asList(fields);
		if (fieldsList.contains(FirebaseFieldEnum.PEDIDOS_PENDENTES)) {
			int pedidosPendentes = pedidoService.obterQuantidadeDePedidosPendentes(codigoFilial);
			dados.put(FirebaseFieldEnum.PEDIDOS_PENDENTES, pedidosPendentes);
		}

		if (fieldsList.contains(FirebaseFieldEnum.PEDIDOS_CANCELADOS)) {
			int pedidosCancelados = cancelamentoPedidoRepository.buscarQuantidadePedidoCancelamentoPorFilial(codigoFilial);
			dados.put(FirebaseFieldEnum.PEDIDOS_CANCELADOS, pedidosCancelados);
		}

		firebaseUtil.updateCustomValuesByLoja(codigoFilial, dados);
	}

	public Filial consultarFilial(String ip) {
		Integer codigoFilial = controleIntranetRepository.findFilialByIp(ip);
		if (codigoFilial == null)
			return null;

		return filialRepositoryCustom.findFilialById(codigoFilial);
	}

	public List<String> consultaIpsPorFilial(Integer idFilial) {
		return controleIntranetRepository.findIpsPorFilial(idFilial);
	}
	
	public FilialDTO findFilialByIDEtiqueta(Integer idFilial) {
		return filialRepositoryCustom.findFilialByIDEtiqueta(idFilial);
	}

	public void atualizarDadosFirebase(Integer filial) {
		ThreadUtils.execute(() -> {
			this.atualizarDadosFirebase(filial, FirebaseFieldEnum.PEDIDOS_PENDENTES, FirebaseFieldEnum.PEDIDOS_CANCELADOS);
			this.cronometroService.atualizarDadosCronometro(filial);
		});
	}

	public Integer obterIdFilialParaGerarAutorizacaoConvenio(Long numeroPedido) {
		Pedido pedido = pedidoService.findById(numeroPedido);

		switch (pedido.getTipoPedido()) {
			case ARAUJO_EXPRESS:
				return 8000;
			case DROGATEL:
				return 5000;
			case TELEMARKETING_ATIVO:
				return 7000;
			case E_COMMERCE:
			case APLICATIVO:
				return 6000;
			case PESSOA_JURIDICA:
				return 9000;
			case ARAUJOTEM:
				return pedido.getCodigoFilialAraujoTem();
			default:
				throw new IllegalArgumentException("Tipo pedido não permite convênio");
		}
	}

}