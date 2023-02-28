package com.clique.retire.repository.cosmos;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PrePedidoRepositoryImpl implements PrePedidoRepositoryCustom {
	
	@PersistenceContext(unitName="cosmosEntityManager")
	private EntityManager em;

	@Override
	public String buscarCodigoPrePedidoOrigem(Integer numeroPrePedido, Integer codigoFilial) {
		try {
			StringBuilder sql = new StringBuilder()
				.append("SELECT o.ppocodorigem ")
				.append("FROM fljinsdbs.dbo.instblppoorigemprepedido o (NOLOCK) ")
				.append("INNER JOIN fljinsdbs.dbo.instblppdprepeddestino d (NOLOCK) ON d.fili_cd_filial = :codigoFilial AND o.pposeq  = d.pposeq ")
				.append("WHERE o.uppcodprepedido = :numeroPrePedido AND o.ppoidcorigem = 'T' ");		
				
			return (String) em.createNativeQuery(sql.toString())
							  .setParameter("numeroPrePedido", numeroPrePedido)
							  .setParameter("codigoFilial", codigoFilial)
							  .getSingleResult();
			
		} catch (NoResultException e) {
			log.info("Não foi indentificado nenhum código de origem para o pré-pedido ARAUJO TEM {} na filial {}.", numeroPrePedido, codigoFilial);
			return null;
		}
	}

}