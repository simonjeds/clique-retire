package com.clique.retire.repository.drogatel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.clique.retire.dto.ItemHistoricoAtendimentoNFDTO;
import com.clique.retire.dto.NotaFiscalDTO;
import com.clique.retire.dto.RelatorioHistoricoNFDTO;
import com.clique.retire.infra.exception.BusinessException;

@Repository
public class HistoricoAtendimentoNFRepositoryImpl implements HistoricoAtendimentoNFRepositoryCustom {

	private static final String DATA_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String NOTA_FISCAL_NAO_ENCONTRADA = "Nota fiscal não encontrada.";
	private static final String NOTA_FISCAL_NAO_RECEBIDA = "A Nota fiscal ainda não foi recebida.";
	private static final String CHAVE_NF = "chaveNF";


	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager emDrogatel;

	@PersistenceContext(unitName = "cosmosEntityManager")
	private EntityManager emCosmos;

	@Override
	public NotaFiscalDTO buscarNotaFiscal(String chaveNF) throws ParseException {

		StringBuilder sql = new StringBuilder("SELECT NOFI_CD_PEDTRANSF ")
				.append(" FROM NOTA_FISCAL nf (nolock) WHERE NOFI_TX_CHAVE_NFE = :chaveNF ");

		Query query = emCosmos.createNativeQuery(sql.toString());
		query.setParameter(CHAVE_NF, chaveNF);

		Integer codigoPedidoMercadoria = null;
		try {
			codigoPedidoMercadoria = (Integer) query.getSingleResult();
		} catch (NoResultException e) {
			throw new BusinessException(NOTA_FISCAL_NAO_ENCONTRADA);
		}

		NotaFiscalDTO notaFiscal = new NotaFiscalDTO();
		notaFiscal.setChave(chaveNF);

        sql = new StringBuilder("SELECT top 1 u.USUA_NM_USUARIO, e.EXPE_DH_RETORNO ")
                .append(" FROM EXPEDICAO_PEDIDO ep (nolock) INNER JOIN EXPEDICAO e (nolock) ON e.EXPE_CD_EXPEDICAO = ep.EXPE_CD_EXPEDICAO ")
                .append("   INNER JOIN USUARIO u (nolock) ON e.USUA_CD_RESP_RETORNO = u.USUA_CD_USUARIO ")
                .append("   WHERE ep.PDMC_CD_PEDIDO_MERCADORIA = :codigoPedidoMercadoria  ORDER BY ep.EXPD_CD_EXPEDICAO_PEDIDO desc");

		query = emDrogatel.createNativeQuery(sql.toString());
		query.setParameter("codigoPedidoMercadoria", codigoPedidoMercadoria);
		Object[] resultado;
		try {
			resultado = (Object[]) query.getSingleResult();
		} catch (NoResultException e) {
			throw new BusinessException(NOTA_FISCAL_NAO_RECEBIDA);
		}


		try {
			notaFiscal.setRecebedor(resultado[0].toString());
			notaFiscal.setDataRecebimento(new java.text.SimpleDateFormat(DATA_PATTERN).parse(resultado[1].toString()));
		} catch (NullPointerException e) {
			throw new BusinessException(NOTA_FISCAL_NAO_RECEBIDA);
		}

		notaFiscal.setNotaFiscalJaRecebida(true);

		return notaFiscal;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RelatorioHistoricoNFDTO> buscarHistoricoPorNotaParaImpressao(NotaFiscalDTO notaFiscal) {
		StringBuilder sql = new StringBuilder("select p.PEDI_NR_PEDIDO, c.CLNT_DS_NOME, ")
		.append("   (SELECT COUNT(*) FROM item_pedido ip (nolock) WHERE ip.pedi_nr_pedido = p.pedi_nr_pedido AND ip.itpd_nr_quantidade_separada + ip.itpd_nr_quantidade_negociada_recebida < ip.itpd_nr_quantidade_pedida) as quantidadeItensPendentes, ")
		.append("   f.fili_nm_fantasia, cb.COBM_CD_BARRA, pm.PRME_CD_PRODUTO, pm.prme_tx_descricao1, qtdRecebida, ")
		.append("   convert(varchar, pm.prme_cd_produto) + '-' + convert(varchar, pm.prme_nr_dv) as codigoComDigito, ")
		.append("   p.FILI_CD_FILIAL_ARAUJO_TEM ")
		.append(" FROM ")
		.append("   (SELECT sum(HNFQTDRECEBIDA) as qtdRecebida, ")
		.append("       PRME_CD_PRODUTO as produto, ")
		.append("       PEDI_NR_PEDIDO as pedido ")
		.append("     from DRGTBLHNFHISTORICONF_HST (nolock) WHERE hnfnumchavenf= :chaveNF AND HNFQTDRECEBIDA > 0 ")
		.append("       group by PRME_CD_PRODUTO, PEDI_NR_PEDIDO " )
		.append("   ) as historico ")
		.append("   INNER JOIN Pedido p (nolock) ON p.pedi_nr_pedido = historico.pedido ")
		.append("   INNER JOIN cliente c (nolock) ON p.clnt_cd_cliente = c.clnt_cd_cliente ")
		.append("   INNER JOIN produto_mestre pm (nolock) ON pm.prme_cd_produto = historico.produto ")
		.append("   INNER JOIN Filial f (nolock) ON p.polo_cd_polo = f.fili_cd_filial ")
		.append("   LEFT JOIN cod_barra_mestre cb (nolock) ON cb.prme_cd_produto = pm.prme_cd_produto and cb.cobm_tp_codbarra='V' and cb.cobm_fl_principal='S' ");

		Query query = emDrogatel.createNativeQuery(sql.toString());
		query.setParameter(CHAVE_NF, notaFiscal.getChave());

		List<Object[]> resultado = query.getResultList();

		List<RelatorioHistoricoNFDTO> lista = new ArrayList<>();
		resultado.forEach(item -> {
			Integer numeroPedido = (Integer) item[0];
			RelatorioHistoricoNFDTO historico = null;
			if (lista.isEmpty() || !numeroPedido.equals(lista.get(lista.size() - 1).getNumeroPedido())) {
				historico = new RelatorioHistoricoNFDTO(notaFiscal.getChave(), notaFiscal.getRecebedor(), notaFiscal.getDataRecebimento(),
						numeroPedido, item[1].toString(), ((Integer) item[2]) == 0, item[3].toString(), (Integer) item[9]);

				lista.add(historico);
			}else {
				historico = lista.get(lista.size() - 1);
			}

			ItemHistoricoAtendimentoNFDTO itemHistorico = new ItemHistoricoAtendimentoNFDTO(numeroPedido,
					(Integer) item[5], item[6].toString(), item[4].toString(), (Integer) item[7], item[8].toString());

			historico.getItens().add(itemHistorico);
		});

		return lista;
	}
}