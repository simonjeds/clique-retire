package com.clique.retire.repository.drogatel;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
public class MarketingPlaceRepositoryImpl implements MarketingPlaceRepositoryCustom {

	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager em;
	
	/**
	 * Método que consulta qual a descrição do pedido marketing place.
	 * 
	 * @param numeroPedidoEcommerce
	 * @return descrição marketing place
	 */
	@Override
	public String obterDescricao(String numeroPedidoEcommerce) {
		try {
			StringBuilder sql = new StringBuilder()
				.append(" select mkt.MKTNOM ")
				.append(" from LKSQL01.CSMECMDBS.dbo.ECMTBLMKTMARKETPLACE mkt ")
				.append(" where mkt.MKTSIG = SUBSTRING(:numeroPedidoEcommerce, 1, 3) ");

			return (String) em.createNativeQuery(sql.toString())
				.setParameter("numeroPedidoEcommerce", numeroPedidoEcommerce)
				.getSingleResult();
		} catch (NoResultException e) {
			log.warn("Não foi identificado nenhum convênio MARKETPLACE para o identificador ecommerce {}.", numeroPedidoEcommerce);
		}

		return null;
	}

}
