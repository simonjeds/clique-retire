package com.clique.retire.repository.drogatel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Produto;
import com.clique.retire.model.drogatel.ProdutoFilial;
import com.clique.retire.model.drogatel.ProdutoFilialId;

@Repository
public interface ProdutoFilialRepository extends JpaRepository<ProdutoFilial, ProdutoFilialId>,
  ProdutoFilialRepositoryCustom {

  @Query(
    "SELECT pf FROM ProdutoFilial pf " +
    "WHERE pf.produtoFilialId.produtoMestre = :produto  AND pf.produtoFilialId.filial.id = :codigoFilial "
  )
  Optional<ProdutoFilial> obterProdutoFilialParaRegistroFalta(Produto produto, Integer codigoFilial);

}
