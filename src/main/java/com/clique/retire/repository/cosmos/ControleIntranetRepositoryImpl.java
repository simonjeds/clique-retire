package com.clique.retire.repository.cosmos;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class ControleIntranetRepositoryImpl implements ControleIntranetRepositoryCustom {

	@PersistenceContext(unitName="cosmosEntityManager")
	private EntityManager em;

	@Override
	public Integer findFilialByIp(String ip) {
		try {
			String sql = "SELECT p.filial FROM ControleIntranet p WHERE trim(p.ip) = :ip";
			return em.createQuery(sql, Integer.class)
					.setParameter("ip", ip.trim())
					.setMaxResults(1)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public List<String> findIpsPorFilial(Integer idFilial) {
		return em.createQuery("SELECT p.ip FROM ControleIntranet p WHERE p.filial = :idFilial", String.class)
				.setParameter("idFilial", idFilial)
				.getResultList();
	}

}
