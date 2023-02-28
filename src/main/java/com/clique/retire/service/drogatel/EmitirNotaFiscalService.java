package com.clique.retire.service.drogatel;

import static com.clique.retire.util.PbmUtils.isErroPBM;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ItemPedidoDTO;
import com.clique.retire.dto.PedidoDTO;
import com.clique.retire.dto.PedidoNotaFiscalDTO;
import com.clique.retire.dto.RegistroPedidoDTO;
import com.clique.retire.dto.ResponseValidarEnderecoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.StatusPedidoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.infra.exception.ErroValidacaoException;
import com.clique.retire.model.drogatel.HabilitadoSap;
import com.clique.retire.model.drogatel.HistoricoFasePedido;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.ResponseSAPConsultaApiDTO;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.model.drogatel.VencimentoCurto;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.cosmos.ImagemRepository;
import com.clique.retire.repository.drogatel.HabilitadoSapRepository;
import com.clique.retire.repository.drogatel.HistoricoFasePedidoRepository;
import com.clique.retire.repository.drogatel.IntegracaoCanaisVendasRepository;
import com.clique.retire.repository.drogatel.ItemPedidoRepository;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.ProcessamentoCanaisVendasRepository;
import com.clique.retire.repository.drogatel.UsuarioRepository;
import com.clique.retire.repository.drogatel.VencimentoCurtoRepository;
import com.clique.retire.service.drogatel.painel_monitoramento.PainelMonitoramentoIntegracaoService;
import com.clique.retire.util.SecurityUtils;
import com.clique.retire.util.WebUtils;
import com.google.gson.Gson;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmitirNotaFiscalService extends GeraToken {

	private static final String O_PEDIDO_NAO_ESTA_NA_FASE_DE_NF = "O pedido não está na fase para a execução da etapa de registro.";
	private static final String PEDIDO_NAO_PERTENCE_A_LOJA = "O pedido informado pertence a outra loja. Favor verificar.";
	private static final String PEDIDO_ARAUJO_TEM = "Esse pedido é do tipo ARAUJO TEM e por isso deve ser registrado no caixa.";
	private static final String PEDIDO_PAGAMENTO_BOLETO_PRAZO = "Tipo de pagamento desse pedido é boleto à prazo.";
	private static final Integer TIPO_PAGAMENTO_BOLETO = 10 ;
	private static final String HABILITADO_SAP = "S";
	private static final String SAP_ATIVO = "SAP_ATIVO" ;
	private static final String ERRO_CONFIRMAR_PAGAMENTO = "Não foi possível confirmar o pagamento deste pedido. Erro de comunicação com a plataforma vendas delivery. Favor entrar em contato com o administrador." ;
	private static final String ERRO_EMISSAO_NOTA_SAP = "Não foi possível solicitar da ordem de venda. Erro de comunicação com a plataforma vendas delivery. Favor entrar em contato com o administrador.";
	private static final String ERRO_TRATATIVA_VALIDA_ENDERECO = "Não foi possível realizar o tratamento do CEP da nota fiscal do pedido. Erro de comunicação com a plataforma vendas delivery. Favor entrar em contato com o administrador.";
	private static final String VINCULO_INCORRETO_LOTE_BIPADO = "A quantidade de item com medicamento controlado no pedido não corresponde ao(s) lote(s) bipado(s) e/ou vinculado(s) a(s) receita(s).";
	
	@Value("${cp.consulta.api}")
	private String urlIntegration;

	@Autowired
	private ControleIntranetRepositoryCustom controleIntranetRepository;
	
	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private PedidoRepositoryCustom pedidoRepositoryCustom;

	@Autowired
	private ImagemRepository imagemRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private VencimentoCurtoRepository vencimentoCurtoRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private HabilitadoSapRepository habilitadoSapRepository;
	
	@Autowired
	private IntegracaoCanaisVendasRepository integracaoCanaisVendasRepository;

	@Autowired
	private ProcessamentoCanaisVendasRepository processamentoCanaisVendasRepository;

	@Autowired
	private PedidoRepositoryCustom pedidoRepository;
	
	@Autowired
	PainelMonitoramentoIntegracaoService painelMonitoramentoIntegracaoService;
	
	@Autowired
	private SeparacaoService separacaoService;
	
	@Autowired
	private HistoricoFasePedidoRepository historicoFaseRepository;
	
	@Autowired
	private RegistroPedidoService registroPedidoService;
	
	@Autowired
	private LoteBipadoService loteBipadoService;
	
	@Autowired
	private ReceitaProdutoControladoService receitaProdutoControladoService;
	
	@Autowired
	private SiacService siacService;

	/**
	 * Consulta um Pedido com Historico de Pendencias de Notas Fiscais. <br/>
	 * 
	 * @return lista de pedidos e respectivos status de NF
	 */
	@Transactional(value = "drogatelTransactionManager", propagation = Propagation.REQUIRES_NEW)
	public List<PedidoNotaFiscalDTO> consultarPedidoComHistoricoPendenciasNotasFiscais(String filtro, Integer codStatus) {
		Integer filial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
		return pedidoRepositoryCustom.buscarPedidosNotasFiscaisPorFiltro(filtro, codStatus, filial);
	}

	@Transactional("cosmosTransactionManager")
	public void montarURLImagens(PedidoDTO pedido) {
		final String url = parametroService.buscarPorChave(ParametroEnum.URL_SERVICO_IMAGENS.getDescricao()).getValor();

		pedido.getItens().forEach(item -> {
			try {
				Integer codigo = imagemRepository.findImagemByCodigoProduto(item.getCodigoProduto());

				item.setUrlImagem(url.concat(String.valueOf(codigo)));
			} catch (Exception e) {
				// em caso de erro na busca de imagens do produto não trava a execução do fluxo.
				log.error("Erro ao buscar a imagem com código: [{}]. Exception: [{}]", item.getCodigoProduto(), e.getMessage());
			}
		});
	}

	public void atualizarVencimentoCurto(Integer codUsuario, Integer codItemPedido, String dataValidade,
			String dataSeparada) throws ParseException {

		if(codItemPedido == null || StringUtils.isBlank(dataValidade)){
			throw new ErroValidacaoException("Item código, data validade são obrigatórios.");
		}
		Usuario usuario = usuarioRepository.buscarPorCodigoUsuario(codUsuario);
		ItemPedido itemPedido = itemPedidoRepository.findByCodigo(codItemPedido);
		VencimentoCurto vencimentoCurto = vencimentoCurtoRepository.findByItemPedidoCodUsuario(codItemPedido,
				codUsuario);

		if (vencimentoCurto == null) {
			vencimentoCurto = new VencimentoCurto(codUsuario.toString());
		}

		vencimentoCurto.setUsuario(usuario);
		vencimentoCurto.setDataValidade(new SimpleDateFormat("dd/MM/yyyy").parse(dataValidade));
		vencimentoCurto.setDataValidadeSeparado(new SimpleDateFormat("dd/MM/yyyy").parse(dataSeparada));
		vencimentoCurto.setItemPedido(itemPedido);

		vencimentoCurtoRepository.save(vencimentoCurto);
	}
	
	public boolean consultarHabilitarFluxoSAP() {
		HabilitadoSap habilitarFluxoSap = habilitadoSapRepository.findByPlsTxtChave(SAP_ATIVO);
		return habilitarFluxoSap.getPlsIdCativaFluxoSap().equals(HABILITADO_SAP);
	}

	/**
	 * Retorna um produto e seus itens para registro de falta. <br/>
	 * 
	 * @param numeroPedido
	 * @return pedido para apontamento de faltas.
	 */
	@Transactional("drogatelTransactionManager")
	public PedidoDTO consultarPedidoParaEmissaoNotaFiscalSap(Integer numeroPedido) {
		PedidoDTO pedidoDTO = pedidoRepositoryCustom.buscarPedidoParaEmitirNotaFiscalSap(numeroPedido);

		if (pedidoDTO.getIdNotaFiscal() != null) {
			try {
				Map<String, Integer> obj = new LinkedHashMap<>();
				obj.put("numeroPedido", numeroPedido);
				Gson gson = new Gson();
				String body = gson.toJson(obj);
				ResponseValidarEnderecoDTO responseValidarEnderecoDTO = processamentoCanaisVendasRepository.validarEnderecoNotaFiscal(body);
				if (Boolean.FALSE.equals(responseValidarEnderecoDTO.getValidado())) {
					throw new BusinessException(responseValidarEnderecoDTO.getMessage());
				}
			} catch (FeignException e) {
				log.error(e.getMessage(), e);
				throw new BusinessException(ERRO_TRATATIVA_VALIDA_ENDERECO);
			}
		}

		validarPedidoParaEmissaoNotaFiscalSap(pedidoDTO, false);
		setaCodigoBarrasPorProduto(pedidoDTO.getItens());
		montarURLImagens(pedidoDTO);

		log.info("[Consulta - Registro Pedido]");
		log.info("Removendo o(s) lote(s) do pedido -> {}", numeroPedido);
		loteBipadoService.limparLoteBipadoPorNumeroPedido(numeroPedido);
		receitaProdutoControladoService.removerProdutoReceitaControlado(numeroPedido,true);

		return pedidoDTO;
	}

	private void validarPedidoParaEmissaoNotaFiscalSap(PedidoDTO pedido, boolean isEmissaoNF) {
		if (StringUtils.isNotBlank(pedido.getChaveNotaFiscal())) {
			throw new BusinessException(
				String.format("O pedido já possui nota fiscal emitida. Chave: %s", pedido.getChaveNotaFiscal())
			);
		}

		if (!SimNaoEnum.S.equals(pedido.getPedidoLoja())) {
			throw new BusinessException("O pedido é de polo, e por isto não está apto para emissão de NF em loja.");
		}

		if (FasePedidoEnum.EM_REGISTRO.equals(pedido.getFase())) {
			throw new BusinessException(
				String.format("Já existe solicitação de NF em processamento para o pedido %s", pedido.getNumeroPedido())
			);
		}

		validarFasePedidoEVinculoLoteCasoHajaControlado(pedido, isEmissaoNF);

		Integer filial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
		if (!filial.equals(pedido.getCodFilial())) {
			throw new BusinessException(PEDIDO_NAO_PERTENCE_A_LOJA);
		}

		if (pedido.isAraujoTem() && !pedido.isPedidoSuperVendedor() && pedido.getIdNotaFiscal() == null) {
			throw new BusinessException(PEDIDO_ARAUJO_TEM);
		}

		if (
			(
				TipoPedidoEnum.PESSOA_JURIDICA.equals(pedido.getTipoPedido())
				&& pedidoRepositoryCustom.isPagamentoSAP(pedido.getNumeroPedido(), TIPO_PAGAMENTO_BOLETO)
			) || pedido.isEcommerceAppCancelado()
		) {
			throw new BusinessException(PEDIDO_PAGAMENTO_BOLETO_PRAZO);
		}
	}
	
	public void validarFasePedidoEVinculoLoteCasoHajaControlado(PedidoDTO pedido, boolean isEmissaoNF) {
		if (isEmissaoNF) {
			if (!Arrays.asList(FasePedidoEnum.EM_SEPARACAO, FasePedidoEnum.AGUARDANDO_REGISTRO, FasePedidoEnum.AGUARDANDO_RECEITA).contains(pedido.getFase())) {
				throw new BusinessException(O_PEDIDO_NAO_ESTA_NA_FASE_DE_NF);
			}
			
			if (pedido.hasItemControlado() && loteBipadoService.existeLoteBipadoEOuReceitaExcedenteOuFaltante(pedido.getNumeroPedido())) {
				throw new BusinessException(VINCULO_INCORRETO_LOTE_BIPADO);
			}
			
		} else {
			if (!(FasePedidoEnum.EM_SEPARACAO.equals(pedido.getFase()) || 
				  (FasePedidoEnum.AGUARDANDO_REGISTRO.equals(pedido.getFase()) && !pedidoService.isPagamentoEmDinheiro(pedido.getNumeroPedido().longValue()) && 
						  														  (!pedido.hasItemControlado() || pedido.isPedidoSuperVendedor())
				  )
				 )) {
				throw new BusinessException(O_PEDIDO_NAO_ESTA_NA_FASE_DE_NF);
			}
		}
	}
	
	@Transactional(transactionManager="drogatelTransactionManager", noRollbackFor=RuntimeException.class)
	public ResponseSAPConsultaApiDTO consultarStatusContingencia(String numeroPedido) {
		log.info("Consultando status de contingência. Pedido -> {}", numeroPedido);
		try {
			ResponseSAPConsultaApiDTO statusContingencia = integracaoCanaisVendasRepository.statusContingencia(Integer.parseInt(numeroPedido));
			
			if(Boolean.TRUE.equals(statusContingencia.getStatusContigencia())) {
				updateFasePedido(
						StatusPedidoEnum.FASE_VERIFICAR_STATUS_CONTIGENCIA.name(), 
						null, 
						Integer.parseInt(numeroPedido));				
			}
			return statusContingencia;
		} catch (Exception e) {	
			updateFasePedido(
					StatusPedidoEnum.FASE_ERRO_VERIFICAR_STATUS_CONTIGENCIA.name(), 
					null,
					Integer.parseInt(numeroPedido));
			
			painelMonitoramentoIntegracaoService
			.integraPanelMonitoramentoAraujo(
					urlIntegration + "consulta-status-grc?NumeroPedido={NumeroPedido}",
					StatusPedidoEnum.FASE_ERRO_VERIFICAR_STATUS_CONTIGENCIA.name(), 
					StatusPedidoEnum.FASE_ERRO_VERIFICAR_STATUS_CONTIGENCIA.getDescricaoPainel(),
					null, numeroPedido);
			e.printStackTrace();
			throw new BusinessException(e.getMessage());
		}
	}
	
	/**
	 * Emite cupom ou nota fiscal no SAP
	 * 
	 * @param numeroPedido numero do pedido
	 * @return TRUE se a nota ou cupom for emitida com sucesso.
	 * @throws Exception
	 */
	public RegistroPedidoDTO solicitarEmissaoNotaFiscal(String numeroPedido) {
		Long idPedido = Long.parseLong(numeroPedido);
		RegistroPedidoDTO dto = new RegistroPedidoDTO();
		if(pedidoService.isPagamentoEmDinheiro(idPedido)) {
			Pedido pedido = pedidoService.bucarPedidoParaSeparacao(idPedido);
			Integer usuarioLogado = SecurityUtils.getCodigoUsuarioLogado();
			
			registroPedidoService.cadastrarRegistroECupomPedidoSIAC(pedido, usuarioLogado);
			siacService.criarPrePedidoSiac(pedido, usuarioLogado);
			pedidoService.registrarNovaFasePedido(
				pedido, FasePedidoEnum.AGUARDANDO_REGISTRO, pedido.getFasePedido(), usuarioLogado, pedido.getPolo().getCodigo()
			);
			separacaoService.finalizarSeparacao(idPedido.intValue());
			dto.setPagamentoEmDinheiro(true);
			return dto;
		}
		
		dto.setNfEmitida(emitirNotaFiscalSap(numeroPedido));
		return dto;
	}

	/**
	 * Emite nota fiscal no SAP
	 * 
	 * @param numeroPedido numero do pedido
	 * @return TRUE se a nota for emitida com sucesso.
	 */
	public Boolean emitirNotaFiscalSap(String numeroPedido) {
		Integer numeroPedidoInt = Integer.valueOf(numeroPedido);
		PedidoDTO pedidoDTO = pedidoRepositoryCustom.buscarPedidoParaEmitirNotaFiscalSap(numeroPedidoInt);
		validarPedidoParaEmissaoNotaFiscalSap(pedidoDTO, true);

		log.info("Solicitação emissão de NF no SAP. Pedido -> {}", numeroPedido);
		Pedido pedido = pedidoService.buscarPorId(numeroPedidoInt.longValue());

		prepararPedidoParaEmissaoNF(pedido);
		StatusPedidoEnum status = StatusPedidoEnum.FASE_ERRO_CONFIRMACAO_PAGAMENTO;
		try {
			
			updateFasePedido(StatusPedidoEnum.FASE_CONFIRMACAO_PAGAMENTO.name(), null, numeroPedidoInt);
			
			log.info("Emissão de NF/SAP etapa CONFIRMAR PAGAMENTO. Pedido -> {}", numeroPedido);
			ResponseSAPConsultaApiDTO consultaApiDTO = integracaoCanaisVendasRepository.confirmarPagamento(numeroPedido);
			if (!Boolean.TRUE.equals(consultaApiDTO.getConfirmacao())) {
				this.registrarErro(pedido, consultaApiDTO.getMessage(), StatusPedidoEnum.FASE_ERRO_CONFIRMACAO_PAGAMENTO);
			}

			status = StatusPedidoEnum.FASE_ERRO_SOLICITA_EMISSAO_ORDEM_VENDA;
			updateFasePedido(StatusPedidoEnum.FASE_SOLICITA_EMISSAO_ORDEM_VENDA.name(), null, numeroPedidoInt);
			
			log.info("Emissão de NF/SAP etapa EMITIR ORDEM VENDA. Pedido -> {}", numeroPedido);
			
			consultaApiDTO = integracaoCanaisVendasRepository.emitirOrdemVenda(numeroPedido);
			if (!Boolean.TRUE.equals(consultaApiDTO.getConfirmacao())) {
				this.registrarErro(pedido, consultaApiDTO.getMessage(), StatusPedidoEnum.FASE_ERRO_SOLICITA_EMISSAO_ORDEM_VENDA);
			}

			log.info("Emissão de NF/SAP etapa EMITIR ORDEM VENDA foi concluída com sucesso. Pedido -> {}", numeroPedido);
			separacaoService.finalizarSeparacao(numeroPedidoInt);
			
		} catch (Exception e) {
			log.error("Emissão de NF/SAP etapa REGISTRO ERRO PAINEL MONITORAMENTO. Pedido -> {}", numeroPedido, e);
			
			pedidoService.registrarNovaFasePedido(
				pedido, FasePedidoEnum.AGUARDANDO_REGISTRO, pedido.getFasePedido(), SecurityUtils.getCodigoUsuarioLogado(),
				pedido.getPolo().getCodigo()
			);
			
			updateFasePedido(status.name(), e.getMessage(), numeroPedidoInt);
				
			painelMonitoramentoIntegracaoService.integraPanelMonitoramentoAraujo(
				urlIntegration
					.concat("OrdemVendaDelivery")
					.concat(StatusPedidoEnum.FASE_ERRO_CONFIRMACAO_PAGAMENTO.equals(status) ? "/confirmar-pagamento": ""),
				status.name(),
				status.getDescricaoPainel(),
				e.getMessage(),
				numeroPedido
			);
			
			if (isErroPBM(e.getMessage()))
				throw new BusinessException(e.getMessage());
			
			throw new BusinessException(
				StatusPedidoEnum.FASE_ERRO_CONFIRMACAO_PAGAMENTO.equals(status)
					? ERRO_CONFIRMAR_PAGAMENTO
					: ERRO_EMISSAO_NOTA_SAP
			);
		} 

		return true;
	}

	private void registrarErro(Pedido pedido, String mensagemErro, StatusPedidoEnum status) {
		pedidoService.registrarNovaFasePedido(
			pedido, FasePedidoEnum.AGUARDANDO_REGISTRO, pedido.getFasePedido(), SecurityUtils.getCodigoUsuarioLogado(),
			pedido.getPolo().getCodigo()
		);

		updateFasePedido(status.name(), mensagemErro, pedido.getNumeroPedido().intValue());
		log.error("Emissão de NF/SAP Pedido -> {}. ERRO: {}", pedido.getNumeroPedido(), mensagemErro);

		throw new BusinessException(mensagemErro);
	}
	
	public void iniciarRegistro(String numeroPedido, Integer codUsuario) {
		Pedido pedido = pedidoService.findById(Long.parseLong(numeroPedido));
		registroPedidoService.cadastrarRegistroECupomPedidoSIAC(pedido, codUsuario);
	}

	public void prepararPedidoParaEmissaoNF(Pedido pedido) {
		salvarHistoricoFasePedido(
			pedido, FasePedidoEnum.EM_REGISTRO, pedido.getFasePedido(), SecurityUtils.getCodigoUsuarioLogado(),
			pedido.getPolo().getCodigo()
		);
		pedido.setFasePedido(FasePedidoEnum.EM_REGISTRO);
		pedidoService.atualizarPedido(pedido);
		iniciarRegistro(pedido.getNumeroPedido().toString(), SecurityUtils.getCodigoUsuarioLogado());
	}
	
	@Transactional(transactionManager="drogatelTransactionManager", propagation= Propagation.REQUIRES_NEW)
	public void estornoPedidoEmissaoNF(Pedido pedido) {
		salvarHistoricoFasePedido(
			pedido, FasePedidoEnum.AGUARDANDO_REGISTRO, pedido.getFasePedido(), SecurityUtils.getCodigoUsuarioLogado(),
			pedido.getPolo().getCodigo()
		);
		pedido.setFasePedido(FasePedidoEnum.AGUARDANDO_REGISTRO);
		pedidoService.atualizarPedido(pedido);
		iniciarRegistro(pedido.getNumeroPedido().toString(), SecurityUtils.getCodigoUsuarioLogado());
	}
	
	private void updateFasePedido(String statusPedido, String mesage, Integer numeroPedido) {	
		pedidoRepository.updateStatusIntegrationPedido(statusPedido, mesage, numeroPedido);
	}
	
	@SuppressWarnings("unused")
	private void alterarFasePedido(Long numeroPedido, FasePedidoEnum novaFase) {
		pedidoRepository.alterarFasePedido(numeroPedido, novaFase);
	}
	
	private void setaCodigoBarrasPorProduto(List<ItemPedidoDTO> itens) {
		List<Integer> listProduto = new ArrayList<>();
		
		itens.forEach(item -> listProduto.add(item.getCodigoProduto()));
		
		Map<Integer, String> mapCodBarras = pedidoRepositoryCustom.buscarCodigoBarraPorProduto(listProduto);
		
		itens.forEach(item -> item.setCodigosEan(mapCodBarras.get(item.getCodigoProduto())));
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
		}
	}
	
	
}
