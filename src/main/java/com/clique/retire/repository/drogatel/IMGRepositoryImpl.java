package com.clique.retire.repository.drogatel;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IMGRepositoryImpl implements IMGRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	public List<byte[]> obterListaReceitaDigital(Long numeroPedido) {
		return obterListaReceitaDigital(numeroPedido, true)
				 .stream()
				 .map(byte[].class::cast)
				 .collect(Collectors.toList());
	}
	
	public boolean isContemReceitaDigital(Long numeroPedido) {
		return !obterListaReceitaDigital(numeroPedido, false).isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> obterListaReceitaDigital(Long numeroPedido, boolean arquivo) {
		return em.createNativeQuery(
					new StringBuilder(" select ")
							  .append(	arquivo ? "det.REID_FRENTE_RECEITA" : "1")
							  .append(" from COSMOSDROG_IMG.dbo.RECEITA_IMAGEM_CAB cab (nolock) ")
							  .append(" inner join COSMOSDROG_IMG.dbo.RECEITA_IMAGEM_DET det (nolock) on det.REIC_CD_RECEITA_IMAGEM = cab.REIC_CD_RECEITA_IMAGEM ")
							  .append(" where cab.PEDI_NR_PEDIDO = :numeroPedido ").toString())
				  .setParameter("numeroPedido", numeroPedido)
				  .getResultList();
	}

}
