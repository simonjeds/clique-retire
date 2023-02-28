package com.clique.retire.repository.drogatel;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ClienteRepositoryImpl implements ClienteRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	@Transactional("drogatelTransactionManager")
	public void atualizarDocumentoCliente(Long numeroPedido, String documento, String orgaoExpedidor, String ufCliente) {
		if (Objects.isNull(documento) || Objects.isNull(orgaoExpedidor) || Objects.isNull(ufCliente)) 
			return;
		
		em.createNativeQuery(" UPDATE cliente "
							+" SET clnt_tn_ci = :documento, "
							+" clnt_ds_orgao_expedidor_ci = :orgaoExpedidor, "
							+" clnt_cd_uf_ci = :ufCliente "
							+" WHERE clnt_cd_cliente = (SELECT clnt_cd_cliente FROM pedido WHERE pedi_nr_pedido = :numeroPedido) ")
		  .setParameter("documento", documento)
		  .setParameter("orgaoExpedidor", orgaoExpedidor)
		  .setParameter("ufCliente", ufCliente)
		  .setParameter("numeroPedido", numeroPedido)
		  .executeUpdate();
	}

}