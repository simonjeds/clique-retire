package com.clique.retire.repository.drogatel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class NegociacaoRepositoryImpl implements NegociacaoRepositoryCustom {

	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager em;
	
	public void finalizarNegociacao(Long numeroPedido) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE NEGOCIACAO_PEDIDO ")
		   .append(" SET NGPD_DH_TERMINO = current_timestamp, NGPD_FL_SITUACAO = '08' ") 
		   .append(" WHERE PEDI_NR_PEDIDO = :numeroPedido and NGPD_DH_TERMINO is null ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("numeroPedido", numeroPedido);
		
		query.executeUpdate();
	}
}