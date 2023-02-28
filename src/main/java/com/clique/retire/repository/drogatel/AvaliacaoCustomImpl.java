package com.clique.retire.repository.drogatel;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.clique.retire.dto.AvaliacaoDTO;
import com.clique.retire.enums.AvaliacaoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.model.drogatel.Avaliacao;

@Repository
public class AvaliacaoCustomImpl implements AvaliacaoCustom {

	private static final String ERRO_AO_CADASTRAR_UMA_AVALIACAO = "Erro ao cadastrar uma avaliação.";

	private static final Logger LOGGER = LoggerFactory.getLogger(AvaliacaoCustomImpl.class);

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public AvaliacaoDTO buscarDadosComplementares(Integer codUsuario) {

		StringBuilder sql = new StringBuilder();
        sql.append("    SELECT ");
        sql.append("    CASE ");
        sql.append("        WHEN pedi_fl_fase_atual = '08' THEN 'QUANTIDADE_SEPARACAO' ");
        sql.append("        WHEN pedi_fl_fase_atual = '11' THEN 'QUANTIDADE_ENTREGA' ");
        sql.append("        WHEN pedi_fl_fase_atual = '15' THEN 'QUANTIDADE_APONTAMENTO_FALTA' ");
        sql.append("        WHEN pedi_fl_fase_atual = '24' THEN 'QUANTIDADE_RECEBIMENTO_MERCADORIA' ");
        sql.append("    ELSE '' ");
        sql.append("    END AS FLUXO_UTILIZADO, ");
        sql.append("    COUNT(*) AS QUANTIDADE ");
        sql.append("    FROM DRGTBLHFPHISTFASEPEDIDO_HST p (nolock) ");
        sql.append("    WHERE pedi_fl_fase_atual in ('08','11', '15', '24') ");
        sql.append("    AND HDRCODUSU = " +codUsuario);
        sql.append("    AND HDRCODPRG = 'PAINEL-CLIQUE-RETIRE-V2' ");
        sql.append("    GROUP BY pedi_fl_fase_atual");

		Query query = em.createNativeQuery(sql.toString());

		List<Object[]> listResultado = query.getResultList();

		AvaliacaoDTO avaliacaoDTO = new AvaliacaoDTO();

		listResultado.forEach(item -> {
			if(item[0].toString().equals(AvaliacaoEnum.QUANTIDADE_SEPARACAO.getDescricao())) {
				avaliacaoDTO.setQuantidadeSeparacoesRealizadas((Integer)item[1]);
			}else if(item[0].toString().equals(AvaliacaoEnum.QUANTIDADE_ENTREGA.getDescricao())) {
				avaliacaoDTO.setQuantidadeEntregasRealizadas((Integer)item[1]);
			}else if(item[0].toString().equals(AvaliacaoEnum.QUANTIDADE_APONTAMENTO_FALTA.getDescricao())) {
				avaliacaoDTO.setQuantidadeApontamentosFaltasRealizadas((Integer)item[1]);
			}else if(item[0].toString().equals(AvaliacaoEnum.QUANTIDADE_RECEBIMENTO_MERCADORIA.getDescricao())) {
				avaliacaoDTO.setQuantidadeRecebimentosMercadoriasRealizadas((Integer)item[1]);
			}
		});


		return avaliacaoDTO;
	}

	@Override
	public void salvarAvaliacao(Avaliacao avaliacao) {
		try {
			LOGGER.info("### Salvando Avaliação ###");
			em.persist(avaliacao);
		}catch(Exception e) {
			e.printStackTrace();
			LOGGER.info("", e);
			throw new BusinessException(ERRO_AO_CADASTRAR_UMA_AVALIACAO);
		}
	}

	@Override
	public Avaliacao recuperarAvaliacaoPorDia(Integer codUsuario, Date dataAtual) {

		try {
			StringBuilder sql = new StringBuilder();
            sql.append("    SELECT ");
            sql.append("        av ");
            sql.append("    FROM Avaliacao av ");
            sql.append("    WHERE cast(av.dataInsercao as date) = :dataAtual ");
            sql.append("    AND av.codigoUsuarioAlteracao = :codUsuario ");
            sql.append("    ORDER BY av.id DESC ");

			Query query = em.createQuery(sql.toString());

			query.setParameter("dataAtual", dataAtual);
			query.setParameter("codUsuario", codUsuario.toString());

			query.setMaxResults(1);

			return (Avaliacao) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}
	}

}
