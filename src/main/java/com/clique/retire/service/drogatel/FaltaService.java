package com.clique.retire.service.drogatel;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.model.drogatel.Falta;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.model.drogatel.ParametroDrogatel;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Polo;
import com.clique.retire.model.drogatel.Produto;
import com.clique.retire.model.drogatel.ProdutoDeposito;
import com.clique.retire.model.drogatel.ProdutoFilial;
import com.clique.retire.model.drogatel.SeparacaoPedido;
import com.clique.retire.repository.drogatel.DrogatelParametroRepository;
import com.clique.retire.repository.drogatel.FaltaRepository;
import com.clique.retire.repository.drogatel.ParametroDrogatelRepository;
import com.clique.retire.repository.drogatel.ProdutoDepositoRepository;
import com.clique.retire.repository.drogatel.ProdutoFilialRepository;
import com.clique.retire.repository.drogatel.SeparacaoRepository;
import com.clique.retire.wrapper.IntWrapper;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class FaltaService {

  public static final String CONSIDERA_ESTOQUE_RESERVADO = "CONSIDERA_ESTOQUE_RESERVADO";
  public static final Integer CODIGO_PARAMETRO_DROGATEL = 1;
  private static final Integer PRODUTO_NAO_EXISTE = -1;
  private static final Integer ZERO = 0;

  private final FaltaRepository faltaRepository;
  private final SeparacaoRepository separacaoRepository;
  private final DrogatelParametroRepository drogatelParametroRepository;
  private final ProdutoFilialRepository produtoFilialRepository;
  private final ProdutoDepositoRepository produtoDepositoRepository;
  private final ParametroDrogatelRepository parametroDrogatelRepository;

  public Optional<ProdutoFilial> obterProdutoFilialOtimizadoParaRegistroFalta(Produto produto, Polo polo, Pedido pedido) {
    DrogatelParametro drogatelParametro = drogatelParametroRepository.findByNome(CONSIDERA_ESTOQUE_RESERVADO);

    if (drogatelParametro != null && drogatelParametro.getValor().equals(SimNaoEnum.S.name())) {
      return produtoFilialRepository.obterProdutoFilialParaRegistroFalta(
        produto.getCodigo(), polo.getCodigo(), pedido.getNumeroPedido()
      );
    }

    return produtoFilialRepository.obterProdutoFilialParaRegistroFalta(produto, polo.getCodigo());
  }

  public void salvarOuAtualizarFalta(Integer codUsuarioLogado, Pedido pedido, IntWrapper quantidadeFalta, ItemPedido itemPedido) {
    Falta falta = new Falta();

    SeparacaoPedido separacaoPedido = separacaoRepository.obterSeparacaoDoPedido(pedido.getNumeroPedido().intValue());
    if (Objects.isNull(separacaoPedido)) 
    	throw new EntidadeNaoEncontradaException(String.format("Nenhum registro de separação para o pedido %s foi encontrado.", pedido.getNumeroPedido()));
    
    Integer entradaNoDia = faltaRepository.spCsmInformacaoProdParaTrans(
      separacaoPedido.getDataInicio(), pedido.getFilial().getId(), itemPedido.getProduto().getCodigo()
    );

    Optional<ProdutoFilial> produtoFilial = obterProdutoFilialOtimizadoParaRegistroFalta(
      itemPedido.getProduto(), pedido.getPolo(), pedido
    );

    Optional<ProdutoDeposito>  produtoDeposito = Optional.empty();
    if (produtoFilial.isPresent() && produtoFilial.get().getDeposito() != null) {
      produtoDeposito = produtoDepositoRepository.obterProdutoDepositoParaRegistroHistoricoFalta(
        produtoFilial.get().getDeposito(), itemPedido.getProduto()
      );
    }

    if (!produtoDeposito.isPresent()) {
      ParametroDrogatel parametroDrogatel = parametroDrogatelRepository
        .obterParametroDrogatelComDepositoPadrao(CODIGO_PARAMETRO_DROGATEL);

      produtoDeposito = produtoDepositoRepository.obterProdutoDepositoParaRegistroHistoricoFalta(
        parametroDrogatel.getDepositoPadrao(), itemPedido.getProduto()
      );
    }

    falta.setQuantidadeEstoqueDeposito(produtoDeposito.map(ProdutoDeposito::getQuantidadeEstoque).orElse(ZERO));
    falta.setQuantidadeEstoquePolo(produtoFilial.map(ProdutoFilial::getEstoqueAtual).orElse(PRODUTO_NAO_EXISTE));
    falta.setUsuario(codUsuarioLogado);
    falta.setPolo(pedido.getPolo());
    falta.setProduto(itemPedido.getProduto());
    falta.setQuantidadeFalta(quantidadeFalta.getValue());
    falta.setCodigoUsuarioAlteracao(codUsuarioLogado.toString());
    falta.setUltimaAlteracao(new Date());
    falta.setPedido(pedido);
    falta.setQuantidadePedida(itemPedido.getQuantidadePedida());
    falta.setEntradaNoDia(entradaNoDia);
    falta.setDataInicioSeparacao(separacaoPedido.getDataInicio());
    falta.setDataTerminoSeparacao(separacaoPedido.getDataTermino());
    falta.setPrecoDeposito(itemPedido.getPrecoUnitario());
    falta.setQuantidadeReservada(produtoFilial.map(ProdutoFilial::getQuantidadeReservada).orElse(ZERO));
    faltaRepository.save(falta);
  }
}