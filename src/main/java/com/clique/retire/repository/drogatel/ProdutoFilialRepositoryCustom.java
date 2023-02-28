package com.clique.retire.repository.drogatel;

import java.util.Optional;

import com.clique.retire.model.drogatel.ProdutoFilial;

public interface ProdutoFilialRepositoryCustom {

  Optional<ProdutoFilial> obterProdutoFilialParaRegistroFalta(Integer codigoProduto, Integer codigoPolo, Long numeroPedido);

}
