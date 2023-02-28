package com.clique.retire.repository.drogatel;

import static com.clique.retire.util.Constantes.NUMERO_PEDIDO;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.LoteBipadoDTO;

@Repository
public class LoteBipadoRepositoryImpl implements LoteBipadoRepositoryCustom {

  @PersistenceContext
  private EntityManager em;

  @SuppressWarnings("unchecked")
  public List<LoteBipadoDTO> buscarLotesPorPedido(Long numeroPedido) {
    StringBuilder sql = new StringBuilder("select ")
      .append(" LTBP_NR_LOTE, sum(LTBP_NR_QUANTIDADE_BIPADA), ITPD_CD_ITEM_PEDIDO ")
      .append("from LOTE_BIPADO ")
      .append("where ITPD_CD_ITEM_PEDIDO in (select ITPD_CD_ITEM_PEDIDO from item_pedido where PEDI_NR_PEDIDO = :numeroPedido) ")
      .append("group by LTBP_NR_LOTE, ITPD_CD_ITEM_PEDIDO ")
      .append("order by LTBP_NR_LOTE ");

    List<Object[]> result = em.createNativeQuery(sql.toString())
      .setParameter(NUMERO_PEDIDO, numeroPedido)
      .getResultList();

    return result.stream()
      .map(l -> LoteBipadoDTO.builder()
        .lote((String) l[0])
        .quantidade((Integer) l[1])
        .idItemPedido((Integer) l[2])
        .build()
      ).collect(Collectors.toList());
  }
  
  public boolean existeDiferencaReferenciaItemNotaFiscal(Long numeroPedido) {
	  return !em.createNativeQuery(
			  		new StringBuilder("SELECT lb.ITNO_CD_ITEM ")
							  .append("FROM LOTE_BIPADO lb ")
							  .append("JOIN ITEM_PEDIDO ip ON lb.ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO ")
							  .append("JOIN ITEM_NOTA_FISCAL inf ON lb.ITPD_CD_ITEM_PEDIDO = inf.ITPD_CD_ITEM_PEDIDO ")
							  .append("WHERE ip.PEDI_NR_PEDIDO = :numeroPedido AND lb.ITNO_CD_ITEM <> inf.ITNO_CD_ITEM ").toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList()
				.isEmpty();
  }


  @Override
  @Transactional("drogatelTransactionManager")
  public void atualizarCodigoItemNotaFiscal(Long numeroPedido) {
    String sql = "UPDATE lb SET lb.ITNO_CD_ITEM = inf.ITNO_CD_ITEM "
      .concat("FROM LOTE_BIPADO lb ")
      .concat("JOIN ITEM_PEDIDO ip ON lb.ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO ")
      .concat("JOIN ITEM_NOTA_FISCAL inf ON lb.ITPD_CD_ITEM_PEDIDO = inf.ITPD_CD_ITEM_PEDIDO ")
      .concat("WHERE ip.PEDI_NR_PEDIDO = :numeroPedido AND lb.ITNO_CD_ITEM <> inf.ITNO_CD_ITEM");

    em.createNativeQuery(sql)
      .setParameter(NUMERO_PEDIDO, numeroPedido)
      .executeUpdate();
  }
  
  public boolean existeLoteBipadoEOuReceitaExcedenteOuFaltante(Integer numeroPedido) {
	  return !em.createNativeQuery(
			  		new StringBuilder("SELECT ")
		  					  .append("    ip.ITPD_CD_ITEM_PEDIDO ")
		  					  .append("FROM ITEM_PEDIDO ip (nolock) ")
		  					  .append("WHERE ip.PEDI_NR_PEDIDO = :numeroPedido AND ip.ITPD_FL_PRODUTO_CONTROLADO = 'S'")
		  					  .append("      AND (ip.ITPD_NR_QUANTIDADE_PEDIDA <> COALESCE((SELECT SUM(LTBP_NR_QUANTIDADE_BIPADA) FROM LOTE_BIPADO (nolock) WHERE ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO),0) ")
		  					  .append("           OR ip.ITPD_NR_QUANTIDADE_PEDIDA <> COALESCE((SELECT SUM(rcpc_nr_caixas) FROM RECEITA_PRODUTO_CONTROLADO (nolock) WHERE ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO),0)) ")
		  					  .toString())
			    .setParameter(NUMERO_PEDIDO, numeroPedido)
			    .getResultList()
			    .isEmpty();
  }
  

}