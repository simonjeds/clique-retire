package com.clique.retire.repository.drogatel;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ExpedicaoRepositoryImpl  implements ExpedicaoRepositoryCustom{
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Método para gravar a hora que o pedido foi para negociacao.
	 * 
	 * @param numeroPedido
	 */
	
	@Transactional("drogatelTransactionManager")
	public void gravarRetornoExpedicaoPedido(Date dataEntrega, Integer numeroPedido) {
		StringBuilder sql = new StringBuilder();

		sql.append(" update ep set EXPD_DH_CHEGOU_CLIENTE = ?, EXPD_DH_REAL_ENTREGA=?, ");
		sql.append(" EXPD_DH_ENTREGA_PEDIDO_MOBILE=?, EXPD_FL_RETORNO = 'S' ");
		 
		sql.append(" from  EXPEDICAO_PEDIDO ep ");
		sql.append(" INNER JOIN EXPEDICAO E ON E.EXPE_CD_EXPEDICAO = EP.EXPE_CD_EXPEDICAO ");
		sql.append(" where pedi_nr_pedido = ? and  E.EXPE_DH_RETORNO IS NULL ");
		
		 em.createNativeQuery(sql.toString()).setParameter(1, dataEntrega).setParameter(2, dataEntrega)
		 .setParameter(3, dataEntrega).setParameter(4, numeroPedido).executeUpdate();
		 
		
		 sql = new StringBuilder();
		 sql.append(" update E set EXPE_DH_RETORNO = ?, USUA_CD_RESP_RETORNO=? ");
		 sql.append(" from  EXPEDICAO_PEDIDO ep ");
		 sql.append(" INNER JOIN EXPEDICAO E ON E.EXPE_CD_EXPEDICAO = EP.EXPE_CD_EXPEDICAO ");
		 sql.append(" where pedi_nr_pedido = ? and  E.EXPE_DH_RETORNO IS NULL ");
		 
		 em.createNativeQuery(sql.toString()).setParameter(1, dataEntrega).setParameter(2, 1)
			.setParameter(3, numeroPedido).executeUpdate();

	}
	
}
