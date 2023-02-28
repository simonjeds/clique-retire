package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.PedidoSeparacaoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.SeparacaoPedido;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.PedidoRepository;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.SeparacaoRepository;
import com.clique.retire.repository.drogatel.SeparacaoRepositoryImpl;
import com.clique.retire.util.SecurityUtils;
import com.clique.retire.util.ThreadUtils;
import com.clique.retire.util.WebUtils;

@Service
public class SeparacaoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeparacaoService.class);

	private static final Integer POLO_MAURICIO_ARAUJO = 188;
	private static final String SEM_PEDIDO_PARA_SEPARAR = "Não há pedidos para separar no momento";

	@Autowired
	private SeparacaoRepository separacaoRepository;

	@Autowired
	private SeparacaoRepositoryImpl separacaoRepositoryImpl;

	@Autowired
	private PedidoRepositoryCustom pedidoCustomRepository;

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private FilialService filialService;

	@Autowired
	private ControleIntranetRepositoryCustom controleIntranetRepository;

	@Autowired
	private ItemPedidoService itemPedidoService;

	@Autowired
	private CronometroService cronometroService;

	@Autowired
	private RegistroPedidoService registroPedidoService;

	@Autowired
	private PedidoFracionadoService pedidoFracionadoService;
	
	private static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado.";

	/**
	 * Método inicia a separação do pedido. <br/>
	 * para pedidos sem produto controlado a separação é iniciada e também
	 * finalizada, alem de iniciar o registro
	 *
	 * @return retorna um DTO com alguns status sobre o inicio da separação.
	 */
	public PedidoSeparacaoDTO iniciarSeparacaoPedidoLoja() {

		Integer codigoUsuario = SecurityUtils.getCodigoUsuarioLogado();
		String enderecoIp = WebUtils.getClientIp();

		LOGGER.info("[Clique&Retire] Iniciando separação - usuário: {} - IP: {}", codigoUsuario, enderecoIp);

		Integer codigoLoja = controleIntranetRepository.findFilialByIp(enderecoIp);
		atualizarFirebase(codigoLoja, true);

		if (codigoLoja == null) {
			throw new BusinessException(SEM_PEDIDO_PARA_SEPARAR);
		}

		Integer numeroPedidoPeloCodigoUsuario = this.obterPedidoEmSeparacaoPorUsuario(codigoUsuario, codigoLoja);
		if (numeroPedidoPeloCodigoUsuario != null) {
			Pedido pedido = pedidoService.bucarPedidoParaSeparacao(numeroPedidoPeloCodigoUsuario.longValue());
			pedido.setNumeroPedido(numeroPedidoPeloCodigoUsuario.longValue());
			return montarRetornoSeparacao(pedido, true);
		}

		Integer numeroPedido = iniciarSeparacao(codigoLoja, codigoUsuario);
		if (numeroPedido == null) {
			return montarRetornoSeparacao(null, false);
		}
		itemPedidoService.atualizarItensPedidoSeparacao(numeroPedido);

		Pedido pedido = pedidoService.bucarPedidoParaSeparacao(numeroPedido.longValue());
		if (pedidoService.isPedidoAraujoTemBalcao(numeroPedido)) {
			registroPedidoService.cadastrarRegistroECupomPedidoSIAC(pedido, codigoUsuario);
			registroPedidoService.atualizarFasePrePedido(pedido.getNumeroPedido());
		}
		
		pedidoService.normalizaPrecoPedidosSIAC(numeroPedido);
		pedidoService.criarHistoricoFasePedido(pedido, FasePedidoEnum.EM_SEPARACAO, FasePedidoEnum.ATENDIDO, codigoUsuario, pedido.getPolo().getCodigo());

		LOGGER.info("Pedido {} separado pelo usuário {}.", numeroPedido, codigoUsuario);
		atualizarFirebase(codigoLoja, false);

		return montarRetornoSeparacao(pedido, false);
	}

	private void atualizarFirebase(Integer codigoLoja, boolean isInicioSeparacao) {
		ThreadUtils.execute(() -> {
			boolean atualizarApenasNovosPedidos = BooleanUtils.isFalse(isInicioSeparacao);
			filialService.atualizarNovosPedidos(codigoLoja, atualizarApenasNovosPedidos);
			if (isInicioSeparacao) {
				cronometroService.atualizarDadosCronometro(codigoLoja);
			}
		});
	}

	private Integer obterPedidoEmSeparacaoPorUsuario(Integer codigoUsuario, Integer codigoLoja) {
		return pedidoCustomRepository.obterPedidoEmSeparacaoPorUsuario(codigoUsuario, codigoLoja);
	}

	private PedidoSeparacaoDTO montarRetornoSeparacao(Pedido pedido, boolean pedidoEmAndamento) {
		PedidoSeparacaoDTO dto = new PedidoSeparacaoDTO();
		dto.setPedidoAndamento(pedidoEmAndamento);

		if (pedido != null) {
			dto.setNumeroPedido(pedido.getNumeroPedido());
			dto.setPedidoAraujoTem(pedido.getCodigoFilialAraujoTem() != null);
			dto.setCodigoFilialGerencial(pedido.getCodigoFilialGerencial());
			dto.setPedidoSuperVendedor(pedido.getSuperVendedor() != null && "S".equals(pedido.getSuperVendedor().getDescricao()));
			dto.setEditado(pedidoService.isPedidoEditado(pedido.getNumeroPedido()));
			dto.setFracionado(pedidoFracionadoService.isFracionado(pedido.getNumeroPedido().intValue()));
		} else {
			dto.setSemPedido(true);
		}
		return dto;
	}

	private synchronized Integer iniciarSeparacao(Integer codigoLoja, Integer codigoUsuario) {
		separacaoRepositoryImpl.iniciarSeparacao(codigoLoja, codigoUsuario);
		return this.obterPedidoEmSeparacaoPorUsuario(codigoUsuario, codigoLoja);
	}

	public Integer obterQuantidadePedidosAguardandoSeparacao() {
		return separacaoRepository.obterQuantidadeSepararPoloEcommerce(POLO_MAURICIO_ARAUJO);
	}

	@Transactional(value = "drogatelTransactionManager")
	public Integer finalizarSeparacao(Integer numeroPedido) {
		SeparacaoPedido separacaoPedido =  separacaoRepository.obterSeparacaoEmAberto(numeroPedido);
		if (separacaoPedido != null && separacaoPedido.getDataTermino() == null) {
			separacaoPedido.setDataTermino(new Date());
			separacaoPedido.setSeparacaoFinalizada(SimNaoEnum.S);
			separacaoPedido.setUltimaAlteracao(new Date());
			separacaoRepository.save(separacaoPedido);
		}
		return numeroPedido;
	}

	public Pedido validarSeparacaoPedido(Long idPedido) {
		return pedidoRepository.findById(idPedido).orElseThrow(()-> new BusinessException(PEDIDO_NAO_ENCONTRADO));
	}

	public Optional<SeparacaoPedido> buscarSeparacaoPedidoPorIdPedido(Long id) {
		return separacaoRepository.obterSeparacaoEmAbertoPorIdPedido(id);
	}
}