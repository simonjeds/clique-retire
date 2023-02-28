package com.clique.retire.repository.cosmos;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;

@Repository
public class UsuarioCosmosRepositoryImpl implements UsuarioCosmosRepositoryCustom {
	
	@PersistenceContext(unitName="cosmosEntityManager")
	private EntityManager em;
	
	@Override
	public Integer buscarCodigoUsuarioPorMatricula(String matricula) {
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("SELECT u.usua_cd_usuario FROM USUARIO u (nolock) ");
		sbQuery.append("WHERE cast(u.usua_tx_matricula as INT) = :matricula ");
		sbQuery.append("AND u.usua_fl_hab_desab = 'H' ");
		
		Query query = em.createNativeQuery(sbQuery.toString());
		query.setParameter("matricula", Integer.valueOf(matricula));
		
		query.setMaxResults(1);
		
		Integer codUsuario = null;
		try {
			codUsuario = (Integer) query.getSingleResult();
		} catch (NoResultException e) {
			throw new EntidadeNaoEncontradaException("Não foi possível localizar o usuário com a matrícula: " + matricula);
		}
		
		return codUsuario;
	}
	
	@Override
	public Integer buscarUsuarioParaConferenciaCaptacao(String matriculaUsuario) {
		try {
			StringBuilder sql = new StringBuilder()
				.append("SELECT DISTINCT u.usua_cd_usuario ")
				.append("FROM usuario u (NOLOCK) ")
				.append("JOIN usu_funcao_sistema ufs (NOLOCK) ON ufs.usua_cd_usuario = u.usua_cd_usuario ")
				.append("WHERE u.usua_fl_hab_desab = 'H' AND ufs.func_cd_funcao <> :codigoFuncaoOperadorDeCaixa ")
				.append("  AND u.usua_tx_matricula = :matriculaUsuario");

			return (Integer) em.createNativeQuery(sql.toString())
				.setParameter("codigoFuncaoOperadorDeCaixa", "4")
				.setParameter("matriculaUsuario", matriculaUsuario)
				.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}
}