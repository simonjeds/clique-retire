package com.clique.retire.repository.drogatel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.clique.retire.infra.exception.BusinessException;

@Repository
public class ParametroCliqueRetireRepositoryImpl implements ParametroCliqueRetireCustom{

	private static final String ERRO_AO_CONSULTAR_PARAMETRO_CLIQUE_RETIRE = "Erro ao consultar ParametroCliqueRetire";
	
	@PersistenceContext
	private EntityManager em;

	public String findByNome(String nome) {
		try{
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append(" SELECT p.PCRVAL");
			sbQuery.append(" FROM DRGTBLPCRPARAMETRO p");
			sbQuery.append(" WHERE p.PCRNOM = '" +nome +"'");
	
			Query query = em.createNativeQuery(sbQuery.toString());
	
			return (String) query.getSingleResult();
		}catch(Exception e) {
			throw new BusinessException(ERRO_AO_CONSULTAR_PARAMETRO_CLIQUE_RETIRE + e.getMessage());
		}
	}
	
}
