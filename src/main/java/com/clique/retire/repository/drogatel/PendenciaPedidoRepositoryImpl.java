package com.clique.retire.repository.drogatel;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.util.Constantes;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PendenciaPedidoRepositoryImpl implements PendenciaPedidoRepositoryCustom {

  @PersistenceContext(unitName = "drogatelEntityManager")
  private EntityManager em;

  @Override
  public void removerPendenciaPedidoControladoNaoEntregueNoPrazo(Long numeroPedido) {
    StringBuilder sql = new StringBuilder()
      .append("DELETE FROM pendencia_pedido_drogatel ")
      .append("WHERE pedi_nr_pedido = :numeroPedido ")
      .append("  AND pepd_fl_pendencia_resolvida = :resolvida ")
      .append("  AND mtdr_cd_motivo_drogatel = (")
      .append("    SELECT md.mtdr_cd_motivo_drogatel FROM motivo_drogatel md WHERE md.mtdr_ds_descricao = :fila ")
      .append("  )");

    this.em.createNativeQuery(sql.toString())
      .setParameter("numeroPedido", numeroPedido)
      .setParameter("resolvida", SimNaoEnum.N.getDescricao())
      .setParameter("fila", Constantes.FILA_CONTROLADO_NAO_ENTREGUE_NO_PRAZO)
      .executeUpdate();
  }

}
