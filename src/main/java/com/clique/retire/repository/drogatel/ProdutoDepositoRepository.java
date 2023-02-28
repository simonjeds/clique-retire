package com.clique.retire.repository.drogatel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Deposito;
import com.clique.retire.model.drogatel.Produto;
import com.clique.retire.model.drogatel.ProdutoDeposito;
import com.clique.retire.model.drogatel.ProdutoDepositoId;

@Repository
public interface ProdutoDepositoRepository extends JpaRepository<ProdutoDeposito, ProdutoDepositoId> {

  @Query(
    "FROM ProdutoDeposito pd " +
    "WHERE pd.produtoDepositoId.deposito = :deposito AND pd.produtoDepositoId.produtoMestre = :produto "
  )
  Optional<ProdutoDeposito> obterProdutoDepositoParaRegistroHistoricoFalta(
    @Param("deposito") Deposito deposito, @Param("produto") Produto produto
  );

}
