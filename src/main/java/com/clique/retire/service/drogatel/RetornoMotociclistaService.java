package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.DadosMotociclistaDTO;
import com.clique.retire.dto.DadosRetornoMotociclistaDTO;
import com.clique.retire.dto.ItemPedidoRetornoMotociclistaDTO;
import com.clique.retire.dto.LoteBipadoDTO;
import com.clique.retire.dto.MotivoDrogatelDTO;
import com.clique.retire.dto.PedidoRetornoMotociclistaDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPendenciaPedidoEnum;
import com.clique.retire.infra.exception.ConflitoException;
import com.clique.retire.infra.exception.ErroValidacaoException;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.ItemFaltaRetorno;
import com.clique.retire.model.drogatel.MotivoDrogatel;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PendenciaPedidoDrogatel;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.repository.cosmos.ControleIntranetRepositoryCustom;
import com.clique.retire.repository.drogatel.ExpedicaoPedidoRepository;
import com.clique.retire.repository.drogatel.PendenciaPedidoRepository;
import com.clique.retire.service.cosmos.ImagemService;
import com.clique.retire.util.SecurityUtils;
import com.clique.retire.util.WebUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetornoMotociclistaService {

  private static final String TIPO_MOTIVO_PEDIDO_NAO_ENTREGUE = "N";
  private static final String FILA_PEDIDO_NAO_ENTREGUE = "ENTREGA 4.0 - Pedidos 4.0 não entregue";
  private static final String FILA_DEVOLUCAO_COM_DINHEIRO = "ENTREGA 4.0 - Devolução com dinheiro";
  private static final String PEDIDO_PERTENCE_A_OUTRA_FILIAL = "O pedido pertence a outra filial";
  private static final String FASE_PEDIDO_NAO_PERMITE_RETORNO = "A fase do pedido não possibilita o retorno do mesmo";

  private final PedidoService pedidoService;
  private final ImagemService imagemService;
  private final MotivoDrogatelService motivoDrogatelService;
  private final ImpressaoPedidoService impressaoPedidoService;
  private final ConexaoDeliveryService conexaoDeliveryService;
  private final ItemFaltaRetornoService itemFaltaRetornoService;
  private final ExpedicaoPedidoRepository expedicaoPedidoRepository;
  private final PendenciaPedidoRepository pendenciaPedidoRepository;
  private final ControleIntranetRepositoryCustom controleIntranetRepository;

  public DadosRetornoMotociclistaDTO consultarPedido(String filtro) {
    log.info("Buscando pedido para gravar retorno do motociclista. Filtro [{}] - Usuário [{}]",
      filtro, SecurityUtils.getCodigoUsuarioLogado()
    );
    PedidoRetornoMotociclistaDTO pedido = this.obterPedidoPorNumeroOuChaveDaNotaParaRetornoDoMotociclista(filtro);
    pedido.setItens(this.agruparProdutosELotes(pedido.getItens()));

    List<MotivoDrogatelDTO> motivos = this.motivoDrogatelService.buscarPorTipo(TIPO_MOTIVO_PEDIDO_NAO_ENTREGUE).stream()
      .map(motivoDrogatel -> {
        MotivoDrogatelDTO motivoDTO = new MotivoDrogatelDTO(motivoDrogatel);
        String descricao = motivoDTO.getDescricao();
        String descricaoFormatada = StringUtils.capitalize(descricao.toLowerCase());
        motivoDTO.setDescricao(descricaoFormatada);
        return motivoDTO;
      })
      .collect(Collectors.toList());

    return DadosRetornoMotociclistaDTO.builder()
      .pedido(pedido)
      .motivos(motivos)
      .build();
  }

  private PedidoRetornoMotociclistaDTO obterPedidoPorNumeroOuChaveDaNotaParaRetornoDoMotociclista(String filtro) {
    PedidoRetornoMotociclistaDTO pedidoDTO = this.pedidoService.consultarPedidoRetornoMotociclista(filtro);
    Integer numeroPedido = pedidoDTO.getNumeroPedido();

    Pedido pedido = this.pedidoService.buscarPorIdComItens(numeroPedido.longValue());
    if (!FasePedidoEnum.EXPEDIDO.equals(pedido.getFasePedido())) {
      log.error("Pedido [{}] está na fase [{}] e não pode ser feito processo de retorno do motociclista.", numeroPedido, pedido.getFasePedido());
      throw new ConflitoException(FASE_PEDIDO_NAO_PERMITE_RETORNO);
    }

    Integer filial = this.controleIntranetRepository.findFilialByIp(WebUtils.getClientIp());
    if (!filial.equals(pedido.getPolo().getCodigo())) {
      log.error("Pedido [{}] é da filial [{}] e não pode ser feito processo de retorno do motociclista na filial [{}].", numeroPedido, pedido.getFilial(), filial);
      throw new ConflitoException(PEDIDO_PERTENCE_A_OUTRA_FILIAL);
    }

    ExpedicaoPedido expedicaoPedido = this.pedidoService.buscarExpedicaoPedidoPeloNumeroPedido(numeroPedido);
    if (Objects.isNull(expedicaoPedido)) {
      log.error("Não há motociclista associado a entrega do pedido {}. Contate o administrador do sistema.", numeroPedido);
      	throw new ErroValidacaoException(String.format("Não há motociclista associado a entrega do pedido %s. Contate o administrador do sistema.", numeroPedido)
      );
    }

    pedido.getItensPedido().forEach(
      itemPedido -> pedidoDTO.getItens().forEach(itemPedidoDTO -> {
        if (itemPedidoDTO.getCodigoItem().equals(itemPedido.getCodigo())) {
          boolean isAntibiotico = SimNaoEnum.getBooleanByValue(itemPedido.getProdutoAntibiotico());
          itemPedidoDTO.setAntibiotico(isAntibiotico);
        }
      }
    ));

    this.imagemService.montarUrlImagensProdutos(pedidoDTO.getItens());

    return pedidoDTO;
  }

  private List<ItemPedidoRetornoMotociclistaDTO> agruparProdutosELotes(List<ItemPedidoRetornoMotociclistaDTO> itens) {
    return itens.stream()
      .collect(Collectors.groupingBy(ItemPedidoRetornoMotociclistaDTO::getCodigoProduto)).values().stream()
      .map(itensAgrupadosPorDescricao -> {
        ItemPedidoRetornoMotociclistaDTO item = itensAgrupadosPorDescricao.get(0);
        item.setCodigoItem(null);
        item.setQuantidadeNota(
          itensAgrupadosPorDescricao.stream().mapToInt(ItemPedidoRetornoMotociclistaDTO::getQuantidadeNota).sum()
        );
        List<LoteBipadoDTO> lotesDTO = itensAgrupadosPorDescricao.stream()
          .flatMap(itemPedidoRetornoMotociclistaDTO -> itemPedidoRetornoMotociclistaDTO.getLotes().stream())
          .collect(Collectors.groupingBy(LoteBipadoDTO::getLote))
          .values().stream()
          .map(lotes -> {
            LoteBipadoDTO lote = lotes.get(0);
            lote.setQuantidade(lotes.stream().mapToInt(LoteBipadoDTO::getQuantidade).sum());
            return lote;
          })
          .collect(Collectors.toList());

        item.setLotes(lotesDTO);
        return item;
      }).collect(Collectors.toList());
  }

  @Transactional
  public void salvarRetorno(DadosRetornoMotociclistaDTO dadosRetorno) throws Exception {
    String filtro = String.valueOf(dadosRetorno.getPedido().getNumeroPedido());
    MotivoDrogatelDTO motivoNaoEntrega = dadosRetorno.getMotivos().stream()
      .filter(motivoDrogatelDTO -> motivoDrogatelDTO.getId().equals(dadosRetorno.getIdMotivo()))
      .findFirst()
      .orElseThrow(() -> new ErroValidacaoException("Motivo de não entrega selecionado é inválido."));

    PedidoRetornoMotociclistaDTO pedidoDTO = this.obterPedidoPorNumeroOuChaveDaNotaParaRetornoDoMotociclista(filtro);
    Integer numeroPedido = pedidoDTO.getNumeroPedido();
    ExpedicaoPedido expedicaoPedido = this.pedidoService.buscarExpedicaoPedidoPeloNumeroPedido(numeroPedido);

    List<ItemFaltaRetorno> itensFaltantes = this.extrairItensFaltantesNoRetorno(
      pedidoDTO, dadosRetorno.getPedido().getItens(), expedicaoPedido
    );
    boolean isEntregaParcial = !itensFaltantes.isEmpty();

    log.info("Salvando retorno motociclista para o pedido [{}] - motivo [{}] - usuário [{}] - parcial [{}]",
      numeroPedido, motivoNaoEntrega.getDescricao(), SecurityUtils.getCodigoUsuarioLogado(), isEntregaParcial ? "Sim" : "Não"
    );

    if (isEntregaParcial && Objects.isNull(dadosRetorno.getCpfMotociclista())) {
      log.error("Não foi informado os 6 primeiros dígitos do CPF para retorno do motociclista do pedido [{}]", filtro);
      throw new ConflitoException("É obrigatório o preenchimento dos 6 primeiros digitos do CPF do motociclista para entrega parcial.");
    }

    DadosMotociclistaDTO dadosMotociclistaDTO = this.conexaoDeliveryService.obterDadosMotociclista(numeroPedido);
    if (Objects.nonNull(dadosMotociclistaDTO) && Objects.nonNull(dadosMotociclistaDTO.getData()) 
    	&& !dadosMotociclistaDTO.getData().getCpf().startsWith(dadosRetorno.getCpfMotociclista())) {
      String message = String.format("O motociclista que está fazendo o retorno não é o mesmo que está vinculado a entrega do pedido [%s]", filtro);
      log.error(message);
      throw new ConflitoException(message);
    }

    this.conexaoDeliveryService.finalizarRotaPedido(numeroPedido);
    this.itemFaltaRetornoService.saveAll(itensFaltantes);
    this.finalizarRetornoExpedicaoPedido(expedicaoPedido, motivoNaoEntrega);
    this.alterarFaseEEnviarPedidoNaoEntregueParaFilaDaEntrega4ponto0(numeroPedido, motivoNaoEntrega);
    this.impressaoPedidoService.imprimirPedido(numeroPedido.longValue(), SecurityUtils.getCodigoUsuarioLogado(), true);
  }

  private List<ItemFaltaRetorno> extrairItensFaltantesNoRetorno(
    PedidoRetornoMotociclistaDTO pedidoDTO, List<ItemPedidoRetornoMotociclistaDTO> itensRetornados, ExpedicaoPedido expedicaoPedido
  ) {
    return pedidoDTO.getItens().stream()
      .map(itemPedido -> {
        itensRetornados.stream()
          .filter(itemRetornado -> itemPedido.getCodigoProduto().equals(itemRetornado.getCodigoProduto()))
          .forEach(itemRetornado -> {
            int quantidadeBipadaRestante =  itemPedido.getQuantidadeNota() < itemRetornado.getQuantidadeBipada()
              ? itemRetornado.getQuantidadeBipada() - itemPedido.getQuantidadeNota()
              : 0;

            int quantidadeNotaRestante = itemPedido.getQuantidadeNota() < itemRetornado.getQuantidadeBipada()
              ? 0
              : itemPedido.getQuantidadeNota() - itemRetornado.getQuantidadeBipada();

            itemRetornado.setQuantidadeBipada(quantidadeBipadaRestante);
            itemPedido.setQuantidadeNota(quantidadeNotaRestante);
          });

        ItemFaltaRetorno itemFaltaRetorno = new ItemFaltaRetorno(SecurityUtils.getCodigoUsuarioLogado().toString());
        itemFaltaRetorno.setCodigoItemPedido(itemPedido.getCodigoItem());
        itemFaltaRetorno.setQuantidade(itemPedido.getQuantidadeNota());
        itemFaltaRetorno.setExpedicaoPedido(expedicaoPedido);
        return itemFaltaRetorno;
      })
      .filter(itemFalta -> itemFalta.getQuantidade() > 0)
      .collect(Collectors.toList());
  }

  private void finalizarRetornoExpedicaoPedido(ExpedicaoPedido expedicaoPedido, MotivoDrogatelDTO motivoNaoEntrega) {
    expedicaoPedido.setIndicadorRetorno(SimNaoEnum.S);
    expedicaoPedido.setCodMotivoNaoEntrega(motivoNaoEntrega.getId().intValue());
    expedicaoPedido.setMotivoNaoEntrega(motivoNaoEntrega.getDescricao());
    expedicaoPedido.getExpedicao().setDataHoraRetorno(new Date());
    expedicaoPedido.getExpedicao().setResponsavelRetorno(new Usuario(SecurityUtils.getCodigoUsuarioLogado()));

    this.expedicaoPedidoRepository.save(expedicaoPedido);
  }

  private void alterarFaseEEnviarPedidoNaoEntregueParaFilaDaEntrega4ponto0(Integer numeroPedido, MotivoDrogatelDTO motivo) {
    log.info("Alterando fase do pedido [{}] para NÃO ENTREGUE e enviando para fila da ENTREGA 4.0", numeroPedido);

    Integer codigoUsuarioLogado = SecurityUtils.getCodigoUsuarioLogado();
    this.pedidoService.registrarNovaFasePedido(
      numeroPedido.longValue(), FasePedidoEnum.NAO_ENTREGUE.getChave(), codigoUsuarioLogado
    );

    String fila = pedidoService.isPagamentoEmDinheiro(numeroPedido.longValue()) ? 
    							FILA_DEVOLUCAO_COM_DINHEIRO : FILA_PEDIDO_NAO_ENTREGUE;
    
    MotivoDrogatel filaPedidoNaoEntregue = this.motivoDrogatelService.getMotivoByDescricaoCache(fila);
    String descricaoPendencia = String.format("%s: %s", fila, motivo.getDescricao());

    PendenciaPedidoDrogatel pendenciaPedido = new PendenciaPedidoDrogatel(codigoUsuarioLogado.toString());
    pendenciaPedido.setNumeroPedido(numeroPedido);
    pendenciaPedido.setDescricaoPendencia(descricaoPendencia);
    pendenciaPedido.setCodigoMotivoDrogatel(filaPedidoNaoEntregue.getId().intValue());
    pendenciaPedido.setPendenciaResolvida(SimNaoEnum.N);
    pendenciaPedido.setDataCriacao(new Date());
    pendenciaPedido.setTipoPendencia(TipoPendenciaPedidoEnum.DIVERSAS.getChave());
    pendenciaPedido.setResponsavelPendencia(new Usuario(1));

    this.pendenciaPedidoRepository.save(pendenciaPedido);
  }

}
