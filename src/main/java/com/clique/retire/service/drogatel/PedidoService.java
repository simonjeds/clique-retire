package com.clique.retire.service.drogatel;

import static com.clique.retire.util.NumberUtil.add;
import static com.clique.retire.util.NumberUtil.subtract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.ItemPedidoSIACDTO;
import com.clique.retire.dto.ModalidadePagamentoDTO;
import com.clique.retire.dto.PagamentoDinheiroDTO;
import com.clique.retire.dto.PedidoDTO;
import com.clique.retire.dto.PedidoEditadoEmailDTO;
import com.clique.retire.dto.PedidoPendente25DiasDTO;
import com.clique.retire.dto.PedidoRetornoMotociclistaDTO;
import com.clique.retire.dto.ProdutoDTO;
import com.clique.retire.dto.RegistroPedidoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.FirebaseFieldEnum;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPagamentoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.HistoricoFasePedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Polo;
import com.clique.retire.model.drogatel.PrePedidoSiac;
import com.clique.retire.model.drogatel.ProblemaSeparacaoPedido;
import com.clique.retire.model.drogatel.SeparacaoPedido;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.cosmos.NotaFiscalRepositoryImpl;
import com.clique.retire.repository.drogatel.CupomFiscalRepository;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.repository.drogatel.HistoricoFasePedidoRepository;
import com.clique.retire.repository.drogatel.ItemPedidoRepositoryCustomImpl;
import com.clique.retire.repository.drogatel.NegociacaoRepositoryImpl;
import com.clique.retire.repository.drogatel.PedidoRepository;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.PedidoRepositoryImpl;
import com.clique.retire.repository.drogatel.ProblemaSeparacaoRepository;
import com.clique.retire.repository.drogatel.RegistroPedidoRepository;
import com.clique.retire.repository.drogatel.SeparacaoRepository;
import com.clique.retire.repository.drogatel.SeparacaoRepositoryImpl;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.FeignUtil;
import com.clique.retire.util.FirebaseUtil;
import com.clique.retire.util.SecurityUtils;
import com.clique.retire.util.ThreadUtils;
import com.clique.retire.util.WebUtils;
import com.clique.retire.wrapper.PageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PedidoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PedidoService.class);
	
	private static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado";
	private static final String ERRO_CRIACAO_PEDIDO_SERVICO = "O pedido de serviço para a coleta da receita não foi gerado. Contate o administrador do sistema.";
	private static final String NUMERO_PEDIDO_NAO_ENCONTRADO = "Número de pedido não encontrado!";

	@Autowired
	private PedidoRepositoryCustom repositoryCustom;

	@Autowired
	private SeparacaoService separacaoService;
	
	@Autowired
	private ParametroService parametroService;	
	
	@Autowired
	private CaptacaoService captacaoService;

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private PedidoRepositoryImpl pedidoRepositoryImpl;
	
	@Autowired
	private SeparacaoRepositoryImpl separacaoRepositoryImpl;
	
	@Autowired
	private NegociacaoRepositoryImpl negociacaoRepository;
	
	@Autowired
	private NotaFiscalRepositoryImpl notaFiscalRepository;
	
	@Autowired
	private ItemPedidoRepositoryCustomImpl itemPedidoCustomRepository;

	@Autowired
	private PedidoRepositoryCustom pedidoRepositoryCustom;

	@Autowired
	private com.clique.retire.repository.drogatel.PedidoMercadoriaRepositoryImpl pedidoMercadoriaRepositoryImpl;

	@Autowired
	private HistoricoFasePedidoRepository historicoFaseRepository;

	@Autowired
	private ItemPedidoService itemPedidoService;

	@Autowired
	private DrogatelParametroRepository drogatelParametroRepository;

	@Autowired
	private PedidoRetiradaLojaService pedidoRetiradaLojaService;

	@Autowired
	private ControleIntranetRepositoryCustom controleIntranetRepository;
	
	@Autowired
	private StatusPedidoEcommerceService statusPedidoEcommerceService;
	
	@Autowired
	private PedidoService pedidoServiceAutowired;
	
	@Autowired
	private EmitirNotaFiscalService emitirNotaFiscalService;
	
	@Autowired
	private SeparacaoRepository separacaoRepository;
	
	@Autowired
	private RegistroPedidoRepository registroPedidoRepository;
	
	@Autowired
	private CupomFiscalRepository cupomFiscalRepository;
	
	@Autowired
	private ProblemaSeparacaoRepository problemaSeparacaoRepository;

	@Autowired
	private FirebaseUtil firebaseUtil;

	@Autowired
	private RegistroPedidoService registroPedidoService;
	
	@Autowired
	private ReceitaProdutoControladoService receitaProdutoControladoService;
	
	@Autowired
	private CaptacaoPedidoService captacaoPedidoService;
	
	@Autowired
	private IMGService imgService;
	
	@Autowired
	private DrogatelService drogatelService;
	
	@Autowired
	private PedidoServicoService pedidoServicoService;
	
	@Autowired
	private SiacService siacService;
		

	@Transactional(value = "drogatelTransactionManager", propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	public Pedido buscarPorId(Long numeroPedido) {
		return pedidoRepository.findByNumeroPedido(numeroPedido);
	}

	public Pedido buscarPorIdComItens(Long numeroPedido) {
		return pedidoRepositoryImpl.buscarPedidoPorCodigoPedido(numeroPedido);
	}

	@Transactional("drogatelTransactionManager")
	public Pedido atualizarPedido(Pedido pedido) {
		return pedidoRepositoryCustom.atualizarPedido(pedido);
	}

	@Transactional(value = "drogatelTransactionManager", propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	public Pedido bucarPedidoParaSeparacao(Long numeroPedido) {
		Object[] obj = (Object[]) pedidoRepository.obterPedidoParaSeparacao(numeroPedido);
		Pedido pedido = new Pedido();
		pedido.setNumeroPedido((Long) obj[0]);
		Polo polo = new Polo();
		polo.setCodigo((Integer) obj[1]);
		pedido.setPolo(polo);
		pedido.setCodigoFilialGerencial((Integer) obj[2]);
		pedido.setFasePedido((FasePedidoEnum) obj[3]);
		pedido.setCodigoFilialAraujoTem(obj[4] != null ? Integer.valueOf(obj[4].toString()) : null);
		pedido.setItensPedido(itemPedidoService.obterItensPedido(pedido.getNumeroPedido()));
		pedido.setSuperVendedor(obj[5] != null ? SimNaoEnum.valueOf(obj[5].toString()) : null);
		return pedido;
	}

	@Transactional("drogatelTransactionManager")
	public void registrarNovaFasePedido(Long numeroPedido, String fase, Integer codigoUsuario) {
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido);

		if (Objects.isNull(pedido)) {
			throw new EntidadeNaoEncontradaException(NUMERO_PEDIDO_NAO_ENCONTRADO);
		}
		
		if (FasePedidoEnum.ATENDIDO.getChave().equals(fase)) {
			LOGGER.info("Total de itens do cupom fiscal apagados: {}" ,cupomFiscalRepository.deleteItemByNumeropedido(numeroPedido));
			LOGGER.info("Total de cupons fiscais apagados: {}" ,cupomFiscalRepository.deleteCupomByNumeropedido(numeroPedido));
			LOGGER.info("Total de registros de pedido apagados: {}" ,registroPedidoRepository.deleteByPedidoNumeroPedido(numeroPedido));
			LOGGER.info("Total de problemas de separação apagados: {}" ,problemaSeparacaoRepository.deleteByPedidoNumeroPedido(numeroPedido));
			LOGGER.info("Total de separacões apagadas: {}" ,separacaoRepository.deleteByNumeroPedido(numeroPedido.intValue()));
			LOGGER.info("Total de itens do pedido atualizados: {}" ,itemPedidoCustomRepository.updateItemPedido(numeroPedido));
		}
		
		if (FasePedidoEnum.CANCELADO.getChave().equals(fase) && notaFiscalRepository.existeNotaFiscal(numeroPedido)) {
			fase = FasePedidoEnum.DEVOLUCAO_TOTAL.getChave();
		}
		
		if (FasePedidoEnum.DEVOLUCAO_TOTAL.getChave().equals(fase)) {
			itemPedidoCustomRepository.ajustarPedidoParaDevolucaoTotal(numeroPedido);
		}
		
		if (!FasePedidoEnum.EM_SEPARACAO.getChave().equals(fase)) {
			// Se o pedido não estiver na fase EM SEPARAÇÃO, a separação é finalizada. 
			separacaoRepositoryImpl.finalizarSeparacao(numeroPedido);
		}

		if (!FasePedidoEnum.EM_NEGOCIACAO.getChave().equals(fase)) {
			// Se o pedido não estiver na fase EM NEGOCIAÇÃO, a negociação é finalizada.
			negociacaoRepository.finalizarNegociacao(numeroPedido);
		}
		
		if(FasePedidoEnum.AGUARDANDO_NEGOCIACAO.getChave().equals(fase)) {
			if(FasePedidoEnum.AGUARDANDO_MERCADORIA.equals(pedido.getFasePedido())) {
				LOGGER.info("Total de itens atualizado: {}" ,itemPedidoCustomRepository.updateItemPedido(numeroPedido));
				pedido.setInicioNegociacao(new Date());
				pedidoRepository.save(pedido);
			} else {
				throw new BusinessException("O pedido não está na fase AGUARDANDO_MERCADORIA!");
			}
			
		}
		
		registrarNovaFasePedido(pedido, FasePedidoEnum.buscarPorChave(fase),
				pedido.getFasePedido(), codigoUsuario, pedido.getPolo().getCodigo());
		
		pedidoServicoService.atualizarFasePedidoServico(pedido, FasePedidoEnum.buscarPorChave(fase));
	}

	@Transactional("drogatelTransactionManager")
	public void definirPedidoFasePedido(Integer numeroPedido, String fase, Integer codigoUsuario) {
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido.longValue());

		if (pedido == null)
			throw new BusinessException(NUMERO_PEDIDO_NAO_ENCONTRADO);

		if (pedido.getTipoPedido().equals(TipoPedidoEnum.ARAUJOTEM)) {
			this.registroPedidoService.finalizarRegistroPedido(numeroPedido);
		}
		
		separacaoRepositoryImpl.finalizarSeparacao(numeroPedido.longValue());
		negociacaoRepository.finalizarNegociacao(numeroPedido.longValue());
		
		registrarNovaFasePedido(pedido, FasePedidoEnum.buscarPorChave(fase),
				pedido.getFasePedido(), codigoUsuario, pedido.getPolo().getCodigo());
	}
	
	@Transactional("drogatelTransactionManager")
	public void ajustarPedidoParaEmissaoNotaFiscal(Integer numeroPedido) {
		LOGGER.info("Ajustando pedido {} para emissão de nota fiscal, para fase EM SEPARAÇÃO (06)", numeroPedido);
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido.longValue());
		
		if (pedido == null) throw new BusinessException(PEDIDO_NAO_ENCONTRADO);
		
		negociacaoRepository.finalizarNegociacao(numeroPedido.longValue());
		pedidoRepositoryImpl.ajustarQuantidadeSeparadaItemPedido(numeroPedido);
		
		registrarNovaFasePedido(
			pedido, FasePedidoEnum.EM_SEPARACAO, pedido.getFasePedido(), SecurityUtils.getCodigoUsuarioLogado(),
			pedido.getPolo().getCodigo()
		);
	}
	
	@Transactional("drogatelTransactionManager")
	public void registrarNovaFasePedido(Pedido pedido, FasePedidoEnum novaFase,
			FasePedidoEnum faseAtual,
			Integer codigoUsuario, Integer codigoPolo) {

		salvarHistoricoFasePedido(pedido, novaFase, faseAtual, codigoUsuario, codigoPolo);

		pedidoRepositoryCustom.alterarFasePedido(pedido.getNumeroPedido(), novaFase);
	}

	@Transactional("drogatelTransactionManager")
	public void criarHistoricoFasePedido(Pedido pedido, FasePedidoEnum novaFase, FasePedidoEnum faseAtual,
			Integer codigoUsuario, Integer codigoPolo) {

		salvarHistoricoFasePedido(pedido, novaFase, faseAtual, codigoUsuario, codigoPolo);

	}

	private void salvarHistoricoFasePedido(Pedido pedido, FasePedidoEnum novaFase, FasePedidoEnum faseAtual,
			Integer codigoUsuario, Integer codigoPolo) {
		if (!novaFase.equals(faseAtual)) {

			HistoricoFasePedido historicoFase = new HistoricoFasePedido(codigoUsuario.toString());
			historicoFase.setFasePedidoAtual(novaFase);
			historicoFase.setCodigoPolo(codigoPolo);
			historicoFase.setPedido(pedido);
			historicoFase.setDataEntrouFaseAtual(new Date());

			historicoFaseRepository.save(historicoFase);
			
			if (FasePedidoEnum.ENTREGUE.equals(novaFase) && (pedido.getTipoPedido().equals(TipoPedidoEnum.APLICATIVO) || pedido.getTipoPedido().equals(TipoPedidoEnum.E_COMMERCE))) {
				try {
					statusPedidoEcommerceService.atualizarStatusIntegradorVtex(pedido, FasePedidoEnum.ENTREGUE, codigoUsuario);
				} catch (JsonProcessingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Disponibiliza o pedido para ser integrado no controle de status ecommerce.
	 * @param numeroPedido
	 * @param idUsuario 
	 * @throws JsonProcessingException 
	 */
	public void diponibilizarPedidoParaIntegracaoEcommerce(Long numeroPedido, Integer idUsuario) throws JsonProcessingException {
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido);
		
		if (pedido.getTipoPedido().equals(TipoPedidoEnum.APLICATIVO) || pedido.getTipoPedido().equals(TipoPedidoEnum.E_COMMERCE)) {
			statusPedidoEcommerceService.atualizarStatusIntegracaoEcommerce(pedido, idUsuario);
		}
	}

	/**
	 * Método para verificar se a loja possui pedido atendido.
	 * 
	 * @param codigoFilial
	 * @return true se houver pedidos na fase atendido e false caso não haja.
	 */
	public boolean isExistePedidosAgSeparacaoPorFilial(Integer codigoFilial) {
		return pedidoRepositoryCustom.isExistePedidosAgSeparacaoPorFilial(codigoFilial);
	}

	public void obterNumeroAberturabox(Long numeroPedido, String numeroPedidoEcommerceCliente) {
		try {
			if (pedidoRepositoryCustom.isPedidoBoxSemCodigoAberturaPreenchido(numeroPedido.intValue())) {
				if (numeroPedidoEcommerceCliente == null) {
					numeroPedidoEcommerceCliente = pedidoRepositoryCustom.obterPedidoEcommerceCliente(numeroPedido.intValue());
				}

				String parametroCanais = drogatelParametroRepository.findByNome(ParametroEnum.CANAIS_VENDA_SERVICO_ARMARIO.getDescricao()).getValor();
				List<String> canais = Arrays.stream(parametroCanais.split(";"))
					.map(String::trim)
					.filter(StringUtils::isNotBlank)
					.collect(Collectors.toList());

				boolean canalUsaNovoServicoArmario = false;
				Pedido pedido = canais.isEmpty() ? null : pedidoRepository.findByNumeroPedido(numeroPedido);
				if(pedido != null){
					canalUsaNovoServicoArmario = canais.isEmpty() || canais.contains(pedido.getTipoPedido().getChave());
				}


				BaseResponseDTO dto;
				if (canalUsaNovoServicoArmario) {
					DrogatelParametro parametroUrl = drogatelParametroRepository.findByNome(ParametroEnum.URL_BASE_SERVICO_ARMARIO.getDescricao());
					dto = FeignUtil.getBackofficeClient(parametroUrl.getValor()).obterCodigoAberturaBoxSite(numeroPedidoEcommerceCliente);
				} else {
					DrogatelParametro parametroUrl = drogatelParametroRepository.findByNome(ParametroEnum.URL_BASE_SERVICOS_REST_DRGT_WEB.getDescricao());
					dto = FeignUtil.getBackofficeClient(parametroUrl.getValor()).obterCodigoAberturaBox(numeroPedidoEcommerceCliente);
				}

				if (dto != null && dto.getData() != null) {
					pedidoRetiradaLojaService.cadastrarPedidoRetiradaLoja(numeroPedido, (String) dto.getData());
				}
			}
		} catch (Exception e) {
			LOGGER.error("Erro ao obter token de abertura do armario. pedido: {}" + numeroPedido + " - " + e.getMessage(), e);
		}
	}

	public Integer buscarNumeroPedidoRandomico() {
		return pedidoRepositoryCustom.buscarNumeroPedidoRandomico();
	}

	public void cancelarTransferencia(Integer numeroPedido) {
		pedidoMercadoriaRepositoryImpl.cancelaTransferencia(numeroPedido);
	}

	public RegistroPedidoDTO registrarPedido(Long numeroPedido) throws JsonProcessingException {
		Integer idUsuario = SecurityUtils.getCodigoUsuarioLogado();
		RegistroPedidoDTO dto = new RegistroPedidoDTO();
		if (captacaoService.isSuperVendedor(numeroPedido)) {
			captacaoService.fluxoRegistroCaptacao(numeroPedido, idUsuario);
			emitirNotaFiscalService.emitirNotaFiscalSap(numeroPedido.toString());
			return dto;
		} else {
			dto.setPagamentoEmDinheiro(this.isPagamentoEmDinheiro(numeroPedido));
			if (receitaProdutoControladoService.isEntregaViaMotociclistaEContemControlado(numeroPedido.intValue())) {
				if (receitaProdutoControladoService.validarSeExisteReceitaComCaptacaoPendente(numeroPedido)) 
					captacaoPedidoService.gerarCaptacaoReceitaEEmitirEtiqueta(numeroPedido, false);

				dto.setReceitaDigital(imgService.isContemReceitaDigital(numeroPedido));
				if (!dto.isReceitaDigital() && !drogatelService.criarPedidoDeServico(numeroPedido)) 
					throw new BusinessException(ERRO_CRIACAO_PEDIDO_SERVICO);
			}
			
			pedidoServiceAutowired.registrarNovaFasePedido(numeroPedido, FasePedidoEnum.AGUARDANDO_RECEITA.getChave(), idUsuario);
			pedidoServiceAutowired.diponibilizarPedidoParaIntegracaoEcommerce(numeroPedido,idUsuario);
		}
		
		return dto;
	}

	public List<PedidoDTO> obterPedidosPendente() {
		Integer idFilial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
		List<PedidoDTO> pedidoDTOS = pedidoRepositoryCustom.obterPedidosPendente(idFilial);
		int pedidosPendentes = pedidoDTOS.size();
		ThreadUtils.execute(
			() -> firebaseUtil.updateCustomValueByLoja(idFilial, FirebaseFieldEnum.PEDIDOS_PENDENTES, pedidosPendentes)
		);
		
		pedidoDTOS.forEach(pedidoDTO -> {
			Long numeroPedido = Long.parseLong(pedidoDTO.getNumeroPedido().toString());
			List<PrePedidoSiac> listPrePedidoSiac = registroPedidoService.buscarPrePedidoSiac(numeroPedido);
			if (!listPrePedidoSiac.isEmpty()) 
				pedidoDTO.setPrePedido(
						listPrePedidoSiac.stream()
						 				 .map(prePedidoSiac -> prePedidoSiac.getNumeroPrePedido().toString())
						 				 .collect(Collectors.joining(";"))
				);
		});
		
		return pedidoDTOS;
	}
	
	public PageWrapper<PedidoPendente25DiasDTO> obterPedidosPendente25Dias(int pagina) {
		Integer idFilial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
		PageWrapper<PedidoPendente25DiasDTO> pedidoDTOS = pedidoRepositoryCustom.obterPedidosPendente25Dias(idFilial, pagina);
		int pedidosPendentes = pedidoDTOS.getConteudo().size();
//		ThreadUtils.execute(
//			() -> firebaseUtil.updateCustomValueByLoja(idFilial, FirebaseFieldEnum.PEDIDOS_PENDENTES, pedidosPendentes)
//		);
		
		return pedidoDTOS;
	}

	public int obterQuantidadeDePedidosPendentes(Integer filial) {
		return pedidoRepositoryCustom.obterQuantidadePedidosPendente(filial);
	}
	
	public PedidoDTO buscarPedidoPorId(Long id) {
		Optional<Pedido> pedido = pedidoRepository.findById(id);
		if (!pedido.isPresent()) {
			throw new BusinessException(PEDIDO_NAO_ENCONTRADO);
		}
		
		DrogatelParametro parametro = parametroService.buscarPorChave(ParametroEnum.APLICATIVOS_INTERNOS_URL_BASE_IMAGENS.getDescricao());
		String urlBaseImagem = parametro.getValor();
		return entityToDTO(pedido.get(), urlBaseImagem);
	}	
	
	private PedidoDTO entityToDTO(Pedido entity, String urlBaseImagem) {
		PedidoDTO dto = new PedidoDTO();
		dto.setFase(entity.getFasePedido());
		dto.setNumeroPedido(entity.getNumeroPedido().intValue());
		Optional<SeparacaoPedido> separacao = separacaoService.buscarSeparacaoPedidoPorIdPedido(entity.getNumeroPedido());
		dto.setDataAssociacao(separacao.isPresent() ? separacao.get().getDataInicio() : new Date());
		
		dto.setItensPedido(repositoryCustom.buscarProdutosPorPedido(entity.getNumeroPedido(), urlBaseImagem, false, null));
		agruparItensPedido(dto);
		
		dto.getItensPedido().stream()
		.forEach(prod -> buscarTipoProblema(entity, prod));
		
		return dto;
	}
	
	private void buscarTipoProblema(Pedido entity, ProdutoDTO ip) {
		if (entity.getProblemasSeparacao() != null && !entity.getProblemasSeparacao().isEmpty()) {
			Optional<ProblemaSeparacaoPedido> problema = entity.getProblemasSeparacao().stream()
					.filter(p -> p.getProduto().getCodigo().intValue() == ip.getIdProduto().intValue())
					.findFirst();
			
			if (problema.isPresent()) {
				ip.setTipoProblema(problema.get().getTipoProblema());
			}
		}
	}	
	
	private void agruparItensPedido(PedidoDTO pedido) {
		Map<Long, List<ProdutoDTO>> map = pedido.getItensPedido().stream()
				.collect(Collectors.groupingBy(ProdutoDTO::getIdProduto));
		
		List<ProdutoDTO> produtos = new ArrayList<>();
		ProdutoDTO produtoDTO = null;
		for (Entry<Long, List<ProdutoDTO>> item : map.entrySet()) {
			produtoDTO = item.getValue().get(0);
			
			produtoDTO.setQuantidadePedida(item.getValue().stream()
					  .map(ProdutoDTO::getQuantidadePedida)
					  .collect(Collectors.summingInt(Integer::intValue)));
			
			produtoDTO.setQuantidadeSeparada(item.getValue().stream()
					  .map(ProdutoDTO::getQuantidadeSeparada)
					  .collect(Collectors.summingInt(Integer::intValue)));
			produtos.add(produtoDTO);
		}
		
		pedido.setItensPedido(produtos);
	}

	@Transactional("drogatelTransactionManager")
	public Map<Long, String> registrarNovaFasePedido(List<Long> numeroPedido, String fase, Integer codigoUsuario) {
		
		Map<Long, String> pedidosNaoAteradoFase = new HashMap<>();
		
		for (Long idPedido : numeroPedido) {
			try {
				
				registrarNovaFasePedido(idPedido, fase, codigoUsuario);
				
			}catch(Exception e) {
 				LOGGER.error("Erro ao alterar fase do pedido: {} , {}", idPedido, e);
				pedidosNaoAteradoFase.put(idPedido, e.getMessage());
			}
		}
		
		return pedidosNaoAteradoFase;
	}

	public Pedido findById(Long numeroPedido) {
		return pedidoRepository.findById(numeroPedido).orElseThrow(() -> new BusinessException(PEDIDO_NAO_ENCONTRADO));
	}

	public PedidoRetornoMotociclistaDTO consultarPedidoRetornoMotociclista(String filtro) {
		return this.pedidoRepositoryCustom.consultarPedidoRetornoMotociclista(filtro);
	}

	public ExpedicaoPedido buscarExpedicaoPedidoPeloNumeroPedido(Integer numeroPedido) {
		return this.pedidoRepositoryCustom.buscarExpedicaoPedidoPorNumeroPedido(numeroPedido.longValue());
	}
	
	public List<String> obterItensEditadosComandaSeparacao(Long numeroPedido) {
		return itemPedidoCustomRepository.obterItensEditadosComandaSeparacao(numeroPedido);
	}
	
	public boolean isPedidoEditado(Long numeroPedido) {
		return !this.obterItensEditadosComandaSeparacao(numeroPedido).isEmpty();
	}

    public List<TipoPagamentoEnum> buscarTiposPagamentoPedido(Long numeroPedido) {
		return pedidoRepositoryCustom.buscarTiposPagamentoPedido(numeroPedido);
    }

	public Optional<ModalidadePagamentoDTO> buscarModalidadePagamentoCartaoPedido(Long numeroPedido) {
		return pedidoRepositoryCustom.buscarModalidadePagamentoPedido(numeroPedido);
	}

	public void atualizaValoresModalidadePagamento(ModalidadePagamentoDTO modalidadePagamento) {
		pedidoRepositoryCustom.atualizaValoresModalidadePagamento(modalidadePagamento);
	}

    public boolean isPedidoComConvenio(Long numeroPedido) {
		return pedidoRepositoryCustom.isPedidoComConvenio(numeroPedido);
    }

	public PedidoEditadoEmailDTO buscarDadosPedidoParaEnvioEmail(Long numeroPedido) {
		return pedidoRepositoryCustom.buscarDadosPedidoParaEnvioEmail(numeroPedido);
	}
	
	public boolean existeEmailCadastradoParaOCliente(Long numeroPedido) {
		String email = pedidoRepositoryCustom.obterEmailClientePedido(numeroPedido);
		return Objects.nonNull(email) && !email.trim().isEmpty();
	}
	
	public boolean isPagamentoEmDinheiro(Long numeroPedido) {
		return pedidoRepositoryCustom.isPagamentoEmDinheiro4ponto0Drogatel(numeroPedido);
	}
	
	@Transactional("drogatelTransactionManager")
	public void normalizaPrecoPedidosSIAC(Integer numeroPedido) {
		if (!isPagamentoEmDinheiro(numeroPedido.longValue())) 
			return;
		
		List<ItemPedidoSIACDTO> itensPrecoMaior = siacService.obterItensValorMaiorQueSIAC(numeroPedido);
		if (itensPrecoMaior.isEmpty()) {
			LOGGER.info("O pedido {} não possui item com valor maior que o cadastrado no SIAC.", numeroPedido);
			return;
		}
		
		PagamentoDinheiroDTO dinheiroDTO = pedidoRepositoryCustom.obterModalidadePagamentoDinheiro(numeroPedido);
    	if(Objects.nonNull(dinheiroDTO)){
    		LOGGER.info("Atualizando o preço do(s) iten(s) com valor maior que o cadastrado no SIAC.");
    		
    		double descontoTotal = 0; 
    		for (ItemPedidoSIACDTO item :  itensPrecoMaior) {
    			LOGGER.info("Atualizando ITEM: {}", item.getCodigoItemPedido());
    			
    			descontoTotal += item.getDiferencaPreco();    			
    			itemPedidoService.atualizarPrecoItemPedidoSIAC(item.getCodigoItemPedido(), item.getNovoPreco());
    			itemPedidoService.registrarHistoricoAltPreco(item);
    		}

    		PedidoDTO pedidoDTO = pedidoRepositoryCustom.obtemPedidoParaNormalizarPreco(numeroPedido);
    		atualizaPrecoPedidoSIAC(dinheiroDTO, pedidoDTO, descontoTotal);

    		LOGGER.info("Total subtraído de {} do pedido {}.", descontoTotal, numeroPedido);
    	} 
	}
	
	private void atualizaPrecoPedidoSIAC(PagamentoDinheiroDTO dinheiroDTO, PedidoDTO pedidoDTO, double descontoTotal) {
		pedidoDTO.setTotalItensPedido(subtract(pedidoDTO.getTotalItensPedido(), descontoTotal, 2));
		pedidoDTO.setTotalPedido(subtract(pedidoDTO.getTotalPedido(), descontoTotal, 2));
		
		dinheiroDTO.setValor(subtract(dinheiroDTO.getValor(), descontoTotal, 2));
		dinheiroDTO.setTroco(add(dinheiroDTO.getTroco(), descontoTotal, 2));
		
		pedidoRepositoryCustom.atualizaPrecoPedidoSIAC(dinheiroDTO, pedidoDTO);
	}
	
	public boolean isPedidoCupomFiscal(Integer numeroPedido) {
		return "TRUE".equals(pedidoRepository.isPedidoCupomFiscal(numeroPedido));
	}
	
	public boolean isPedidoAraujoTemBalcao(Integer numeroPedido) {
		return "TRUE".equals(pedidoRepository.isPedidoAraujoTemBalcao(numeroPedido));
	}

	@Transactional("drogatelTransactionManager")
	public void entregaPedidoPendente(Long numeroPedido){
		Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido);
		if (Objects.isNull(pedido)) 
			throw new EntidadeNaoEncontradaException(NUMERO_PEDIDO_NAO_ENCONTRADO);

		if(!FasePedidoEnum.AGUARDANDO_EXPEDICAO.getChave().equals(pedido.getFasePedido().getChave()))
			throw new BusinessException("O pedido não está na fase AGUARDANDO_EXPEDICAO!");

		registrarNovaFasePedido(pedido, FasePedidoEnum.ENTREGUE,
				pedido.getFasePedido(), Constantes.USUARIO_ADMINISTRADOR, pedido.getPolo().getCodigo());
	}
}