package com.clique.retire.repository.drogatel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.clique.retire.dto.HistoricoMetricaDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.HistoricoMetrica;

@Repository
public class HistoricoMetricaCustomImpl implements HistoricoMetricaRepositoryCustom {
	
	private static final String ERRO_AO_SALVAR_METRICAS = "Erro ao salvar métricas";
	private static final String COD_USUARIO = "1";
	private static final Integer VERSAO_REGISTRO = 1;

	private static final String PAINEL_CLIQUE_RETIRE = "PAINEL-CLIQUE RETIRE";
	private static final String CLIQUE_RETIRE = "CLIQUE RETIRE";

	private static final Logger LOGGER = LoggerFactory.getLogger(HistoricoMetricaCustomImpl.class);
	
	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricoMetricaDTO> consultarMetricas(LocalDate dataInicial, LocalDate dataFinal) {

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT hm.MCRSEQ, hm.PEDI_NR_PEDIDO, filial.fili_cd_filial, hm.MCRTPOINTEGRAR, hm.MCRTPOSEPARAR, ");
        sql.append("    hm.MCRTPOREGISTRAR, filial.fili_nm_fantasia, ");

        sql.append("    (Select COUNT(*) FROM pedido p (nolock) WHERE p.pedi_fl_fase = '");
        sql.append(FasePedidoEnum.ATENDIDO.getChave()).append("' AND p.polo_cd_polo = ");
        sql.append("             filial.fili_cd_filial AND p.pedi_fl_operacao_loja = 'S') as novosPedidos, ");

        sql.append("    (Select COUNT(*) FROM pedido p (nolock) WHERE ");
        sql.append("    CAST(p.PEDI_DH_TERMINO_ATENDIMENTO AS DATE) BETWEEN '").append(dataInicial +"\'").append(" AND \'" +dataFinal +"\' ");
        sql.append("    AND p.polo_cd_polo = filial.fili_cd_filial AND p.pedi_fl_operacao_loja = 'S') as pedidosDestaLoja ");

        sql.append(" FROM DRGTBLMCRMETRICAS_HST hm (nolock) ");
        sql.append("    RIGHT JOIN FILIAL filial (nolock)  ON hm.POLO_CD_POLO = filial.fili_cd_filial ");
        sql.append("    INNER JOIN POLO polo (nolock) ON polo.POLO_CD_POLO = filial.fili_cd_filial ");
        sql.append(" WHERE hm.hdrdthins IS NULL OR CAST(hm.hdrdthins AS DATE) BETWEEN '")
		.append(dataInicial +"\'").append(" AND " +"\'" +dataFinal +"\'");
		
		Query query = em.createNativeQuery(sql.toString());
		
		List<Object[]> listResultado = query.getResultList();
		
		List<HistoricoMetricaDTO> listHistoricoMetricaDTO = new ArrayList<>();
		
		listResultado.forEach(item -> {
			HistoricoMetricaDTO historicoMetricaDTO = new HistoricoMetricaDTO();
			historicoMetricaDTO.setId((Integer) item[0]);
			historicoMetricaDTO.setNumeroPedido((Integer) item[1]);
			historicoMetricaDTO.setNumeroFilial((Integer) item[2]);
			historicoMetricaDTO.setTempoIntegracao((Integer) item[3]);
			historicoMetricaDTO.setTempoInicioSeparacao((Integer) item[4]);
			historicoMetricaDTO.setTempoRegistro((Integer) item[5]);
			historicoMetricaDTO.setNomeFilial((String) item[6]);
			historicoMetricaDTO.setNovosPedidos((Integer) item[7]);
			historicoMetricaDTO.setQuantidadePedidosDestaLoja((Integer) item[8]);
			
			listHistoricoMetricaDTO.add(historicoMetricaDTO);
		});
		
		return listHistoricoMetricaDTO;
	}

	public void salvarEmBatch(List<HistoricoMetrica> metricas) {
		
		try {
			
			StringBuilder sql = new StringBuilder("INSERT INTO DRGTBLMCRMETRICAS_HST ");
			sql.append("    (PEDI_NR_PEDIDO, POLO_CD_POLO, MCRTPOINTEGRAR, MCRTPOSEPARAR, MCRTPOREGISTRAR, ");
			sql.append("    HDRDTHHOR, HDRCODUSU, HDRCODLCK, HDRDTHINS, HDRCODETC, HDRCODPRG) VALUES ");
			
			for (HistoricoMetrica historico : metricas) {
				sql.append("(").append(historico.getNumeroPedido()).append(",")
						.append(historico.getNumeroFilial()).append(",")
						.append(historico.getTempoIntegracao()).append(",")
						.append(historico.getTempoSeparacao()).append(",")
						.append(historico.getTempoRegistro()).append(",")
						.append(":dataAlteracao, :codigoUsuario, :versaoRegistro, :dataInsercao, :estacao, :programa),");
			}
			
			sql.deleteCharAt(sql.lastIndexOf(","));
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("codigoUsuario", COD_USUARIO);
			query.setParameter("versaoRegistro", VERSAO_REGISTRO);
			query.setParameter("dataInsercao", new Date());
			query.setParameter("dataAlteracao", new Date());
			query.setParameter("estacao", CLIQUE_RETIRE);
			query.setParameter("programa", PAINEL_CLIQUE_RETIRE);
			query.executeUpdate();
		}catch(Exception e) {
			LOGGER.info("", e);
			e.printStackTrace();
			throw new BusinessException(ERRO_AO_SALVAR_METRICAS);
		}
		
	}
}
