package com.clique.retire.repository.drogatel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.PedidoServico;

@Repository
public interface ExpedicaoPedidoRepository extends JpaRepository<ExpedicaoPedido, Integer> {

  @Modifying
  @Query(
    value =
      "UPDATE r SET expe_cd_expedicao = :codigoExpedicao " +
      "FROM drgtblrogrotagerada r " +
      "JOIN drgtblprgpedrotagerada rg ON rg.rogcod = r.rogcod " +
      "WHERE (rg.pedi_nr_pedido = :numeroPedido OR rg.pese_nr_servico = :numeroPedido) AND r.rogstarota = 'E' AND r.expe_cd_expedicao IS NULL",
    nativeQuery = true
  )
  void atualizarExpedicaoNaRotaGerada(Long numeroPedido, Integer codigoExpedicao);
  
  List<ExpedicaoPedido> findByPedidoServicoAndIndicadorRetornoOrderByCodigoDesc(PedidoServico pedidoServico, SimNaoEnum indicadorRetorno);

}
