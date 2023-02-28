package com.clique.retire.repository.drogatel;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.clique.retire.dto.FilialDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.model.drogatel.Filial;

@Repository
public class FilialRepositoryCustomImpl implements FilialRepositoryCustom{

	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager em;
	
	/**
	 * Método que consulta do nome da impressora da filial.
	 * 
	 * @param codigoFilial
	 * @return nome da impressora.
	 */
	public String obterNomeImpressora(Integer codigoFilial) {
		Query query = em.createNativeQuery("select top 1 FIENOMIMP as nomeImpressora from DRGTBLFIEFILIALECM (nolock) where FILI_CD_FILIAL = :codFilial ");
		query.setParameter("codFilial", codigoFilial);

		return (String)query.getSingleResult();
	}

	@Override
	public Integer buscarQuantidadeNovosPedidos(Integer filial) {
		StringBuilder sql = new StringBuilder()
			.append("SELECT COUNT(*) FROM pedido p (NOLOCK) ")
			.append("LEFT JOIN drgtblpdcpedidocompl compl (NOLOCK) ON compl.pdcnrpedido = p.pedi_nr_pedido ")
			.append("WHERE p.pedi_fl_fase = :fase ")
			.append("  AND p.polo_cd_polo = :filial ")
			.append("  AND p.pedi_fl_operacao_loja = 'S' ")
			.append("  AND COALESCE(p.pedi_fl_formula, 'N') = 'N' ")
			.append("  AND COALESCE(compl.pdcidcpapafila, 'N') = 'N' ");

		return (Integer) em.createNativeQuery(sql.toString())
			.setParameter("fase", FasePedidoEnum.ATENDIDO.getChave())
			.setParameter("filial", filial)
			.getSingleResult();
	}

	@Override
	public Filial findFilialById(Integer codigoFilial) {
		return em.find(Filial.class, codigoFilial);
	}
	
	
	public FilialDTO findFilialByIDEtiqueta (Integer idFilial) {
		
		StringBuilder sql = new StringBuilder();		
		sql.append("SELECT DISTINCT f.FILI_CD_FILIAL as cod_filial, ");
		sql.append("     f.FILI_NM_FANTASIA as nm_fantasia, ");
		sql.append("     f.FILI_TN_DDD1 as ddd, ");
		sql.append("     f.FILI_TN_TEL1 as tel, ");
		sql.append("     f.FILI_TP_LOGRAD as tipo_lg, ");
		sql.append("     f.FILI_NM_LOGRAD as logradouro, ");
		sql.append("     f.FILI_TX_NR_LOGRAD as nr_lograd, ");
		sql.append("     f.FILI_NM_COMPLEMEN as complemento, ");
	    sql.append("     f.FILI_NM_BAIRRO as bairro, ");
		sql.append("     cep.NOME_LOCAL as cidade, ");
		sql.append("     f.FILI_TN_CEP as cep, ");
		sql.append("     f.ESTA_SG_UF as uf, ");
		sql.append("     f.FILI_TN_CNPJ as cnpj ");
		sql.append("FROM FILIAL f (nolock) ");
		sql.append("INNER JOIN CEP_LOG cl (nolock) ON (f.FILI_TN_CEP = cl.CEP8_LOG) ");
		sql.append("INNER JOIN CEP_LOC cep (nolock) ON (cep.CHAVE_LOCAL = cl.CHVLOCAL_LOG) ");
		sql.append("WHERE f.FILI_CD_FILIAL = :idFilial ");
		
		@SuppressWarnings("unchecked")
		List<Tuple> lista = ((TypedQuery<Tuple>) em.createNativeQuery(sql.toString(), Tuple.class)
												   .setParameter("idFilial", idFilial)
							).getResultList();
		
		if (!lista.isEmpty()) {
			Tuple filial = lista.get(0);
			
			String endereco = new StringBuilder(filial.get("tipo_lg").toString()).append(" ")
					.append(filial.get("logradouro").toString()).append(", ")
					.append(filial.get("nr_lograd").toString()).append(" ")
					.append(Objects.nonNull(filial.get("complemento")) ? filial.get("complemento").toString() : "")
					.toString();
			
			return FilialDTO.builder()
							.id(idFilial)
							.nome(filial.get("nm_fantasia").toString())
							.ddd(filial.get("ddd").toString())
							.telefone(filial.get("tel").toString())
							.endereco(endereco)
							.bairro(filial.get("bairro").toString())
							.cidade(filial.get("cidade").toString())
							.cep(filial.get("cep").toString())
							.siglaEstado(filial.get("uf").toString())
							.cnpj(filial.get("cnpj").toString())
							.build();
		}
		
		return FilialDTO.filialSemDados(idFilial);
	}
}
