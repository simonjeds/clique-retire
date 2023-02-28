package com.clique.retire.repository.drogatel;

import com.clique.retire.dto.CancelamentoPedidoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

@Repository
public class CancelamentoPedidoRepositoryImpl implements CancelamentoPedidoRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  private String getQueryPedidoCanceladoPorFilial(boolean isCountQuery) {
    String select = isCountQuery
      ? "SELECT COUNT(1) "
      : "SELECT TOP 1 p.pedi_nr_pedido AS numero_pedido, c.clnt_ds_nome AS nome_cliente ";

    String join = isCountQuery ? "" : "INNER JOIN cliente c (NOLOCK) ON p.clnt_cd_cliente = c.clnt_cd_cliente  ";

    StringBuilder sql = new StringBuilder()
      .append(select)
      .append("FROM pedido p (NOLOCK) ")
      .append(join)
      .append("WHERE p.polo_cd_polo = :codFilial ")
      .append("  AND p.pedi_fl_operacao_loja = 'S' ")
      .append("  AND p.pedi_dh_termino_atendimento > '20211111' ")
      .append("  AND p.pedi_fl_fase = :cancelado ")
      .append("  AND EXISTS ( ")
      .append("    SELECT 1 FROM drgtblhfphistfasepedido_hst hist (NOLOCK) ")
      .append("    WHERE hist.pedi_nr_pedido = p.pedi_nr_pedido AND hist.pedi_fl_fase_atual = '29' ")
      .append("  ) ")
      .append("  AND NOT EXISTS ( ")
      .append("    SELECT 1 FROM drgtblcpccancpedcontrol (NOLOCK) cpc WHERE cpc.pedi_nr_pedido = p.pedi_nr_pedido")
      .append("  ) ");

    if (!isCountQuery) {
      sql.append("ORDER BY p.pedi_dh_termino_atendimento ");
    }

    return sql.toString();
  }

  @SuppressWarnings("unchecked")
  public Optional<CancelamentoPedidoDTO> buscarPedidoCancelamentoPorFilial(Integer codFilial) {
    String sql = this.getQueryPedidoCanceladoPorFilial(false);
    List<Tuple> pedidos = em.createNativeQuery(sql, Tuple.class)
      .setParameter("codFilial", codFilial)
      .setParameter("cancelado", FasePedidoEnum.CANCELADO.getChave())
      .getResultList();

    if (pedidos.isEmpty()) {
      return Optional.empty();
    }

    Tuple pedido = pedidos.iterator().next();

    CancelamentoPedidoDTO cancelamentoPedidoDTO = CancelamentoPedidoDTO.builder()
      .numeroPedido(pedido.get("numero_pedido", Integer.class))
      .nomeCliente(pedido.get("nome_cliente", String.class))
      .build();

    return Optional.of(cancelamentoPedidoDTO);
  }

  @Override
  public Integer buscarQuantidadePedidoCancelamentoPorFilial(Integer codFilial) {
    String sql = this.getQueryPedidoCanceladoPorFilial(true);
    return (Integer) em.createNativeQuery(sql)
      .setParameter("codFilial", codFilial)
      .setParameter("cancelado", FasePedidoEnum.CANCELADO.getChave())
      .getSingleResult();
  }

}
