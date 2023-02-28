package com.clique.retire.repository.drogatel;

import com.clique.retire.dto.*;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.ItemPedido;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.WebUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.swing.text.MaskFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemPedidoRepositoryCustomImpl implements ItemPedidoRepositoryCustom {

	private final DateFormat dateFormatterVencCurto = new SimpleDateFormat("dd/MM/yyyy");
	private static final String EMITIDO_EM = "Emitido em ";
	private static final String NUMERO_PEDIDO = "numeroPedido";
	private static final String CODIGO_ITEM_PEDIDO = "codigoItemPedido";

	@PersistenceContext
	private EntityManager em;

	/**
	 * Método para aualizar os itens do pedido ao iniciar a separação.
	 *
	 * @param numeroPedido
	 */
	public void atualizarItensPedidoSeparacao(Long numeroPedido) {
		StringBuilder sql = new StringBuilder();

		sql.append("update item_pedido set xxxx_dh_alt=?, ");
		sql.append("ITPD_NR_QUANTIDADE_SEPARADA = ITPD_NR_QUANTIDADE_PEDIDA - ITPD_NR_QUANTIDADE_NEGOCIADA, ");
		sql.append("ITPD_FL_ITEM_SEPARADO = 'S' ");
		sql.append("where pedi_nr_pedido = ? ");

		em.createNativeQuery(sql.toString()).setParameter(1, new Date()).setParameter(2, numeroPedido).executeUpdate();
	}

	/**
	 * Método obtem AS informações para montar os itens para impressão do pedido.
	 *
	 * @param numeroPedido
	 * @return List de RelatorioProdutoSeparacaoDTO.
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioProdutoSeparacaoDTO> obterItensPedidoParaImpressao(
		Long numeroPedido, RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO
	) {
		StringBuilder sql = new StringBuilder()
		.append("SELECT COALESCE(CONVERT(VARCHAR, pm.prme_cd_produto) + '-' + CONVERT(VARCHAR, pm.prme_nr_dv), '') AS codigo,  ")
		.append("       COALESCE(cb.cobm_cd_barra, '') AS cod_barras,  ")
		.append("       COALESCE(prme_tx_descricao1,'') AS descricao,  ")
		.append("       COALESCE(itpd_nr_quantidade_pedida,'') AS quantidade,  ")
		.append("       ISNULL(prfi_qt_estoqatual, 0) AS estoque, ")
		.append("       COALESCE(UPPER(est1.espa_ds_categoria),'') AS secao, ")
		.append("       CASE ")
		.append("           WHEN v.pmpv_dt_fimval IS NULL AND v.tplp_sq_tplistasps IS NOT NULL THEN 'TRUE'  ")
		.append("           ELSE 'FALSE'  ")
		.append("       END AS psicotropico, ")
		.append("       CASE  ")
		.append("           WHEN valoranbitiotico IS NOT NULL THEN 'TRUE'  ")
		.append("           ELSE 'FALSE'  ")
		.append("       END AS antibiotico, ")
		.append("       COALESCE(ippidctippbm,'') AS farmacia_popular, ")
		.append("       COALESCE(CAST(uppcodprepedido AS VARCHAR),'') AS num_pre_pedido, ")
		.append("       pm.prme_cd_produto AS codigo_produto, ")
		.append("       vc.ivcdatvalidadepedido AS data_validade_venc_curto, ")
		.append("       ip.itpd_fl_produto_controlado AS controlado,")
		.append("       pm.prme_fl_geladeira AS geladeira, ")
		.append("       COALESCE(pm.prme_tx_descricao2,'') AS descricao_longa ")
		.append("FROM item_pedido ip (NOLOCK)  ")
		.append("INNER JOIN pedido p (NOLOCK) ON p.pedi_nr_pedido = ip.pedi_nr_pedido ")
		.append("INNER JOIN produto_mestre pm (NOLOCK) ON ip.prme_cd_produto = pm.prme_cd_produto ")
		.append("LEFT JOIN drgtblippitempedidopbm pbm (NOLOCK) ON ip.itpd_cd_item_pedido = pbm.itpd_cd_item_pedido  ")
		.append("INNER JOIN cod_barra_mestre cb (NOLOCK) ON cb.prme_cd_produto = ip.prme_cd_produto ")
		.append("               AND cb.cobm_tp_codbarra = 'V' and cb.cobm_fl_principal = 'S' ")
		.append("INNER JOIN estrutura_pacote est3 (NOLOCK) ON est3.espa_cd_categoria = pm.espa_cd_categoria ")
		.append("INNER JOIN estrutura_pacote est2 (NOLOCK) ON est2.espa_cd_categoria = est3.espa_cd_categoriap ")
		.append("INNER JOIN estrutura_pacote est1 (NOLOCK) ON est1.espa_cd_categoria = est2.espa_cd_categoriap ")
		.append("LEFT JOIN produto_filial pf (NOLOCK) ON pf.prme_cd_produto = pm.prme_cd_produto ")
		.append("               AND pf.fili_cd_filial = p.polo_cd_polo ")
		.append("LEFT JOIN drgtblipsitemprepedsiac ipp (NOLOCK) ON ipp.itpd_cd_item_pedido = ip.itpd_cd_item_pedido ")
		.append("               AND ipp.ipsidcativo = :ativo ")
		.append("LEFT JOIN drgtblppsprepedsiac pp (NOLOCK) on ipp.ppsseq = pp.ppsseq and pp.ppsidcativo = :ativo ")
		.append("LEFT JOIN prod_mestre_ps_val v WITH (NOLOCK) ON v.prme_cd_produto = ip.prme_cd_produto ")
		.append("               AND v.pmpv_dt_fimval IS NULL ")
		.append("LEFT JOIN tp_lista_psicotrop l WITH (NOLOCK) ON v.tplp_sq_tplistasps = l.tplp_sq_tplistasps   ")
		.append("LEFT OUTER JOIN ( ")
		.append("               SELECT COIN_TX_VALOR AS VALORANBITIOTICO FROM COSMOS_INI (NOLOCK)  ")
		.append("               WHERE coin_tx_aplicacao = 'CSMPSICOTROPICO' AND coin_tx_sessao = 'EXPORTACAO_PRC' ")
		.append("       AND COIN_TX_ENTRADA = 'LISTA_ANTIB'")
		.append(") AS ANTIBIOTICOS ON l.tplp_sq_tplistasps = valoranbitiotico ")
		.append("LEFT JOIN drgtblivcitemvencimcurto (NOLOCK) vc ON ip.itpd_cd_item_pedido = vc.itpd_cd_item_pedido ")
		.append("WHERE p.pedi_nr_pedido = :numeroPedido ")
		.append("ORDER BY  est1.espa_ds_categoria, prme_tx_descricao1 ");

		List<Object[]> listResult = em.createNativeQuery(sql.toString())
			.setParameter(NUMERO_PEDIDO, numeroPedido)
			.setParameter("ativo", "A")
			.getResultList();

		if(listResult.isEmpty()){
			throw new EntidadeNaoEncontradaException(Constantes.ITENS_PEDIDO_NAO_ENCONTRADO);
		}

		return listResult.stream()
			.map(item -> {

				if ("S".equals(String.valueOf(item[12])))
					relatorioPedidoSeparacaoDTO.setMedicamentoControlado(true);

				RelatorioProdutoSeparacaoDTO itemDTO = new RelatorioProdutoSeparacaoDTO();
				itemDTO.setCodigo(item[0].toString());
				itemDTO.setCodBarras(item[1].toString());
				itemDTO.setDescricao(item[2].toString());
				itemDTO.setQuantidade(item[3].toString());
				itemDTO.setEstoque(String.format("%.0f", item[4]));
				itemDTO.setSecao(StringUtils.capitalize(item[5].toString().toLowerCase()));
				itemDTO.setProdutoControlado(String.valueOf(item[6]));
				itemDTO.setProdutoAntibiotico(String.valueOf(item[7]));
				itemDTO.setProdutoFarmaciaPopular(item[8].toString());
				itemDTO.setNumPrePedido(item[9].toString());
				itemDTO.setCodigoProduto(item[10] != null ? Integer.valueOf(item[10].toString()) : null);
				itemDTO.setDataValidadePedidoVencCurto(item[11] != null ? ((Date) item[11]) : null);
				if (itemDTO.getDataValidadePedidoVencCurto() != null) {
					itemDTO.setDataValidadePedidoVencCurtoFormat(
						dateFormatterVencCurto.format(itemDTO.getDataValidadePedidoVencCurto())
					);
				}

				itemDTO.setProdutoGeladeira(String.valueOf(item[13]));
				itemDTO.setDescricaoLonga(item[14].toString().toLowerCase());

				itemDTO.definirPropriedadesEspeciaisProduto();
				return itemDTO;
			})
			.collect(Collectors.toList());
	}

	/**
	 * Método obtem AS informações para montar o relatório de impressão do pedido.
	 *
	 * @param numeroPedido
	 * @return RelatorioTermoCompromissoDTO.
	 */
	public RelatorioPedidoSeparacaoDTO obterPedidoParaImpressao(Long numeroPedido) {

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT DISTINCT COALESCE(p.pedi_nr_pedido, '') AS numPedido, "); //0
		sql.append("    COALESCE(c.CLNT_DS_NOME,'') AS nomeCliente, "); //1
		sql.append("    COALESCE(ent.TRLDESTIPORETIRADA,'') AS tipoRetirada, "); //2
		sql.append("    COALESCE(f.fili_nm_fantasia,'') AS filial, "); //3
		sql.append("    p.PEDI_VL_TOTAL_ITENS_PEDIDO AS valorProdutos, "); //4
		sql.append("    COALESCE(f.fili_cd_filial,0) AS codigoFilial, "); //5
		sql.append("    f.ESTA_SG_UF AS uf, "); //6
		sql.append("    cl.NOME_LOCAL AS nome_local, "); //7
		sql.append("    COALESCE((SELECT TOP 1 u.USUA_NM_USUARIO as USUA_NM_USUARIO FROM SEPARACAO_PEDIDO sp (nolock) ");
		sql.append("            INNER JOIN USUARIO u (nolock) on sp.USUA_CD_USUARIO = u.USUA_CD_USUARIO ");
		sql.append("            WHERE sp.PEDI_NR_PEDIDO = :numeroPedido ");
		sql.append("            ORDER BY sp.SPPD_CD_SEPARACAO_PEDIDO DESC),'') as usuario_separacao, "); // 8
		sql.append("    COALESCE(p.pedi_nr_ecom_cliente,'') as pedi_nr_ecom_cliente,  ");  //9
		sql.append("    COALESCE(ent.trlseq,'') as cod_tipo_retirada, ");//10
		sql.append("    COALESCE(prl.PRLCODABERTARMARIO,'') as codBox, ");//11
		sql.append("    COALESCE(filialOrigem.fili_nm_fantasia,'') as filialAraujoTem, "); //12
		sql.append("    COALESCE(filialOrigem.fili_cd_filial,'') as idFilialAraujoTem, "); //13
		sql.append("    p.PEDI_TP_PEDIDO as tipoPedido, "); // 14
		sql.append("    COALESCE(p.PEDI_FL_SUPERVENDEDOR,''), "); //15
		sql.append("    COALESCE(p.TPFR_CD_TIPO_FRETE,'') as TPFR_CD_TIPO_FRETE, "); //16
		sql.append("    COALESCE(pd.pgdn_vl_troco,'0.00') as pgdn_vl_troco, "); //17
		sql.append("    COALESCE(p.pedi_vl_total_pedido,'0.00') as valor_total_pedido "); //18
		sql.append(" FROM pedido p (nolock) ");
		sql.append("    INNER JOIN cliente c (nolock)  ON c.CLNT_CD_CLIENTE = p.CLNT_CD_CLIENTE ");
		sql.append("    LEFT JOIN  DRGTBLTRLTIPORETLOJA ent  (nolock) ON ent.TRLSEQ = p.TRLSEQ ");
		sql.append("    INNER JOIN  filial f (nolock)  ON p.polo_cd_polo = f.fili_cd_filial ");
		sql.append("    INNER JOIN cep_loc cl (nolock) ON f.chave_local = cl.chave_local ");
		sql.append("    LEFT JOIN DRGTBLPRLPEDIDORETLOJA prl (nolock) ON p.pedi_nr_pedido = prl.PEDI_NR_PEDIDO ");
		sql.append("    LEFT JOIN filial filialOrigem (nolock) ON p.fili_cd_filial_araujo_tem = filialOrigem.fili_cd_filial ");
		sql.append("    left join MODALIDADE_PAGAMENTO mp on mp.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO");
		sql.append("    left join PAGAMENTO_DINHEIRO pd on pd.MDPG_CD_MODALIDADE_PAGAMENTO = mp.MDPG_CD_MODALIDADE_PAGAMENTO ");
		sql.append(" WHERE p.pedi_nr_pedido = :numeroPedido ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter(NUMERO_PEDIDO, numeroPedido);

		Object[] result = null;
		try {
			result = (Object[]) query.getSingleResult();
		} catch (NoResultException e) {
			throw new EntidadeNaoEncontradaException(Constantes.PEDIDO_NAO_ENCONTRADO);
		}

		RelatorioPedidoSeparacaoDTO relatorioPedidoSeparacaoDTO = new RelatorioPedidoSeparacaoDTO();

		if (result != null) {
			relatorioPedidoSeparacaoDTO.setNumPedido(result[0].toString());
			relatorioPedidoSeparacaoDTO.setNomeCliente(result[1].toString());
			relatorioPedidoSeparacaoDTO.setTipoRetirada(result[2].toString().toUpperCase());
			relatorioPedidoSeparacaoDTO.setFilial(result[3].toString());
			relatorioPedidoSeparacaoDTO.setCodFilial((Integer) result[5]);
			relatorioPedidoSeparacaoDTO.setEmitidoEm(EMITIDO_EM + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
			if (StringUtils.isNotBlank(relatorioPedidoSeparacaoDTO.getNumPedido())) {
				String numPrateleira = relatorioPedidoSeparacaoDTO.getNumPedido();
				relatorioPedidoSeparacaoDTO.setNumPrateleira(numPrateleira.substring(numPrateleira.length() - 1));
			}
			relatorioPedidoSeparacaoDTO.setNomeVendedor(result[8].toString().split(" ")[0]);
			relatorioPedidoSeparacaoDTO.setIdPedidoVtex(result[9].toString());
			relatorioPedidoSeparacaoDTO.setCodTipoRetirada(result[10].toString());
			relatorioPedidoSeparacaoDTO.setCodBox(result[11].toString());
			relatorioPedidoSeparacaoDTO.setFilialOrigemAraujoTem(result[12].toString());
			relatorioPedidoSeparacaoDTO.setPedidoAraujoTem(StringUtils.isNotBlank(relatorioPedidoSeparacaoDTO.getFilialOrigemAraujoTem()));
			relatorioPedidoSeparacaoDTO.setIdFilialOrigemAraujoTem(Integer.valueOf(result[13].toString()));
			relatorioPedidoSeparacaoDTO.setCanalVenda(result[14] == null ? null : TipoPedidoEnum.buscarPorChave(result[14].toString()).getValor());
			relatorioPedidoSeparacaoDTO.setSuperVendedor("S".equals(String.valueOf(result[15])));
			relatorioPedidoSeparacaoDTO.setCodigoPedidoParceiro(result[9].toString());
            relatorioPedidoSeparacaoDTO.setPedidoQuatroPontoZero(result[16].toString().equals("1"));
			relatorioPedidoSeparacaoDTO.setTroco(result[17].toString());
			relatorioPedidoSeparacaoDTO.setValorTotalPedido(result[18].toString());
		}

		return relatorioPedidoSeparacaoDTO;
	}

	/**
	 * Método obtem AS informações para montar o relatório do termo de entrega.
	 *
	 * @param numeroPedido
	 * @return RelatorioTermoCompromissoDTO.
	 */
	public RelatorioTermoCompromissoDTO obterRelatorioTermoEntrega(Long numeroPedido) {
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT COALESCE(p.pedi_nr_pedido,'') AS numPedido, ");
		sql.append("    COALESCE(CLNT_DS_NOME,'') AS nomeCliente,  ");
		sql.append("    COALESCE(p.polo_cd_polo,'0') AS codigoFilial,  ");
		sql.append("    COALESCE(c.clnt_tn_cpf_cnpj,'') as cpfCliente, ");
		sql.append("    COALESCE(f.fili_nm_fantasia,'') AS nomeFilial, ");
		sql.append("    COALESCE(p.pedi_nr_ecom_cliente,'') AS numPedidoVtex, ");
		sql.append("    filialOrigem.fili_nm_fantasia as filialAraujoTem, ");
		sql.append("    p.PEDI_TP_PEDIDO as tipoPedido ");
		sql.append(" FROM pedido p (nolock)  ");
		sql.append("    INNER JOIN filial f (nolock) ON p.polo_cd_polo = f.fili_cd_filial ");
		sql.append("    INNER JOIN  cliente c (nolock) ON c.CLNT_CD_CLIENTE = p.CLNT_CD_CLIENTE ");
		sql.append("    LEFT JOIN filial filialOrigem (nolock) ON p.fili_cd_filial_araujo_tem = filialOrigem.fili_cd_filial ");
		sql.append(" where p.pedi_nr_pedido = :numeroPedido ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter(NUMERO_PEDIDO, numeroPedido);

		Object[] result = (Object[]) query.getSingleResult();

		RelatorioTermoCompromissoDTO relatorioTermoEntregaDTO = new RelatorioTermoCompromissoDTO();

		if (result != null) {
			relatorioTermoEntregaDTO.setNumPedido(result[0].toString());
			relatorioTermoEntregaDTO.setNomeCliente(result[1].toString());
			relatorioTermoEntregaDTO.setCodFilial((Integer) result[2]);
			relatorioTermoEntregaDTO.setNomeFilial(result[4].toString());
			relatorioTermoEntregaDTO.setNumPedidoVtex(result[5].toString());

			String cpfCliente = result[3].toString();

			try {
				MaskFormatter mask = new MaskFormatter("###.###.###-##");
				mask.setValueContainsLiteralCharacters(false);
				cpfCliente = mask.valueToString(cpfCliente);
			} catch (Exception e) {
				e.printStackTrace();
			}

			relatorioTermoEntregaDTO.setCpfCliente(cpfCliente);
			relatorioTermoEntregaDTO.setFilialOrigemAraujoTem(result[6] == null ? null : String.valueOf(result[6]));
			relatorioTermoEntregaDTO.setPedidoAraujoTem(StringUtils.isNotBlank(relatorioTermoEntregaDTO.getFilialOrigemAraujoTem()));
			relatorioTermoEntregaDTO.setCanalVenda(result[7] == null ? null : TipoPedidoEnum.buscarPorChave(result[7].toString()).getValor());
		}

		return relatorioTermoEntregaDTO;
	}

	/**
	 * Método que consulta o numero da autorização PBM de um ItemPedido.
	 *
	 * @param codigo
	 * @return número da autorização.
	 */
	@SuppressWarnings("unchecked")
	public String obterNumeroAutorizacaoPBM(Integer codigo) {
		StringBuilder sql = new StringBuilder();

		sql.append(" select IPPNUMAUTORIZPBM ");
		sql.append("     from DRGTBLIPPITEMPEDIDOPBM ipbm (nolock)  ");
		sql.append("     where ipbm.ITPD_CD_ITEM_PEDIDO = :codigoItemPedido ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter(CODIGO_ITEM_PEDIDO, codigo);
		List<String> result = query.getResultList();

		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	/**
	 * Método que consulta os itens do pedido durante a separação
	 *
	 * @param numeroPedido
	 * @return retorna list com os itens do pedido.
	 */
	@SuppressWarnings("unchecked")
	public List<ItemPedido> obterItensPedidoParaSeparacao(Long numeroPedido) {
		StringBuilder sql = new StringBuilder();

		sql.append(" select ip.itpd_cd_item_pedido, ip.xxxx_dh_alt, ip.itpd_nr_quantidade_pedida, ");
		sql.append(" ip.itpd_nr_quantidade_separada, ");
		sql.append(" ip.itpd_nr_quantidade_negociada, ");
		sql.append(" ip.itpd_nr_quantidade_negociada_recebida, ");
		sql.append(" ip.itpd_nr_quantidade_registrada, ");
		sql.append(" ip.itpd_nr_quantidade_devolvida, ");
		sql.append(" ip.itpd_fl_produto_controlado, ");
		sql.append(" ip.itpd_fl_item_registrado ");
		sql.append("     from ITEM_PEDIDO ip (nolock)  ");
		sql.append("     where ip.pedi_nr_pedido = :numeroPedido  ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter(NUMERO_PEDIDO, numeroPedido);
		List<Object[]> listResult = query.getResultList();
		List<ItemPedido> result = new ArrayList<>();

		if (listResult != null && !listResult.isEmpty()) {
			listResult.forEach(item -> {
				ItemPedido itemPedido = new ItemPedido();
				itemPedido.setCodigo((Integer) item[0]);
				itemPedido.setUltimaAlteracao((Date) item[1]);
				itemPedido.setQuantidadePedida((Integer) item[2]);
				itemPedido.setQuantidadeSeparada((Integer) item[3]);
				itemPedido.setQuantidadeNegociada((Integer) item[4]);
				itemPedido.setQuantidadeNegociadaRecebida((Integer) item[5]);
				itemPedido.setQuantidadeRegistrada((Integer) item[6]);
				itemPedido.setQuantidadeDevolvida((Integer) item[7]);
				itemPedido.setProdutoControlado(SimNaoEnum.valueOf(item[8].toString()));
				itemPedido.setItemRegistrado(SimNaoEnum.valueOf(item[9].toString()));
				result.add(itemPedido);
			});
		}
		return result;
	}

	@Override
	public void atualizarItemPedido(ItemPedido itemPedido) {
		em.merge(itemPedido);
	}

	@Override
	public void ajustarPedidoParaDevolucaoTotal(Long numeroPedido) {
		StringBuilder sql = new StringBuilder();

		sql.append(
				" UPDATE item_pedido set ITPD_NR_QUANTIDADE_DEVOLVIDA = ITPD_NR_QUANTIDADE_PEDIDA, ITPD_NR_QUANTIDADE_SEPARADA = ITPD_NR_QUANTIDADE_PEDIDA ")
				.append(" WHERE PEDI_NR_PEDIDO = :numeroPedido ");

		em.createNativeQuery(sql.toString()).setParameter(NUMERO_PEDIDO, numeroPedido).executeUpdate();

		sql = new StringBuilder();
		sql.append(
				" UPDATE ITEM_CUPOM_FISCAL set ITCU_QT_DEVOLVIDA = (select ITPD_NR_QUANTIDADE_PEDIDA from item_pedido where ITPD_CD_ITEM_PEDIDO = ITEM_CUPOM_FISCAL.ITPD_CD_ITEM_PEDIDO) ")
				.append(" WHERE CUFI_CD_CUPOM in (select CUFI_CD_CUPOM from CUPOM_FISCAL where REPE_CD_REGISTRO_PEDIDO in (select REPE_CD_REGISTRO_PEDIDO from REGISTRO_PEDIDO where PEDI_NR_PEDIDO = :numeroPedido )) ");

		em.createNativeQuery(sql.toString()).setParameter(NUMERO_PEDIDO, numeroPedido).executeUpdate();
	}

	public Integer updateItemPedido(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
		.append(" UPDATE item_pedido set ")
		.append("       itpd_nr_quantidade_separada = 0,  ITPD_NR_QUANTIDADE_NEGOCIADA = 0, ")
		.append("       ITPD_NR_QUANTIDADE_NEGOCIADA_RECEBIDA = 0, ITPD_NR_QUANTIDADE_REGISTRADA = 0, ")
		.append("       ITPD_NR_QUANTIDADE_DEVOLVIDA = 0, ITPD_NR_QUANTIDADE_EXPEDIDA = 0, ITPD_FL_ITEM_REGISTRADO = 'N' ")
		.append("       where PEDI_NR_PEDIDO in (:numeroPedido) ");

		return em.createNativeQuery(sql.toString())
			.setParameter(NUMERO_PEDIDO, numeroPedido)
			.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	public List<ProdutoDevolucaoAraujoTemDTO> obterItensPedidoParaDevolucaoAraujoTem(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
		.append("SELECT CONVERT(VARCHAR, pm.prme_cd_produto) + '-' + CONVERT(VARCHAR, pm.prme_nr_dv) AS codigo, ")
		.append("       cb.cobm_cd_barra AS codBarra, ")
		.append("       prme_tx_descricao1 AS descricaoProduto, ")
		.append("       SUM(itpd_nr_quantidade_pedida) AS quantidade, ")
		.append("       pm.prme_cd_produto AS codigoProdutoSemDigito ")
		.append(" FROM item_pedido ip (NOLOCK)  ")
		.append(" INNER JOIN produto_mestre pm (NOLOCK) ON ip.prme_cd_produto = pm.prme_cd_produto ")
		.append(" INNER JOIN cod_barra_mestre cb (NOLOCK) ON cb.prme_cd_produto = ip.prme_cd_produto ")
		.append("               AND cb.cobm_tp_codbarra = 'V' and cb.cobm_fl_principal = 'S' ")
		.append(" WHERE ip.pedi_nr_pedido = :numeroPedido ")
		.append(" GROUP BY pm.prme_cd_produto, pm.prme_nr_dv, prme_tx_descricao1, cb.cobm_cd_barra ")
		.append(" ORDER BY prme_tx_descricao1 ");

		List<Object[]> listResult = em.createNativeQuery(sql.toString())
			.setParameter(NUMERO_PEDIDO, numeroPedido)
			.getResultList();

		return listResult.stream()
			.map(item -> ProdutoDevolucaoAraujoTemDTO.builder()
				.codigoProduto(item[0].toString())
				.codigoBarra(item[1].toString())
				.nomeProduto(item[2].toString())
				.quantidade(Integer.parseInt(item[3].toString()))
				.codigoProdutoSemDigito(Integer.parseInt(item[4].toString()))
				.build()
			).collect(Collectors.toList());
	}

    @SuppressWarnings("unchecked")
    public List<String> obterItensEditadosComandaSeparacao(Long numeroPedido) {
        StringBuilder sql = new StringBuilder();
           sql.append(" SELECT CONVERT(VARCHAR, pm.prme_cd_produto) + '-' + CONVERT(VARCHAR, pm.prme_nr_dv) AS codigo ")
           .append(" FROM DRGTBLMPPMOVPRODUTOPEDID mv (NOLOCK) ")
           .append(" INNER JOIN PRODUTO_MESTRE pm (NOLOCK) on mv.PRME_CD_PRODUTO = pm.PRME_CD_PRODUTO ")
           .append(" WHERE pedi_nr_pedido = :numeroPedido AND MPPQTD < 0 ");

        return em.createNativeQuery(sql.toString())
                 .setParameter(NUMERO_PEDIDO, numeroPedido)
                 .getResultList();
    }
    
	/**
	 * Método que atualiza o preço de um item_pedido que está com preço maior que o da loja.
	 * 
	 * @param codigoItem, novoPreco
	 * @return 
	 */
	public void atualizarPrecoItemPedidoSIAC(Integer codigoItem, Double novoPreco) {
		em.createNativeQuery("UPDATE item_pedido SET itpd_vl_preco_unitario = :preco , xxxx_dh_alt = :data WHERE itpd_cd_item_pedido = :codigoItem ")
		  .setParameter("preco", novoPreco)
		  .setParameter("data", new Date())
		  .setParameter("codigoItem", codigoItem)
		  .executeUpdate();
	}
	
	public void registrarHistoricoAltPreco(ItemPedidoSIACDTO item) {
		
		em.createNativeQuery("INSERT INTO drgtblhaphistaltpreco_hst VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")
		  .setParameter(1, new Date())
		  .setParameter(2, 1)
		  .setParameter(3, 0)
		  .setParameter(4, new Date())
		  .setParameter(5, WebUtils .getHostName())
		  .setParameter(6, "CLIQUE-RETIRE")
		  .setParameter(7,item.getNumeroPedido())
		  .setParameter(8,item.getCodigoItemPedido())
		  .setParameter(9,item.getNovoPreco())
		  .setParameter(10,item.getPrecoAnterior())
		  .setParameter(11,item.getCodigoFilial())
		  .setParameter(12,item.getQuantidadeProduto())
		  .setParameter(13,item.getCodigoProduto())
		  .executeUpdate();
	}
}