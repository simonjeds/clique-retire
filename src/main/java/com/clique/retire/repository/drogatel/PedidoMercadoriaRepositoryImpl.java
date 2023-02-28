package com.clique.retire.repository.drogatel;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.clique.retire.enums.FasePedidoEnum;

@Repository
public class PedidoMercadoriaRepositoryImpl implements PedidoMercadoriaCustom {

	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager em;

	@Override
	@Transactional
	public void cancelaTransferencia(Integer numeroPedido) {
		StringBuilder query = new StringBuilder(" UPDATE PedidoMercadoria ")
										.append(" SET fasePedido = '").append(FasePedidoEnum.CANCELADO.getChave()).append("'")
										.append(" WHERE codigo = :numeroPedido ");

		em.createQuery(query.toString())
		  .setParameter("numeroPedido", numeroPedido)
		  .executeUpdate();
	}
	
	@Override
	public void removerReferenciaPorItensPedido(List<Integer> idsItensPedido) {
		em.createNativeQuery("UPDATE item_pedido_mercadoria SET itpd_cd_item_pedido = null where itpd_cd_item_pedido IN :idsItensPedido")
		  .setParameter("idsItensPedido", idsItensPedido)
		  .executeUpdate();
		
	}
}