package com.clique.retire.service.drogatel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.FasePedidoDTO;
import com.clique.retire.dto.LocalizarPedidoFiltroDTO;
import com.clique.retire.dto.MarketplaceHandshakeResponseDTO;
import com.clique.retire.dto.PedidoEntregaDTO;
import com.clique.retire.dto.PedidoIdentificadorUnicoDTO;
import com.clique.retire.dto.PedidoServicoDTO;
import com.clique.retire.dto.PinMarketplaceDTO;
import com.clique.retire.dto.TipoPedidoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.infra.exception.ConflitoException;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.model.drogatel.Expedicao;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PedidoServico;
import com.clique.retire.model.drogatel.PendenciaPedidoDrogatel;
import com.clique.retire.model.drogatel.Polo;
import com.clique.retire.model.drogatel.PrePedidoSiac;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.model.enums.TipoTaxaEntregaEnum;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.repository.drogatel.ExpedicaoPedidoRepository;
import com.clique.retire.repository.drogatel.ExpedicaoRepository;
import com.clique.retire.repository.drogatel.ExpedicaoRepositoryCustom;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.repository.drogatel.PendenciaPedidoRepository;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.DateUtils;
import com.clique.retire.util.FeignUtil;
import com.clique.retire.util.WebUtils;
import com.clique.retire.wrapper.PageWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import feign.Response;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EntregaPedidoService {


	private static final Integer CODIGO_USUARIO = 1;
	private static final String VIA_MOTO = "E";

	private static final String O_PEDIDO_E_DE_OUTRA_FILIAL = "O pedido é de outra filial.";
	private static final String A_FASE_DO_PEDIDO_NAO_POSSIBILITA_A_ENTREGA_DO_MESMO = "A fase do pedido não possibilita a entrega do mesmo.";
	private static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado. Verifique e tente novamente, ou utilize outro dado para a entrega.";
	private static final String PEDIDO_NAO_ENCONTRADO_PARCEIRO = "Pedido não encontrado, verifique e tente novamente.";

	private static final String JA_EMITIU_NF = "Já foi emitida NF-e para o pedido.";
	private static final String EXPEDICAO_NAO_ECONTRADA = "Expedição não encontrada para o pedido: {}";
	
	@Autowired
	private DrogatelParametroRepository drogatelParametroRepository;
	
	@Autowired
	private ControleIntranetRepositoryCustom controleIntranetRepository;
	
	@Autowired
	private ExpedicaoRepository expedicaoRepository;
	
	@Autowired
	private ExpedicaoPedidoRepository expedicaoPedidoRepository;
	
	@Autowired
	private PedidoRepositoryCustom pedidoRepositoryCustom; 
	
	@Autowired
	private PedidoService pedidoService;
	
	@Autowired
	private PendenciaPedidoRepository pendenciaPedidoRepository;
	
	@Autowired 
	private ExpedicaoRepositoryCustom expedicaoRepositoryCustom;
	
	@Autowired 
	private ReceitaProdutoControladoService receitaProdutoControladoService;
	
	@Autowired
	private CaptacaoPedidoService captacaoPedidoService;

	@Autowired
	private FilialService filialService;
	
	@Autowired
	private IMGService imgService;
	
	@Autowired
	private PedidoServicoService pedidoServicoService;
	
	@Autowired
	private RegistroPedidoService registroPedidoService;
	
	@Value("${url.base.ecommerce.integracao}")
	private String urlEcommerceIntegracao;
	
	@Autowired
	private ProdutoService produtoService;
	
	/**
	 * Método que retorna uma lista de pedidos, conforme filtro e filial. <br/>
	 * Também considera limite de retorno pelo parâmetro maxResults.
	 * @param filtro Parâmetros de filtro da listagem
	 * @return lista de pedidos (em formato DTO) consultados no banco de dados do DROGATEL
	 */
	@Transactional("drogatelTransactionManager")
	public PageWrapper<PedidoEntregaDTO> buscarPedidos(LocalizarPedidoFiltroDTO filtro) {
		Integer filial = controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
		filtro.setFilial(filial);
		PageWrapper<PedidoEntregaDTO> pedidos = pedidoRepositoryCustom.buscarPedidosLojaPorFiltro(filtro);
		
		pedidos.getConteudo().forEach(pedido -> pedido.setReceitaDigital(imgService.isContemReceitaDigital(pedido.getId().longValue())));
		
		if (BooleanUtils.isTrue(filtro.getIsEntrega())) {
			this.validarPedidosParaEntrega(pedidos.getConteudo(), filial, false);
		}

		return pedidos;
	}
	
	/**
	 * Método que retorna um pedido, conforme código VTEX ou número do pedido. <br/>
	 * Também considera paginação pelo parâmetro maxResults.
	 * 
	 * @param filtro Número do pedido ou código ecommerce
	 * @param ipCliente ip do cliente
	 * @return pedido para entrega (em formato DTO) consultado no banco de dados do DROGATEL
	 */
	@Transactional("drogatelTransactionManager")
	public List<PedidoEntregaDTO> buscarPedidoParaEntrega(String filtro, String ipCliente, boolean isParceiro) {
		DrogatelParametro parametro = isParceiro 
				                      ? drogatelParametroRepository.findByNome(ParametroEnum.IFOOD_PIN_TAMANHO.getDescricao()) 
				                      : null;
		
		List<PedidoEntregaDTO> pedidos = parametro == null || filtro.length() != Integer.parseInt(parametro.getValor()) 
										 ? pedidoRepositoryCustom.buscarPedidoParaEntrega(filtro, null, isParceiro) 
										 : Arrays.asList();
		
		Integer codFilial = filialService.consultarFilialParaAcessoPainel(ipCliente);

		if (pedidos.isEmpty()) 
			pedidos = buscaPedidoMarketingPlaceVETEX(filtro, isParceiro, codFilial);

		if (pedidos.isEmpty()) 
			throw new EntidadeNaoEncontradaException(isParceiro ? PEDIDO_NAO_ENCONTRADO_PARCEIRO : PEDIDO_NAO_ENCONTRADO);

		pedidos.forEach(pedido -> {
			
			Long numeroPedido = Long.parseLong(pedido.getId().toString());
			pedido.setGeladeira(produtoService.isProdutoGeladeira(numeroPedido));
			
			if (pedidoService.isPagamentoEmDinheiro(numeroPedido)) {
				pedido.setPagamentoEmDinheiro(true); 
				List<PrePedidoSiac> listPrePedidoSiac = registroPedidoService.buscarPrePedidoSiac(numeroPedido);
				if (!listPrePedidoSiac.isEmpty()) 
					pedido.setPrePedido(
							listPrePedidoSiac.stream()
							 				 .map(prePedidoSiac -> prePedidoSiac.getNumeroPrePedido().toString())
							 				 .collect(Collectors.joining(";"))
					);
			}
		});
		
		this.validarPedidosParaEntrega(pedidos, codFilial, true);
		
		return pedidos;
	}

	private List<PedidoEntregaDTO> buscaPedidoMarketingPlaceVETEX(String filtro, boolean isParceiro, Integer codFilial) {
		DrogatelParametro parametro;
		List<PedidoEntregaDTO> pedidos;
		try {
			log.info("Handshake. Código: {} - Filial: {}", filtro, codFilial);
			
			PinMarketplaceDTO pinMarketPlaceDTO = PinMarketplaceDTO.builder()
				.codigo(filtro)
				.codigoLoja(codFilial.toString())
				.build();
			
			MarketplaceHandshakeResponseDTO response = FeignUtil.getRappiClient(urlEcommerceIntegracao).handShake(pinMarketPlaceDTO);
			String numeroPedidoDrogatel = response.getCodigoPedido();
			
			log.info("Resposta do Handshake para o código '{}': {}", filtro, numeroPedidoDrogatel);
			pedidos = pedidoRepositoryCustom.buscarPedidoParaEntrega(null, numeroPedidoDrogatel, isParceiro);
			if (pedidos.isEmpty()) {
				parametro = drogatelParametroRepository.findByNome(ParametroEnum.URL_BASE_BUSCAR_PEDIDO_DROGATEL_POR_PIN.getDescricao());

				if (parametro != null && StringUtils.isNotEmpty(parametro.getValor())) {
					PedidoIdentificadorUnicoDTO pedidoDTO = FeignUtil.getPedidoClient(parametro.getValor()).buscarIdPedidoDrogatel(filtro);
					log.info("Código do pedido obtido via VETEX : {}", pedidoDTO.getData());
					pedidos = pedidoRepositoryCustom.buscarPedidoParaEntrega(null, pedidoDTO.getData(), isParceiro);
				}
			}
		} catch (Exception e) {
			log.error("Pedido não encontrado com o filtro [{}]. Exception: [{}]",
				filtro, Objects.nonNull(e.getCause()) ? e.getCause().getMessage() : e.getMessage()
			);
			throw new EntidadeNaoEncontradaException(isParceiro? PEDIDO_NAO_ENCONTRADO_PARCEIRO : PEDIDO_NAO_ENCONTRADO);
		}
		return pedidos;
	}

	private void iniciarRetiradaPedido(Integer numeroPedido) {
		try {
			log.info("Sinalizando que o motociclista chegou na loja. Pedido: {}", numeroPedido);
			FeignUtil.getRappiClient(urlEcommerceIntegracao).iniciarRetirada(numeroPedido);
		} catch (Exception exception) {
			log.error("Ocorreu um erro ao iniciar a retirada do pedido.", exception);
			throw new BusinessException("Ocorreu um erro ao iniciar a retirada do pedido.");
		}
	}

	private void validarPedidosParaEntrega(List<PedidoEntregaDTO> pedidos, Integer codigoFilial, boolean lancarExcecao) {
		boolean pedidoUnico = pedidos.size() == 1;
		
		pedidos.forEach(pedido -> {
			try {
				validarPedidoMesmaFilial(codigoFilial, pedido.getFilial());
				validarFasePedidoPermiteEntrega(pedido.getFase(), pedido.isControlado(), pedido.isSuperVendedor(), pedido.getId().longValue(), true);
				validarSeHouveEmissaoNF(pedido);

				Long numeroPedido = pedido.getId().longValue();
				if (receitaProdutoControladoService.validarSeExisteReceitaComCaptacaoPendente(numeroPedido) && 
					receitaProdutoControladoService.isEntregaViaMotociclistaEContemControlado(pedido.getId())
					) {
					captacaoPedidoService.gerarCaptacaoReceitaEEmitirEtiqueta(numeroPedido, false);
				}
				
				pedido.setReceitaDigital(imgService.isContemReceitaDigital(numeroPedido));
				pedido.setPossuiCaptacao(pedido.isControlado() && !receitaProdutoControladoService.validarSeExisteCaptacaoPendente(numeroPedido));

				if (pedidoUnico && Objects.isNull(pedido.getPrePedido())) 
					removerReceitaIniciarRetirada(pedido);
				
			} catch (BusinessException | ConflitoException exception) {
				pedido.setMensagemErro(exception.getMessage());
				if (lancarExcecao && pedidos.size() == 1) throw exception;
			}});
	}

	private void validarSeHouveEmissaoNF(PedidoEntregaDTO pedido) {
		if (!FasePedidoEnum.AGUARDANDO_EXPEDICAO.equals(pedido.getFase()) && !pedido.isSuperVendedor() && pedido.isControlado() && pedido.isNfEmitida()) 
			throw new BusinessException(JA_EMITIU_NF);
	}

	private void removerReceitaIniciarRetirada(PedidoEntregaDTO pedido) {
		if (pedido.isControlado() && !pedido.isPossuiCaptacao()) 
			receitaProdutoControladoService.removerProdutoReceitaControlado(pedido.getId(),true);

		if (pedido.isPossuiPin()) 
			this.iniciarRetiradaPedido(pedido.getId());
	}

	@Transactional("drogatelTransactionManager")
	public BaseResponseDTO entregarPedidoComPin(
		Long numeroPedido, String pin, String ip, Integer codigoUsuario, boolean validarIp
	) throws JsonProcessingException {
		log.info("Validando PIN '{}' para entrega do pedido de Marketplace", pin);

		String codigoFilial = String.valueOf(this.controleIntranetRepository.findFilialByIp(ip));

		PinMarketplaceDTO pinMarketPlaceDTO = PinMarketplaceDTO.builder()
			.codigo(pin)
			.codigoLoja(codigoFilial)
			.build();
		Response response = FeignUtil.getRappiClient(this.urlEcommerceIntegracao).finalizarRetirada(pinMarketPlaceDTO);
		if (Objects.isNull(response)) {
			return BaseResponseDTO.builder()
					.status(String.valueOf(HttpStatus.BAD_REQUEST))
					.data(String.format("PIN %s, referente ao número de pedido %s, não foi devidamente finalizado.", pin, numeroPedido))
					.build();
		}

		final HttpStatus statusCode = HttpStatus.valueOf(response.status());

		log.info("Status Code do retorno da validação do PIN '{}': {}", pin, statusCode);

		if (statusCode.is5xxServerError()) {
			throw new BusinessException("Ocorreu um erro na finalização da retirada.");
		}

		if (statusCode.is2xxSuccessful()) {
			this.entregarPedido(numeroPedido, ip, codigoUsuario, validarIp);
		}

		String message = HttpStatus.BAD_REQUEST.equals(statusCode) || HttpStatus.NOT_FOUND.equals(statusCode)
			? "PIN inválido. Tente novamente!"
			: "success";

		return BaseResponseDTO.builder()
			.status(String.valueOf(statusCode.value()))
			.data(message)
			.build();
	}

	@Transactional("drogatelTransactionManager")
	public void entregarPedido(Long numeroPedido, String ip, Integer codigoUsuario, boolean validarIP) throws JsonProcessingException {
		Object[] result = pedidoRepositoryCustom.buscarTipoEntrega(numeroPedido.intValue());
		String tipoEntrega = result[0].toString();
		String tipoPedido = result[1].toString();

		log.info("Entregar pedido [{}] - tipoEntrega: [{}] - tipoPedido [{}] - usuário [{}]",
			numeroPedido, tipoEntrega, tipoPedido, codigoUsuario
		);

		Pedido pedido = pedidoRepositoryCustom.buscarPedidoPorCodigoPedido(numeroPedido);
		boolean pedidoPossuiProdutoControlado = pedido.getItensPedido().stream()
			.anyMatch(item -> SimNaoEnum.S.equals(item.getProdutoControlado()));

		validarFasePedidoPermiteEntrega(pedido.getFasePedido(), pedidoPossuiProdutoControlado, SimNaoEnum.S.equals(pedido.getSuperVendedor()), numeroPedido, false);

		Integer codigoFilial = pedido.getFilial().getId();

		if (validarIP) {
			codigoFilial = controleIntranetRepository.findFilialByIp(ip);
			validarPedidoMesmaFilial(codigoFilial, pedido.getFilial().getId());
		}
		
		pedidoRepositoryCustom.atualizarQuantExpedidaDosItensDoPedido(pedido.getNumeroPedido().intValue(), new Date());

		salvarExpedicaoParaEntregaDoPedido(codigoUsuario, pedido, codigoFilial, tipoEntrega, null);

		if (!tipoEntrega.equalsIgnoreCase(VIA_MOTO)) {
			pedidoRepositoryCustom.atualizarTipoRetiradaEfetiva(pedido.getNumeroPedido().intValue());
		}
		
		FasePedidoEnum novaFase = tipoEntrega.equalsIgnoreCase(VIA_MOTO) ? FasePedidoEnum.EXPEDIDO :  FasePedidoEnum.ENTREGUE;
		pedidoService.registrarNovaFasePedido(
			pedido, novaFase, pedido.getFasePedido(), codigoUsuario, codigoFilial
		);
	}

	public List<FasePedidoDTO> buscarStatusPedido() {
		return Arrays.stream(FasePedidoEnum.values())
			.filter(fase -> !fase.getDescricaoCombo().equals(""))
			.collect(Collectors.groupingBy(FasePedidoEnum::getDescricaoCombo))
			.entrySet().stream()
			.map(entry -> FasePedidoDTO.builder()
				.codigo(entry.getValue().stream().map(FasePedidoEnum::getChave).collect(Collectors.joining(",")))
				.descricao(entry.getKey())
				.build()
			).collect(Collectors.toList());
	}
	
	public List<TipoPedidoDTO> buscarTipoPedido() {
		return pedidoRepositoryCustom.buscarTiposPedido();
	}
	
	@Transactional("drogatelTransactionManager")
	public void gerarExpedicaoPedidoServico(List<PedidoServicoDTO> listaPS) throws NullPointerException {
		listaPS.forEach(ps -> {
			PedidoServico pedidoServico = pedidoServicoService.obterPedidoServicoPeloID(ps.getNumeroPedido());
			pedidoService.registrarNovaFasePedido(pedidoServico.getPedido().getNumeroPedido(), 
												  FasePedidoEnum.EXPEDIDO.getChave(), Constantes.USUARIO_ADMINISTRADOR);
			salvarExpedicaoParaEntregaDoPedido(Constantes.USUARIO_ADMINISTRADOR, null, ps.getCodigoFilial(), VIA_MOTO, pedidoServico);
		});
	}
	
	private void salvarExpedicaoParaEntregaDoPedido(Integer codigoUsuario, Pedido pedido, Integer codigoFilial, String tipoEntrega, PedidoServico pedidoServico) throws NullPointerException {
		
		Usuario usuario = new Usuario();
		usuario.setCodigoUsuario(codigoUsuario);
		
		Date dataAtual = new Date();
		
		Expedicao expedicao = new Expedicao();
		expedicao.setDataHoraExpedicao(dataAtual);
		expedicao.setQuantidadeEntrega(1);
		expedicao.setResponsavelExpedicao(usuario);
		expedicao.setPolo(new Polo());
		expedicao.getPolo().setCodigo(codigoFilial);
		expedicao.setUltimaAlteracao(dataAtual);
		expedicao.setCodigoUsuarioAlteracao(String.valueOf(codigoUsuario));
		expedicao.setExpedicaoPedido(new ArrayList<>());
		
		ExpedicaoPedido expedicaoPedido = new ExpedicaoPedido();
		expedicaoPedido.setUltimaAlteracao(dataAtual);
		expedicaoPedido.setCodigoUsuarioAlteracao(String.valueOf(codigoUsuario));
		expedicaoPedido.setValorTaxaEntrega(0d);
		expedicaoPedido.setTipoTaxaEntrega(TipoTaxaEntregaEnum.NAO_PAGO);
		expedicaoPedido.setDataHoraRealAlterado(dataAtual);
		expedicaoPedido.setSequenciaEntrega(1);
		
		Long numeroPedido = null;
		if (Objects.nonNull(pedido)) {
			expedicaoPedido.setPedido(pedido);
			expedicaoPedido.setTipoPedido(pedido.getTipoPedido());
			numeroPedido = pedido.getNumeroPedido();
		} else {
			expedicaoPedido.setPedidoServico(pedidoServico);
			expedicaoPedido.setTipoPedido(TipoPedidoEnum.SERVICO);
			numeroPedido = pedidoServico.getCodigo(); 
		}

		if(!tipoEntrega.equalsIgnoreCase(VIA_MOTO)) {
			expedicao.setDataHoraRetorno(dataAtual);
			expedicao.setResponsavelRetorno(usuario);
			expedicaoPedido.setIndicadorRetorno(SimNaoEnum.S);
			expedicaoPedido.setDataHoraPrevisaoEntrega(dataAtual);
		} else {
			expedicaoPedido.setIndicadorRetorno(SimNaoEnum.N);
			expedicaoPedido.setDataHoraPrevisaoEntrega(DateUtils.em30Minutos());
		}
		expedicaoPedido.setExpedicao(expedicao);
		
		expedicao.getExpedicaoPedido().add(expedicaoPedido);
		expedicaoRepository.save(expedicao);
		expedicaoPedidoRepository.atualizarExpedicaoNaRotaGerada(numeroPedido, expedicao.getCodigo());
	}

	private void validarFasePedidoPermiteEntrega(FasePedidoEnum fase, boolean pedidoComControlado, boolean superVendedor, Long numeroPedido, boolean consultaParaEntrega) {
		// Valida se a fase do pedido possibilita a entrega.
		if (
			FasePedidoEnum.AGUARDANDO_EXPEDICAO.equals(fase) ||
			FasePedidoEnum.AGUARDANDO_RECEITA.equals(fase) ||
			(!superVendedor && FasePedidoEnum.AGUARDANDO_REGISTRO.equals(fase) && pedidoComControlado)
		) return;
		
		boolean isPagamentoEmDinheiro4Ponto0 = pedidoService.isPagamentoEmDinheiro(numeroPedido);
		if (consultaParaEntrega) {
			if (FasePedidoEnum.AGUARDANDO_REGISTRO.equals(fase) && isPagamentoEmDinheiro4Ponto0) return;
		} else {
			if (FasePedidoEnum.EM_REGISTRO.equals(fase) && isPagamentoEmDinheiro4Ponto0) return;
		}

		throw new ConflitoException(A_FASE_DO_PEDIDO_NAO_POSSIBILITA_A_ENTREGA_DO_MESMO);
	}

	private void validarPedidoMesmaFilial(Integer codigoFilialAtual, Integer codigoFilialPedido) {
		if (!codigoFilialPedido.equals(codigoFilialAtual)) {
			throw new BusinessException(O_PEDIDO_E_DE_OUTRA_FILIAL);
		}
	}

	public void confirmarEntregaPedidoMotociclista(Long numeroPedido, Date dataEntrega) {
		log.info("recebendo confirmação de entrega de pedido: {}", numeroPedido);
		
		//recupera ultima expedicao pedido, pelo numero do pedido.
		ExpedicaoPedido expedicaoPedido = pedidoRepositoryCustom.buscarExpedicaoPedidoPorNumeroPedido(numeroPedido);
		if(expedicaoPedido == null) {
			log.info(EXPEDICAO_NAO_ECONTRADA, numeroPedido);
			return;
		}

		log.info("confirmando nova fase do pedido entregue: {}", numeroPedido);
		pedidoService.registrarNovaFasePedido(
				expedicaoPedido.getPedido(), FasePedidoEnum.ENTREGUE,
				expedicaoPedido.getPedido().getFasePedido(), CODIGO_USUARIO, expedicaoPedido.getExpedicao().getPolo().getCodigo());
		
		log.info("nova fase do pedido entregue: {}", numeroPedido);
		
		expedicaoRepositoryCustom.gravarRetornoExpedicaoPedido(dataEntrega, numeroPedido.intValue());
		
		log.info("fim confirmar entrega pedido: {}", expedicaoPedido.getPedido().getNumeroPedido());
	}

	public void confirmarPedidoNaoEntregueMotociclista(Long numeroPedido, String motivo) {
		Integer codigoMotivoNaoEntrega = this.buscarCodigoMotivoNaoEntrega();
		if (codigoMotivoNaoEntrega == null) {
			log.info("Não foi encontrado o Código Motivo Drogatel para Pedido Não Entregue: {}", numeroPedido);
			return;
		}
		
		//recupera ultima expedicao pedido, pelo numero do pedido.
		ExpedicaoPedido expedicaoPedido = this.buscarExpedicaoPedidoDrogatel(numeroPedido, codigoMotivoNaoEntrega, motivo);
		if (expedicaoPedido == null) {
			log.info(EXPEDICAO_NAO_ECONTRADA, numeroPedido);
			return;
		}

		PendenciaPedidoDrogatel pendencia = this.criarPendenciaPedidoDrogatel(numeroPedido.intValue(), codigoMotivoNaoEntrega, motivo);

		log.info("registrando nova fase do pedido nao entregue: {}", expedicaoPedido.getPedido().getNumeroPedido());
		
		pedidoService.registrarNovaFasePedido(
				expedicaoPedido.getPedido(), FasePedidoEnum.NAO_ENTREGUE,
				expedicaoPedido.getPedido().getFasePedido(), CODIGO_USUARIO, expedicaoPedido.getExpedicao().getPolo().getCodigo());
		
		log.info("nova fase do pedido nao entregue: {}", expedicaoPedido.getPedido().getNumeroPedido());
		
		expedicaoPedidoRepository.save(expedicaoPedido);
		pendenciaPedidoRepository.save(pendencia);
		
		log.info("fim confirmar-pedido-nao-entregue-motociclista: {}", expedicaoPedido.getPedido().getNumeroPedido());
	}

	private Integer buscarCodigoMotivoNaoEntrega() {
		DrogatelParametro parametro = drogatelParametroRepository.findByNome(ParametroEnum.CODIGO_MOTIVO_DROGATEL_PEDIDO_NAO_ENTREGUE.getDescricao());
		if (parametro == null) {
			return null;
		}

		return Integer.parseInt(parametro.getValor());
	}

	private ExpedicaoPedido buscarExpedicaoPedidoDrogatel(Long numeroPedido, Integer codigoMotivoNaoEntrega, String motivo) {
		ExpedicaoPedido expedicaoPedido = pedidoRepositoryCustom.buscarExpedicaoPedidoPorNumeroPedido(numeroPedido);
		if (expedicaoPedido == null) {
			return null;
		}

		expedicaoPedido.setIndicadorRetorno(SimNaoEnum.S);
		expedicaoPedido.setCodMotivoNaoEntrega(codigoMotivoNaoEntrega);
		expedicaoPedido.setMotivoNaoEntrega(motivo);
		expedicaoPedido.getExpedicao().setDataHoraRetorno(new Date());

		Usuario usuario = new Usuario();
		usuario.setCodigoUsuario(CODIGO_USUARIO);
		expedicaoPedido.getExpedicao().setResponsavelRetorno(usuario);
		return expedicaoPedido;
	}

	private PendenciaPedidoDrogatel criarPendenciaPedidoDrogatel(Integer numeroPedido, Integer codigoMotivoNaoEntrega, String motivo) {
		Usuario responsavelPendencia = new Usuario();
		responsavelPendencia.setCodigoUsuario(CODIGO_USUARIO);

		PendenciaPedidoDrogatel pendencia = new PendenciaPedidoDrogatel(String.valueOf(responsavelPendencia.getCodigoUsuario()));
		pendencia.setCodigoMotivoDrogatel(codigoMotivoNaoEntrega);
		pendencia.setDataCriacao(new Date());
		pendencia.setDescricaoPendencia(motivo);
		pendencia.setNumeroPedido(numeroPedido);
		pendencia.setPendenciaResolvida(SimNaoEnum.N);
		pendencia.setTipoPendencia("D");
		pendencia.setResponsavelPendencia(responsavelPendencia);
		return pendencia;
	}

	public void retornarPedidoParaExpedicao(Long numeroPedido, String motivo) {
		Integer codigoMotivoNaoEntrega = this.buscarCodigoMotivoNaoEntrega();
		if (codigoMotivoNaoEntrega == null) {
			log.info("Não foi encontrado o Código Motivo Drogatel para Pedido Não Entregue: {}", numeroPedido);
			return;
		}

		ExpedicaoPedido expedicaoPedido = this.buscarExpedicaoPedidoDrogatel(numeroPedido, codigoMotivoNaoEntrega, motivo);
		if (expedicaoPedido == null) {
			log.info(EXPEDICAO_NAO_ECONTRADA, numeroPedido);
			return;
		}

		log.info("Fase atual do pedido: {}", expedicaoPedido.getPedido().getFasePedido().getValor());

		pedidoService.registrarNovaFasePedido(
				expedicaoPedido.getPedido(), FasePedidoEnum.AGUARDANDO_EXPEDICAO,
				expedicaoPedido.getPedido().getFasePedido(), CODIGO_USUARIO, expedicaoPedido.getExpedicao().getPolo().getCodigo());

		expedicaoPedidoRepository.save(expedicaoPedido);

		log.info("Fim retornar-pedido-expedicao: {}", numeroPedido);
	}

}
