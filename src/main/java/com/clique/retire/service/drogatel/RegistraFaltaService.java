package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ImpressaoDTO;
import com.clique.retire.dto.ItemFaltaDTO;
import com.clique.retire.dto.MovimentoPedidoDrogatelDTO;
import com.clique.retire.dto.MovimentoPedidoItemDrogatelDTO;
import com.clique.retire.dto.PedidoFaltaDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoEdicaoPedido;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.infra.exception.ErroValidacaoException;
import com.clique.retire.infra.exception.ModalidadePagamentoInaptaException;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.drogatel.PedidoRepositoryCustom;
import com.clique.retire.service.cosmos.ImagemService;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.DateUtils;
import com.clique.retire.util.SecurityUtils;
import com.clique.retire.wrapper.IntWrapper;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistraFaltaService {

  private static final String O_PEDIDO_CONSULTADO_NAO_PERTENCE_A_FILIAL = "O pedido consultado não pertence à mesma filial.";
  private static final String PEDIDO_NAO_PERMITE_APONTAMENTO_DE_FALTA = "O pedido consultado não está na fase EM SEPARAÇÃO e, portanto, não permite apontamento de falta.";
  private static final Integer ZERO = 0;

  private final Gson gson;
  private final FaltaService faltaService;
  private final PedidoService pedidoService;
  private final ImagemService imagemService;
  private final DrogatelService drogatelService;
  private final ParametroService parametroService;
  private final SeparacaoService separacaoService;
  private final EdicaoPedidoService edicaoPedidoService;
  private final PedidoRepositoryCustom pedidoRepositoryCustom;
  private final ImpressaoPedidoService impressaoPedidoService;

  /**
   * Retorna um produto e seus itens para registro de falta. <br/>
   *
   * @param codigoLoja codigo da loja logada
   * @return pedido para apontamento de faltas.
   */
  @Transactional("drogatelTransactionManager")
  public PedidoFaltaDTO buscarPedidoParaApontamentoDeFaltaUsuario(Integer codigoLoja) {
    Integer numeroPedidoSeparacao = this.obterPedidoEmSeparacaoPorUsuario(SecurityUtils.getCodigoUsuarioLogado(), codigoLoja);
    return buscarPedidoParaApontamentoDeFalta(numeroPedidoSeparacao, codigoLoja);
  }

  /**
   * Retorna um produto e seus itens para registro de falta. <br/>
   *
   * @param numeroPedido numero do pedido
   * @return pedido para apontamento de faltas.
   */
  @Transactional("drogatelTransactionManager")
  public PedidoFaltaDTO buscarPedidoParaApontamentoDeFalta(Integer numeroPedido, Integer codigoLoja) {
    PedidoFaltaDTO pedido = pedidoRepositoryCustom.buscarPedidoParaInicioFalta(numeroPedido);

    if (!codigoLoja.equals(pedido.getCodigoFilial())) {
      throw new ErroValidacaoException(O_PEDIDO_CONSULTADO_NAO_PERTENCE_A_FILIAL);
    }

    if (!FasePedidoEnum.EM_SEPARACAO.equals(pedido.getFase())) {
      throw new ErroValidacaoException(PEDIDO_NAO_PERMITE_APONTAMENTO_DE_FALTA);
    }

    Long idTipoFrete = pedido.getTipoFrete().longValue();
	boolean permiteApontamentoFalta = !TipoPedidoEnum.ARAUJOTEM.getChave().equals(pedido.getTipoPedido())
        || verificarSePedidoEhQuatroPontoZeroEPermiteApontamentoFalta(numeroPedido.longValue(), idTipoFrete);
    pedido.setPermiteApontamentoFalta(permiteApontamentoFalta);
    
    imagemService.montarUrlImagensProdutos(pedido.getProdutos());

    return pedido;
  }

  private boolean verificarSePedidoEhQuatroPontoZeroEPermiteApontamentoFalta(Long numeroPedido, Long idTipoFrete) {
    int quantidadeApontamentoDeFaltasEmLojasDiferentes =
        pedidoRepositoryCustom.buscarQuantidadeDeApontamentoDeFaltaEmLojaDiferente(numeroPedido);
    int quantidadePermitidaDeTransferenciaDePolos = Integer.parseInt(
        parametroService.buscarPorChave(ParametroEnum.PARAMETRO_QUANTIDADE_MAXIMA_PULOS_PARA_TRANSFERENCIA));

    boolean isEntregaQuatroPontoZero = Constantes.TIPO_FRETE_ENTREGA_VIA_MOTO_ID.equals(idTipoFrete.intValue());
    boolean quantidadeDeApontamentosDeFaltaDentroDoPermitido =
        quantidadeApontamentoDeFaltasEmLojasDiferentes < quantidadePermitidaDeTransferenciaDePolos;

    return isEntregaQuatroPontoZero && quantidadeDeApontamentosDeFaltaDentroDoPermitido;
  }

  /**
   * Atualiza a quantidade separada dos Items de Pedido de acordo com a quantidade
   * em falta por produto.
   *
   * @param pedidoFaltaDTO dados dos pedido para apontamento de falta
   */
  public PedidoFaltaDTO registrarFaltaProdutoPedido(PedidoFaltaDTO pedidoFaltaDTO) {
    Pedido pedido = pedidoRepositoryCustom.buscarPedidoParaRegistrarFalta(pedidoFaltaDTO.getCodigo());

    if (FasePedidoEnum.EM_SEPARACAO != pedido.getFasePedido()) {
      throw new BusinessException(PEDIDO_NAO_PERMITE_APONTAMENTO_DE_FALTA);
    }

    Long numeroPedido = pedido.getNumeroPedido();
    Integer codUsuarioLogado = SecurityUtils.getCodigoUsuarioLogado();

    log.info("Realizando apontamento de falta do pedido [{}] - usuário [{}]: {}", numeroPedido, codUsuarioLogado,
        gson.toJson(pedidoFaltaDTO));

    //pedidoRepositoryCustom.ajustarQuantidadeSeparadaItemPedido(numeroPedido.intValue())

  	pedidoFaltaDTO.getProdutos().forEach(produtoDTO -> {
  		int quantidadeFaltaInteger = Optional.ofNullable(produtoDTO.getQuantidadeFalta()).orElse(0);
  		final IntWrapper quantidadeFalta = new IntWrapper(quantidadeFaltaInteger);
  		final IntWrapper falta = new IntWrapper(quantidadeFaltaInteger);
  		pedido.getItensPedido().stream()
  				.filter(itemPedido -> itemPedido.getProduto().getCodigo().equals(produtoDTO.getCodigo()))
  				.forEach(itemPedido -> {
  					
  					if(quantidadeFaltaInteger < 1) 
  			  			throw new ErroValidacaoException("A quantidade de falta apontada é inválida.");
  					
  					if (quantidadeFalta.getValue() > 0) {
  						if (itemPedido.getQuantidadeSeparada() >= quantidadeFalta.getValue()) {
  							itemPedido.setQuantidadeSeparada(
  									itemPedido.getQuantidadeSeparada() - quantidadeFalta.getValue());
  							falta.setValue(quantidadeFalta.getValue());
  							quantidadeFalta.setValue(ZERO);
  						} else {
  							falta.setValue(itemPedido.getQuantidadeSeparada());
  							quantidadeFalta.setValue(quantidadeFalta.getValue() - itemPedido.getQuantidadeSeparada());
  							itemPedido.setQuantidadeSeparada(ZERO);
  						}
  						faltaService.salvarOuAtualizarFalta(codUsuarioLogado, pedido, falta, itemPedido);
  						if (Objects.nonNull(itemPedido.getItemPedidoPBM())) 
  							pedidoFaltaDTO.setContemPBM(true);
  					}
  				});
  		
  		if (!drogatelService.sinalizarApontamentoFaltaZeroBalcao(pedido.getPolo().getCodigo(), produtoDTO.getCodigo(), codUsuarioLogado)) 
  			pedidoFaltaDTO.setFalhaSinalizacaoZeroBalcao(true);
    });
  	
    boolean isPedidoAptoParaEdicao = edicaoPedidoService.isPedidoAptoParaEdicao(pedido);  
    boolean isPedidoQuatroPontoZeroEPermiteApontamentoDeFaltaConvencional =
        verificarSePedidoEhQuatroPontoZeroEPermiteApontamentoFalta(numeroPedido, pedido.getIdTipoFrete());

    if (isPedidoAptoParaEdicao 
    	&& (pedido.getMarketplace().equals(SimNaoEnum.S) 
    		|| (!isPedidoQuatroPontoZeroEPermiteApontamentoDeFaltaConvencional 
    	        && pedidoService.existeEmailCadastradoParaOCliente(numeroPedido)))) {
    	 
      try {
        List<ItemFaltaDTO> itensFaltantes = montarDtoParaEdicaoPedido(pedido);
        TipoEdicaoPedido tipoEdicao = editarOuCancelarPedido(numeroPedido, itensFaltantes, pedidoFaltaDTO.isContemPBM());
        if (Objects.nonNull(tipoEdicao)) {
        	if (tipoEdicao.equals(TipoEdicaoPedido.ITENS_REMOVIDOS)) 
        		pedidoFaltaDTO.setDocumento(imprimirComandaDeSeparacao(numeroPedido));
        	
        	pedidoFaltaDTO.setTipoApontamento(tipoEdicao);
        	return pedidoFaltaDTO;
        }
      } catch (ModalidadePagamentoInaptaException exception) {
        log.error("Erro na modalidade de pagamento do pedido [{}]: {}", numeroPedido, exception.getMessage());
        log.info("Pedido [{}] seguirá para apontamento de falta convencional.", numeroPedido);
      }
    }

    FasePedidoEnum faseAnterior = pedido.getFasePedido();

    pedido.setFasePedido(FasePedidoEnum.AGUARDANDO_NEGOCIACAO);
    pedido.setInicioNegociacao(new Date());

    pedidoService.atualizarPedido(pedido);
    pedidoService.criarHistoricoFasePedido(
        pedido, pedido.getFasePedido(), faseAnterior, codUsuarioLogado, pedido.getPolo().getCodigo()
    );
	separacaoService.finalizarSeparacao(pedido.getNumeroPedido().intValue());

    return pedidoFaltaDTO;
  }
  
  public TipoEdicaoPedido editarOuCancelarPedido(Long numeroPedido, List<ItemFaltaDTO> itensFaltantes, boolean contemPBM) {
	Pedido pedido = pedidoService.findById(numeroPedido);
  	TipoEdicaoPedido tipoEdicao = edicaoPedidoService.editarOuCancelarPedido(pedido, itensFaltantes, contemPBM);
  	if (Objects.isNull(tipoEdicao))
  		return null;
  	
	log.info("Pedido [{}] - Sinalizando no drogatel a alteração no pedido", numeroPedido);
	MovimentoPedidoDrogatelDTO dto = definirMovimentoPedidoDrogatelDTO(pedido);
	drogatelService.movimentacaoPedidoEcommerce(dto);
    
  	return tipoEdicao;
  }

  private MovimentoPedidoDrogatelDTO definirMovimentoPedidoDrogatelDTO(Pedido pedido) {
  	List<MovimentoPedidoItemDrogatelDTO> itens = pedido.getItensPedido()
  			.stream()
  			.map(item ->  MovimentoPedidoItemDrogatelDTO
  					.builder()
					.codigoProduto(item.getProduto().getCodigo())
					.quantidade(item.getQuantidadePedida())
					.valorUnitario(item.getPrecoUnitario())
					.build())
  			.collect(Collectors.toList());
      
    return MovimentoPedidoDrogatelDTO.builder()
    		.dataModificacao(DateUtils.getDataHoraFormatadaDrogatel(pedido.getUltimaAlteracao()))
			.numeroPedido(pedido.getNumeroPedido().intValue())
			.numeroPedidoEcommerce(pedido.getCodigoVTEXSomenteNumeros())
			.numeroPedidoEcommerceCliente(pedido.getCodigoVTEX())
			.fasePedido(pedido.getFasePedido().getChave())
			.itens(itens)
			.valorTotal(pedido.getValorTotal())
			.valorFrete(pedido.getValorTaxaEntrega())
			.build();
  }

  private List<ItemFaltaDTO> montarDtoParaEdicaoPedido(Pedido pedido) {
    return pedido.getItensPedido().stream()
        .filter(item -> !Objects.equals(item.getQuantidadePedida(), item.getQuantidadeSeparada()))
        .map(item -> {
          ItemFaltaDTO itemDTO = new ItemFaltaDTO();
          itemDTO.setCodigoItem(item.getCodigo());
          itemDTO.setQuantidadeFalta(item.getQuantidadePedida() - item.getQuantidadeSeparada());
          return itemDTO;
        }).collect(Collectors.toList());
  }

  private Integer obterPedidoEmSeparacaoPorUsuario(Integer codigoUsuario, Integer codigoLoja) {
    return pedidoRepositoryCustom.obterPedidoEmSeparacaoPorUsuario(codigoUsuario,codigoLoja);
  }
  
  private String imprimirComandaDeSeparacao(Long numeroPedido) {
      try {
          ImpressaoDTO impressaoDTO = impressaoPedidoService.imprimirPedido(numeroPedido, SecurityUtils.getCodigoUsuarioLogado(), true);
          return impressaoDTO.getDocumento();
      } catch (Exception e) {
          log.error("Ocorreu um erro na reimpressão da comanda após edição do pedido: ", e);
          throw new BusinessException("Ocorreu um erro ao tentar reimprimir a comanda de separação do pedido.");
      }
  }

  @Transactional("drogatelTransactionManager")
  public void inserirHistoricoApontamentoFaltaAraujoTem(PedidoFaltaDTO pedidoFalta) {
    Pedido pedido = pedidoRepositoryCustom.buscarPedidoParaRegistrarFalta(pedidoFalta.getCodigo());

    if (FasePedidoEnum.EM_SEPARACAO != pedido.getFasePedido()) {
      throw new BusinessException(PEDIDO_NAO_PERMITE_APONTAMENTO_DE_FALTA);
    }

    FasePedidoEnum faseAnterior = pedido.getFasePedido();
    Integer usuarioLogado = SecurityUtils.getCodigoUsuarioLogado();
    Integer codigoFilial = pedido.getPolo().getCodigo();

    pedidoService.criarHistoricoFasePedido(
      pedido, FasePedidoEnum.AGUARDANDO_NEGOCIACAO, faseAnterior, usuarioLogado, codigoFilial
    );
    pedidoService.registrarNovaFasePedido(
      pedido, FasePedidoEnum.AGUARDANDO_REGISTRO, faseAnterior, usuarioLogado, codigoFilial
    );
  }

}
