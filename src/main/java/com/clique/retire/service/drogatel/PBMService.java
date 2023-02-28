package com.clique.retire.service.drogatel;

import com.clique.retire.client.rest.PBMClient;
import com.clique.retire.config.properties.PBMConfigurationProperties;
import com.clique.retire.dto.PBMAutorizadorDTO;
import com.clique.retire.dto.PBMAutorizadorResponseDTO;
import com.clique.retire.dto.PBMClienteDTO;
import com.clique.retire.dto.PBMProdutoDTO;
import com.clique.retire.dto.PBMProdutoResponseDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.infra.exception.BusinessException;
//import com.clique.retire.infra.exception.ModalidadePagamentoInaptaException
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.repository.drogatel.PedidoRepositoryImpl;
import com.clique.retire.util.FeignUtil;
import com.clique.retire.util.NumberUtil;
//import com.clique.retire.util.ThreadUtils
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
//import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PBMService {

  private final PBMConfigurationProperties configuration;
  private final PedidoRepositoryImpl pedidoRepository;
  //private final EdicaoPedidoService edicaoPedidoService
  private final DrogatelParametroRepository parametroRepository;
  //private final ProcessaValoresPedidoService processaValoresPedidoService
  private final PBMClient pbmClient;

  public PBMService(
    PBMConfigurationProperties configuration, PedidoRepositoryImpl pedidoRepository,// @Lazy EdicaoPedidoService edicaoPedidoService,
    DrogatelParametroRepository parametroRepository//, ProcessaValoresPedidoService processaValoresPedidoService
  ) {
    this.configuration = configuration;
    this.pedidoRepository = pedidoRepository;
    //this.edicaoPedidoService = edicaoPedidoService
    this.parametroRepository = parametroRepository;
    //this.processaValoresPedidoService = processaValoresPedidoService
    this.pbmClient = FeignUtil.getPBMClient(configuration.getUrl());
  }

  @Transactional("drogatelTransactionManager")
  public PBMAutorizadorResponseDTO regerarAutorizacaoPedido(Long numeroPedido) {
    try {
      log.info("Regerando PBM para o pedido '{}'", numeroPedido);
      PBMAutorizadorResponseDTO pbmAutorizadorResponseDTO = this.gerarAutorizacaoPBM(numeroPedido);
      log.info("Resposta do PBM para o pedido '{}': {}", numeroPedido, new Gson().toJson(pbmAutorizadorResponseDTO));

      pbmAutorizadorResponseDTO.getProdutos().forEach(produto -> {
        if (StringUtils.isBlank(produto.getNumeroAutorizacao())) {
          throw new BusinessException(
            "Solicitação de PBM não autorizada para o produto ".concat(produto.getCodProduto().toString())
          );
        }
      });

      Pedido pedido = this.pedidoRepository.buscarPedidoPorCodigoPedido(numeroPedido);
      List<ItemPedido> itensComPBM = pedido.getItensPedido().stream()
        .filter(itemPedido -> Objects.nonNull(itemPedido.getItemPedidoPBM()))
        .collect(Collectors.toList());

      Map<Long, PBMProdutoResponseDTO> produtosPorCodigo = pbmAutorizadorResponseDTO.getProdutos().stream()
        .collect(Collectors.toMap(PBMProdutoResponseDTO::getCodProduto, Function.identity()));

      //boolean isPedidoInaptoParaEdicao = !edicaoPedidoService.isPedidoAptoParaEdicao(pedido.getNumeroPedido())

      itensComPBM.forEach(item -> {
        PBMProdutoResponseDTO produtoRespostaAutorizacao = produtosPorCodigo.get(item.getCodigoProduto());
        double precoAutorizado = NumberUtil.round(produtoRespostaAutorizacao.getPrecoVenda(), 2);
        double precoItem = NumberUtil.round(item.getPrecoUnitario(), 2);

        if (precoAutorizado != precoItem) {//precoAutorizado "igualIgual" 0 || "abre parênteses aqui" isPedidoInaptoParaEdicao "EcomercialEcomercial" precoAutorizado "diferenteDe" precoItem "fecha parênteses aqui"
          throw new BusinessException(
            String.format(
              "Solicitação de PBM autorizada, com divergência no valor de venda do produto. Item: %d, PrecoAtual: %f, PrecoPBM: %f.",
              item.getCodigo(), precoItem, precoAutorizado
            )
          );
        }

        item.getItemPedidoPBM().setAutorizacaoPBM(produtoRespostaAutorizacao.getNumeroAutorizacao());
        //item.setPrecoUnitario(precoAutorizado)
      });

      /*double valorTotalItensAnterior = pedido.getValorTotalItens()
      double valorTotalItensAtual = processaValoresPedidoService.calculaValorTotalItensPedido(pedido.getItensPedido())

      if "abre parênteses aqui" valorTotalItensAtual "maiorQue" valorTotalItensAnterior "fecha parênteses aqui" "abre chaves aqui"
        throw new ModalidadePagamentoInaptaException("Valor atual dos itens maior que valor anterior após reautorização de PBM.")
      "fecha chaves aqui"

      processaValoresPedidoService.atualizarValoresDoPedido(pedido)*/

      return pbmAutorizadorResponseDTO;
    } catch (Exception exception) {
      log.error(exception.getMessage(), exception instanceof BusinessException ? null : exception);
      throw new BusinessException("Erro relacionado a PBM. Será necessário refazer a autorização. Favor entra em contato com o SAC no telefone 3270-5000, opção 2.");
    }
  }

  @Transactional("drogatelTransactionManager")
  public PBMAutorizadorResponseDTO gerarAutorizacaoPBM(Long numeroPedido) {
    Pedido pedido = this.pedidoRepository.buscarPedidoPorCodigoPedido(numeroPedido);

    PBMAutorizadorDTO pbmAutorizadorDTO = new PBMAutorizadorDTO();
    pbmAutorizadorDTO.setIdLoja(pedido.getPolo().getCodigo());
    pbmAutorizadorDTO.setCliente(this.gerarDadosCliente(pedido));
    pbmAutorizadorDTO.setProdutos(this.montarListaDeProdutos(pedido));

    return this.gerarAutorizacao(pbmAutorizadorDTO);
  }

  private PBMClienteDTO gerarDadosCliente(Pedido pedido) {
    return PBMClienteDTO.builder()
      .cpf(pedido.getCliente().getDocumento())
      .build();
  }

  private List<PBMProdutoDTO> montarListaDeProdutos(Pedido pedido) {
    List<Integer> idsProdutos = pedido.getItensPedido().stream()
      .filter(itemPedido -> Objects.nonNull(itemPedido.getItemPedidoPBM()))
      .map(ItemPedido::getCodigoProduto)
      .map(Long::intValue)
      .collect(Collectors.toList());
    Map<Integer, String> codigosEan = this.pedidoRepository.buscarCodigoBarraPorProduto(idsProdutos);

    return pedido.getItensPedido().stream()
      .filter(itemPedido -> Objects.nonNull(itemPedido.getItemPedidoPBM()))
      .filter(itemPedido -> Objects.nonNull(codigosEan.get(itemPedido.getCodigoProduto().intValue())))
      .map(itemPedido -> {
        String ean = codigosEan.get(itemPedido.getCodigoProduto().intValue()).split(";")[0];

        PBMProdutoDTO pbmProdutoDTO = new PBMProdutoDTO();
        pbmProdutoDTO.setCodigo(itemPedido.getCodigoProduto());
        pbmProdutoDTO.setQuantidade(itemPedido.getQuantidadePedida());
        pbmProdutoDTO.setPrecoLiquidoEstabelecimento(itemPedido.getPrecoUnitario());
        pbmProdutoDTO.setEan(ean.replaceAll("^0", ""));
        return pbmProdutoDTO;
      }).collect(Collectors.toList());
  }

  private PBMAutorizadorResponseDTO gerarAutorizacao(PBMAutorizadorDTO dto) {
    DrogatelParametro parametroRetentativasPBM = parametroRepository.findByNome(
      ParametroEnum.CLIQUE_RETIRE_QUANTIDADE_RETENTATIVAS_PBM.getDescricao()
    );
    int quantidadeRetentativasPBM = Integer.parseInt(parametroRetentativasPBM.getValor());
    int tentativas = 1;

    do {
      try {
        log.info("Realizando {}ª tentativa de gerar autorização de PBM: {}", tentativas, new Gson().toJson(dto));
        return this.pbmClient.gerarAutorizacao(dto, configuration.getClientId(), configuration.getAccessToken());
      } catch (Exception exception) {
        log.error("Ocorreu um erro na {}ª tentativa de geração do PBM: {}", tentativas, exception.getMessage());
        if (++tentativas > quantidadeRetentativasPBM) {
          throw exception;
        }
        //ThreadUtils.sleep(3)
      }
    } while (true);
  }

}
