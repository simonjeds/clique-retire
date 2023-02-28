package com.clique.retire.repository.drogatel;

import static com.clique.retire.util.Constantes.NUMERO_PEDIDO;
import static com.clique.retire.util.Constantes.PEDIDO_NAO_ENCONTRADO;
import static com.clique.retire.util.Constantes.TIPO_FRETE_ENTREGA_VIA_MOTO_ID;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.clique.retire.dto.CaptacaoLoteDTO;
import com.clique.retire.dto.ItemPedidoDTO;
import com.clique.retire.dto.ItemPedidoRetornoMotociclistaDTO;
import com.clique.retire.dto.ItemPendenteNegociarDTO;
import com.clique.retire.dto.LocalizarPedidoFiltroDTO;
import com.clique.retire.dto.LoteBipadoDTO;
import com.clique.retire.dto.ModalidadePagamentoDTO;
import com.clique.retire.dto.PagamentoComunicacaoDTO;
import com.clique.retire.dto.PagamentoDinheiroDTO;
import com.clique.retire.dto.PedidoDTO;
import com.clique.retire.dto.PedidoDataMetricasDTO;
import com.clique.retire.dto.PedidoEditadoEmailDTO;
import com.clique.retire.dto.PedidoEntregaDTO;
import com.clique.retire.dto.PedidoFaltaDTO;
import com.clique.retire.dto.PedidoNotaFiscalDTO;
import com.clique.retire.dto.PedidoPendente25DiasDTO;
import com.clique.retire.dto.PedidoRetornoMotociclistaDTO;
import com.clique.retire.dto.ProdutoDTO;
import com.clique.retire.dto.ProdutoFaltaDTO;
import com.clique.retire.dto.RelatorioPedidoDevolucaoAraujoTemDTO;
import com.clique.retire.dto.TipoPedidoDTO;
import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPagamentoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.ExpedicaoPedido;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.PrePedidoSiac;
import com.clique.retire.service.cosmos.ImagemService;
import com.clique.retire.wrapper.PageWrapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Repository
@RequiredArgsConstructor
public class PedidoRepositoryImpl implements PedidoRepositoryCustom {

	private static final String ENTREGA_EM_DOMICILIO = "Entrega em domicílio";
	private static final String N_FE_NAO_EMITIDA = "NFe Não Emitida";
	private static final String N_FE_SOLICITADA = "NFe Solicitada";
	private static final String N_FE_EMITIDA = "NFe Emitida";
	private static final String FASES = "fases";
	private static final String FILTRO = "filtro";
	private static final String FILTRO_LIKE = "filtroLike";
	private static final String FROM_PEDIDO = " FROM pedido p (nolock) ";
	private static final String DOCUMENTO_CLIENTE_ALIAS = "documento_cliente";
	private static final String NUMERO_PEDIDO_ALIAS = "numero_pedido";
	private static final Integer PENDENTES = 1;
	private static final Integer TODAS = 3;
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final String SIMPLE_DATE_PATTERN = "dd/MM/yyyy";

	@PersistenceContext
	private EntityManager em;

	private final ImagemService imagemService;

	/**
	 * Método que busca o pedido pelo número do Pedido
	 */
	@Override
	public Pedido buscarPedidoPorCodigoPedido(Long numeroPedido) {
		try {
			String sql = "SELECT p FROM Pedido p JOIN FETCH p.itensPedido ip WHERE p.numeroPedido = :numeroPedido ";
			return em.createQuery(sql, Pedido.class)
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getSingleResult();
		} catch (NoResultException e) {
			throw new BusinessException(PEDIDO_NAO_ENCONTRADO);
		}
	}

	/**
	 * Método que busca os pedidos por uma filial específica e por um filtro.
	 *
	 * <b>filtro</b> - String que é buscada nos campos CPF, nome e código do pedido
	 * <b>filial</b> - Id da filial <b>fases</b> - Lista de fases para filtro dos
	 * pedidos <b>maxResults</b> - Número máximo de resultados
	 */
	@Override
	public PageWrapper<PedidoEntregaDTO> buscarPedidosLojaPorFiltro(LocalizarPedidoFiltroDTO filtro) {
		StringBuilder selectConsultaUm = new StringBuilder()
				.append(" SELECT p.pedi_nr_pedido AS numero_pedido, ")
				.append("  p.pedi_fl_fase AS fase_pedido, ")
				.append("  p.pedi_nr_ecom_cliente AS numero_ecommerce, ")
				.append("  c.clnt_ds_nome AS nome_cliente, ")
				.append("  c.clnt_tn_cpf_cnpj AS documento_cliente, ")
				.append("  f.fili_nm_fantasia AS nome_loja, ")
				.append("  p.polo_cd_polo AS id_loja, ")
				.append("  tp.trldestiporetirada AS tipo_retirada, ")
				.append("  p.tpfr_cd_tipo_frete AS tipo_frete, ")
				.append("  tpp.tppdestippedido AS tipo_pedido, ")
				.append("  '(' + t.nrtl_tn_ddd + ') ' + t.nrtl_tn_numero AS telefone, ")
				.append("  IIF(EXISTS( ")
				.append("    SELECT 1 FROM item_pedido (NOLOCK) ip ")
			.append("    WHERE ip.PEDI_NR_PEDIDO = p.pedi_nr_pedido AND ip.ITPD_FL_PRODUTO_CONTROLADO = 'S' ")
			.append("  ), 'TRUE', 'FALSE') AS is_controlado, ")
			.append("  IIF(p.pedi_fl_fase = '19', 1, 2) AS ordenador, ")
			.append("  IIF(exists (SELECT cufi_nr_cupom ")
			.append("              FROM cupom_fiscal cf (nolock) ")
			.append("              WHERE cf.repe_cd_registro_pedido IN (SELECT repe_cd_registro_pedido ")
			.append("                                                   FROM registro_pedido rp (nolock) ")
			.append("                                                   WHERE rp.pedi_nr_pedido = p.pedi_nr_pedido) ")
			.append("                    AND cf.cufi_nr_cupom is not null ")
			.append("             ) OR nf.nofi_dh_nota IS NOT NULL, 'TRUE', 'FALSE') AS nf_emitida, ")
			.append("  IIF(EXISTS( ")
			.append("    SELECT 1 FROM receita_produto_controlado (NOLOCK) ")
			.append("    WHERE itpd_cd_item_pedido IN ( ")
			.append("      SELECT itpd_cd_item_pedido FROM item_pedido (NOLOCK) ")
			.append("      WHERE pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("    ) AND RCPC_CD_AUTORIZACAO IS NOT NULL ")
			.append("  ), 'TRUE', 'FALSE') AS autorizacao_gerada ");

		StringBuilder joinsConsultaUm = new StringBuilder()
				.append(FROM_PEDIDO)
				.append("INNER JOIN cliente c (NOLOCK) ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
				.append("INNER JOIN filial f (NOLOCK) ON p.polo_cd_polo = f.fili_cd_filial ")
				.append("LEFT JOIN omcgcpdbs..gcptbltpptipopedido tpp (NOLOCK) ON p.pedi_tp_pedido = tpp.tppcodtippedido ")
			.append("LEFT JOIN numero_telefone t (NOLOCK) ON t.nrtl_cd_numero_telefone = p.nrtl_cd_numero_telefone ")
			.append("LEFT JOIN drgtblprgpedrotagerada pr (NOLOCK) ON pr.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("LEFT JOIN drgtbltrltiporetloja tp (NOLOCK) ON p.trlseq = tp.trlseq ")
			.append("LEFT JOIN nota_fiscal_drogatel nf (nolock) ON nf.pedi_nr_pedido = p.pedi_nr_pedido ");

		StringBuilder selectConsultaDois = new StringBuilder()
			.append("SELECT  (")
			.append("    SELECT o.uppcodprepedido FROM lksql01.fljinsdbs.dbo.instblppoorigemprepedido o (NOLOCK) ")
			.append("    WHERE o.ppoidcorigem = 'T' ")
			.append("      AND o.fili_cd_filial = ped.pedcodestaborigem")
			.append("      AND o.ppocodorigem = ped.pedseq ")
			.append("  ) AS numero_pedido, ")
			.append("  fpd.codigo_fase_pedido AS fase_pedido, ")
			.append("  '-' AS numero_ecommerce, ")
			.append("  cli.clinomcliente AS nome_cliente, ")
			.append("  doc.docnumdocumento AS documento_cliente, ")
			.append("  f.fili_nm_fantasia AS nome_loja, ")
			.append("  f.fili_cd_filial AS id_loja, ")
			.append("  tpr.tprdestipretirada AS tipo_retirada, ")
			.append("  15 AS tipo_frete, ")
			.append("  tpp.tppdestippedido AS tipo_pedido, ")
			.append("  '' AS telefone, ")
			.append("  'FALSE' AS is_controlado, ")
			.append("  IIF(fpd.codigo_fase_pedido = '19', 1, 2) AS ordenador, ")
			.append("  'FALSE' AS nf_emitida, ")
			.append("  'FALSE' AS autorizacao_gerada ");

		StringBuilder joinsConsultaDois = new StringBuilder()
				.append("FROM omcgcpdbs..gcptblpedpedido ped (NOLOCK) ")
				.append("INNER JOIN omcgcpdbs..gcptblfpdfasepedido fpd (NOLOCK) ON ped.fpdseq = fpd.fpdseq ")
				.append("INNER JOIN omcgcpdbs..gcptblclicliente cli (NOLOCK) ON ped.cliseq = cli.cliseq ")
				.append("INNER JOIN omcgcpdbs..gcptbltprtiporetirada tpr (NOLOCK) ON ped.tppseq = tpr.tprseq ")
				.append("INNER JOIN filial f (NOLOCK) ON ped.pedcodestaborigem = f.fili_cd_filial ")
				.append("LEFT JOIN omcgcpdbs..gcptbltpptipopedido tpp (NOLOCK) ON ped.tppseq = tpp.tppseq  ")
			.append("  AND TPP.tppcodtippedido <> 'C' ")
			.append("LEFT JOIN omcgcpdbs..gcptblapdassociaped apd (NOLOCK) ON ped.pedseq = apd.pedseq ")
			.append("LEFT JOIN ( ")
			.append("  SELECT TOP 1 cliseq, docnumdocumento FROM omcgcpdbs..gcptbldoccliente (NOLOCK) ORDER BY HDRDTHHOR DESC ")
			.append(") doc ON cli.cliseq = doc.cliseq ");

		List<String> fases = StringUtils.isNotBlank(filtro.getFase())
			 ? Arrays.stream(filtro.getFase().split(","))
				 .map(String::trim)
				 .collect(Collectors.toList())
			 : Collections.emptyList();

		Map<String, Object> parametros = new HashMap<>();

		StringBuilder whereConsultaUm = new StringBuilder("WHERE p.pedi_fl_operacao_loja = 'S' ");
		StringBuilder whereConsultaDois = new StringBuilder("WHERE apd.pedseq IS NULL ");

		if (BooleanUtils.isTrue(filtro.getTodasLojas())) {
			parametros.put("expedido", FasePedidoEnum.EXPEDIDO.getChave());
			whereConsultaUm.append("AND p.pedi_fl_fase <> :expedido ");
			whereConsultaDois.append("AND fpd.codigo_fase_pedido <> :expedido ");
		} else {
			parametros.put("filial", filtro.getFilial());
			whereConsultaUm.append("AND p.polo_cd_polo = :filial ");
			whereConsultaDois.append("AND ped.pedcodestaborigem = :filial ");
		}

		if (StringUtils.isNotBlank(filtro.getFiltro().trim())) {
			parametros.put(FILTRO_LIKE, StringUtils.lowerCase("%" + filtro.getFiltro().trim().replace("  ", " ") + "%"));
			whereConsultaUm
					.append("AND ( ")
					.append("  LOWER(p.pedi_nr_ecom_cliente) LIKE :filtroLike ");

			if (StringUtils.isNumeric(filtro.getFiltro().trim())) {
				parametros.put("filtroPedido", Long.valueOf(filtro.getFiltro().trim()));
				whereConsultaUm
						.append("  OR p.pedi_nr_pedido = :filtroPedido ")
						.append("  OR pr.rogcod = :filtroPedido ");

				whereConsultaDois
						.append("AND PED.PEDSEQ = ( ")
						.append("  SELECT top 1 o.ppocodorigem FROM lksql01.fljinsdbs.dbo.instblppoorigemprepedido o (NOLOCK) ")
						.append("  WHERE uppcodprepedido = :filtroPedido ")
						.append("    AND o.fili_cd_filial = ped.pedcodestaborigem AND o.ppocodorigem = ped.pedseq ")
						.append(") ");
			} else {
				whereConsultaUm.append("  OR LOWER(replace(c.clnt_ds_nome,'  ',' ')) LIKE :filtroLike ");
				whereConsultaDois
						.append("AND ( ")
						.append("  LOWER(replace(cli.clinomcliente,'  ',' ')) LIKE :filtroLike ");

				String filtroCPF = filtro.getFiltro().replaceAll("\\D+", "").toLowerCase();
				if (StringUtils.isNotBlank(filtroCPF)) {
					parametros.put("filtroCPF", "%" + filtroCPF + "%");
					whereConsultaUm.append("  OR c.clnt_tn_cpf_cnpj LIKE :filtroCPF ");
					whereConsultaDois.append("  OR doc.docnumdocumento LIKE :filtroCPF ");
				}

				whereConsultaDois.append(") ");
			}

			whereConsultaUm.append(") ");
		}

		if (!fases.isEmpty()) {
			parametros.put(FASES, fases);
			whereConsultaUm.append("AND p.pedi_fl_fase IN :fases ");
			whereConsultaDois.append("AND fpd.codigo_fase_pedido IN :fases ");
		}

		if (StringUtils.isNotBlank(filtro.getTipoPedido())) {
			parametros.put("tipoPedido", Integer.valueOf(filtro.getTipoPedido()));
			whereConsultaUm.append("AND tpp.tppseq = :tipoPedido ");
			whereConsultaDois.append("AND tpp.tppseq = :tipoPedido ");
		}

		StringBuilder sql = new StringBuilder()
			.append(selectConsultaUm)
			.append(joinsConsultaUm)
			.append(whereConsultaUm)
			.append("UNION ")
			.append(selectConsultaDois)
			.append(joinsConsultaDois)
			.append(whereConsultaDois)
			.append("ORDER BY ordenador DESC");

		int quantidadePorPagina = 15;
		int pagina = filtro.getPagina() - 1;

		Query query = em.createNativeQuery(sql.toString(), Tuple.class)
				.setFirstResult(pagina * quantidadePorPagina)
				.setMaxResults(quantidadePorPagina);

		parametros.forEach(query::setParameter);
		CompletableFuture<List<Tuple>> threadConsultaPedidos = CompletableFuture.supplyAsync(query::getResultList);

		int totalRegistros = 1;
		if (!parametros.containsKey("filtroPedido")) {
			StringBuilder sqlCount = new StringBuilder()
					.append("SELECT SUM(quantidade) FROM ( ")
					.append("SELECT COUNT(*) AS quantidade ")
					.append(joinsConsultaUm)
					.append(whereConsultaUm)
					.append("UNION ")
					.append("SELECT COUNT(*) AS quantidade ")
					.append(joinsConsultaDois)
					.append(whereConsultaDois)
					.append(") AS result");

			Query queryCount = em.createNativeQuery(sqlCount.toString());
			parametros.forEach(queryCount::setParameter);
			totalRegistros = (int) queryCount.getSingleResult();

		}

		try {
			List<PedidoEntregaDTO> pedidos = threadConsultaPedidos.get().stream().map(tupla -> {
				PedidoEntregaDTO pedido = PedidoEntregaDTO.builder()
						.id(tupla.get(NUMERO_PEDIDO_ALIAS, BigDecimal.class).intValue())
						.fase(FasePedidoEnum.buscarPorChave(tupla.get("fase_pedido", String.class)))
						.codigoVTEX(tupla.get("numero_ecommerce", String.class))
						.nomeCliente(tupla.get("nome_cliente", String.class))
						.cpf(tupla.get(DOCUMENTO_CLIENTE_ALIAS, String.class))
						.nomeFilial(tupla.get("nome_loja", String.class))
					.mesmaFilial(filtro.getFilial().equals(tupla.get("id_loja", Integer.class)))
					.tipoRetirada(tupla.get("tipo_retirada", String.class))
					.tipoPedido(tupla.get("tipo_pedido", String.class))
					.telefone(tupla.get("telefone", String.class))
					.controlado(Boolean.parseBoolean(tupla.get("is_controlado", String.class)))
					.filial(tupla.get("id_loja", Integer.class))
				    .nfEmitida(Boolean.parseBoolean(tupla.get("nf_emitida", String.class)))
				    .autorizacaoGerada(Boolean.parseBoolean(tupla.get("autorizacao_gerada", String.class)))
				    .build();

				if (TIPO_FRETE_ENTREGA_VIA_MOTO_ID.equals(tupla.get("tipo_frete", Integer.class))) {
					pedido.setTipoRetirada(ENTREGA_EM_DOMICILIO);
				}

				return pedido;
			}).collect(Collectors.toList());
			return new PageWrapper<>(pedidos, filtro.getPagina(), quantidadePorPagina, totalRegistros);
		} catch (InterruptedException | ExecutionException e) {
			log.error(e);
			Thread.currentThread().interrupt();
			throw new BusinessException("Ocorreu um erro desconhecido. Contate o administrador do sistema.");
		}
	}

	/**
	 * Método que busca pedidos com notas fiscais
	 *
	 * <b>filtro</b> - String que é buscada nos campos CPF, nome e código do pedido
	 * <b>filial</b> - Id da filial <b>fases</b> - Lista de fases para filtro dos
	 * pedidos <b>maxResults</b> - Número máximo de resultados
	 */
	@Override
	public List<PedidoNotaFiscalDTO> buscarPedidosNotasFiscaisPorFiltro(String filtro, Integer codStatus, Integer filial) {
		StringBuilder sql = 
				new StringBuilder("SELECT p.pedi_nr_pedido as numeroPedido, ") 
						  .append("       p.pedi_nr_ecom_cliente as codigoVetex, ") 
						  .append("       nf.NOFI_CH_ACESSO as chave_nf,  ")
						  .append("       u.usua_nm_usuario as nome_usuario, ") 
						  .append("       p.xxxx_dh_alt as ultima_alteracao,  ")
						  .append("       p.pedi_fl_fase as fase,  ")
						  .append("       nf.NOFI_NR_NOTA as numero_nota, ") 
						  .append("       c.clnt_ds_nome as nome_cliente,  ")
						  .append("       ip.PEDI_TX_ERRO_INTEGRACAO as erro ")
						  .append(FROM_PEDIDO)
						  .append("INNER JOIN cliente c (nolock) on p.CLNT_CD_CLIENTE = c.CLNT_CD_CLIENTE ")  
						  .append("INNER JOIN NOTA_FISCAL_DROGATEL nf (nolock) on nf.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO ") 
						  .append("INNER JOIN usuario u (nolock) on u.usua_cd_usuario = (SELECT rp.usua_cd_responsavel FROM REGISTRO_PEDIDO rp WHERE rp.REPE_CD_REGISTRO_PEDIDO = p.REPE_CD_REGISTRO_PEDIDO) ")
						  .append("LEFT JOIN DRGTBLCFPCONTRFASINTPED ip (nolock) on ip.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO ")
						  .append("WHERE p.pedi_fl_fase ");

		Map<String, Object> parametros = new HashMap<>();
		if (codStatus.equals(TODAS)) {
			sql.append(" IN (").append(FasePedidoEnum.EM_REGISTRO.getChave()).append(",")
							   .append(FasePedidoEnum.AGUARDANDO_EXPEDICAO.getChave()).append(",")
							   .append(FasePedidoEnum.AGUARDANDO_REGISTRO.getChave()).append(")") ;
		} else {
			sql.append(" = :fase ");
			parametros.put("fase", codStatus.equals(PENDENTES) ? FasePedidoEnum.EM_REGISTRO.getChave() : FasePedidoEnum.AGUARDANDO_EXPEDICAO.getChave());
		}

		sql.append(" AND p.POLO_CD_POLO  = :filial AND p.PEDI_FL_OPERACAO_LOJA = 'S' ");
		
		if (!StringUtils.isEmpty(filtro)) {
			sql.append(" AND ( ");
			sql.append("   CONVERT(VARCHAR,p.pedi_nr_pedido) LIKE :filtro ");
			sql.append("   OR LOWER(p.pedi_nr_pedido_ecommerce) LIKE :filtro ");
			sql.append("   OR c.clnt_tn_cpf_cnpj LIKE :filtroCPF ");
			sql.append("   OR LOWER(c.clnt_ds_nome) LIKE :filtro ");
			sql.append(" )");
			parametros.put(FILTRO, "%" + filtro.toLowerCase() + "%");
			if(!filtro.replaceAll("\\D+", "").equals("")) 
				parametros.put("filtroCPF", "%" + filtro.replaceAll("\\D+", "").toLowerCase() + "%");
			else
				parametros.put("filtroCPF", "%" + filtro.toLowerCase() + "%");
		}
		
		@SuppressWarnings("unchecked")
		TypedQuery<Tuple> query = (TypedQuery<Tuple>) em.createNativeQuery(sql.toString(), Tuple.class)
														.setParameter("filial", filial);

		parametros.forEach(query::setParameter);

		return query.getResultList().stream().map(pedido -> {
			String fase = pedido.get("fase", String.class);
			Integer numeroPedido = pedido.get(NUMERO_PEDIDO, Integer.class);
			
			PedidoNotaFiscalDTO pedidoDTO = new PedidoNotaFiscalDTO();
            pedidoDTO.setCodigo(numeroPedido);
            pedidoDTO.setCodEcommerce(pedido.get("codigoVetex", String.class));
            pedidoDTO.setNomeCliente(pedido.get("nome_cliente", String.class));
            pedidoDTO.setChaveNota(pedido.get("chave_nf", String.class));
            pedidoDTO.setNomeResponsavel(pedido.get("nome_usuario", String.class));
            pedidoDTO.setUltimaAtualizacao(pedido.get("ultima_alteracao", Date.class));
            pedidoDTO.setFasePedido(FasePedidoEnum.buscarPorChave(fase));

			if (FasePedidoEnum.AGUARDANDO_REGISTRO.getChave().equals(fase)) {
				pedidoDTO.setSituacao(N_FE_NAO_EMITIDA);
				pedidoDTO.setMensagem(pedido.get("erro",String.class));
			} else if (FasePedidoEnum.EM_REGISTRO.getChave().equals(fase)) {
				pedidoDTO.setSituacao(N_FE_SOLICITADA);
				pedidoDTO.setMensagem("Solicitação emissão da ordem de venda ao SAP");
			} else if (FasePedidoEnum.AGUARDANDO_EXPEDICAO.getChave().equals(fase)) {
				pedidoDTO.setSituacao(N_FE_EMITIDA + " - Nº " + pedido.get("numero_nota"));
				pedidoDTO.setMensagem("Processado com sucesso");
			}

			return pedidoDTO;
		}).collect(Collectors.toList());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PedidoEntregaDTO> buscarPedidoParaEntrega(String filtro, String numeroPedidoDrogatel, boolean isParceiro) {
		StringBuilder sql = new StringBuilder()
			.append("SELECT DISTINCT ")
			.append("   p.pedi_nr_pedido AS numero_pedido, ")
			.append("   p.pedi_fl_fase AS fase,  ")
			.append("   p.pedi_nr_ecom_cliente AS numero_ecommerce, ")
			.append("   c.clnt_ds_nome AS nome_cliente, ")
			.append("   c.clnt_tn_cpf_cnpj AS documento_cliente, ")
			.append("   tel.nrtl_tn_ddd AS ddd_telefone_cliente, ")
			.append("   tel.nrtl_tn_numero AS numero_telefone_cliente, ")
			.append("   f.fili_nm_fantasia AS nome_filial,")
			.append("   f.fili_cd_filial AS codigo_filial, ")
			.append("   ip.itpd_fl_produto_controlado AS is_controlado, ")
			.append("   ip.itpd_fl_produto_antibiotico AS is_antibiotico, ")
			.append("   p.pedi_fl_supervendedor AS is_super_vendedor, ")
			.append("   tp.trldestiporetirada AS tipo_retirada, ")
			.append("   p.tpfr_cd_tipo_frete AS tipo_frete, ")
			.append("   IIF(exists (SELECT cufi_nr_cupom ")
			.append("               FROM cupom_fiscal cf (nolock) ")
			.append("               WHERE cf.repe_cd_registro_pedido IN (SELECT repe_cd_registro_pedido ")
			.append("                                                    FROM registro_pedido rp (nolock) ")
			.append("                                                    WHERE rp.pedi_nr_pedido = p.pedi_nr_pedido) ")
			.append("                     AND cf.cufi_nr_cupom is not null ")
			.append("              ) OR nf.nofi_dh_nota IS NOT NULL, 'TRUE', 'FALSE') AS nf_emitida ")
			.append(FROM_PEDIDO)
			.append("INNER JOIN cliente c (NOLOCK) ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
			.append("INNER JOIN item_pedido ip (NOLOCK) ON p.pedi_nr_pedido = ip.pedi_nr_pedido ")
			.append("INNER JOIN filial f (NOLOCK) ON p.polo_cd_polo = f.fili_cd_filial ")
			.append("LEFT JOIN drgtbltrltiporetloja tp (NOLOCK) ON p.trlseq = tp.trlseq ")
			.append("LEFT JOIN drgtblprgpedrotagerada pr (NOLOCK) ON pr.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("LEFT JOIN numero_telefone tel (NOLOCK) ON tel.nrtl_cd_numero_telefone = p.nrtl_cd_numero_telefone ")
			.append("LEFT JOIN nota_fiscal_drogatel nf (NOLOCK) ON nf.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("WHERE p.pedi_fl_fase NOT IN :fases AND p.pedi_fl_operacao_loja = 'S' ");

		Map<String, Object> parametros = new HashMap<>();
		filtro = Objects.isNull(filtro) ? "" : filtro.trim().replace("  ", " ");
		if (StringUtils.isNotBlank(numeroPedidoDrogatel)) {
			sql.append("AND p.pedi_nr_pedido = :numeroPedidoDrogatel ");
			parametros.put("numeroPedidoDrogatel", numeroPedidoDrogatel);
		} else if (!StringUtils.isNumeric(filtro)) {
			sql.append(" AND (replace(c.clnt_ds_nome,'  ',' ') LIKE :filtroLike OR LOWER(p.pedi_nr_ecom_cliente) LIKE :filtro) ");
			parametros.put(FILTRO_LIKE, "%" + filtro + "%");
			parametros.put(FILTRO, StringUtils.lowerCase(filtro));
		} else if (filtro.length() == 11 || filtro.length() == 14) {
			sql.append(" AND c.clnt_tn_cpf_cnpj = :documento_cliente ");
			parametros.put(DOCUMENTO_CLIENTE_ALIAS, isParceiro ? "@" : filtro);
		} else {
			sql
				.append(" AND ( ")
				.append("   p.pedi_nr_pedido_ecommerce = :filtro ")
				.append("   OR pr.rogcod = :filtro ")
				.append("   OR ( ")
				.append("     p.pedi_nr_pedido = :filtro ")
				.append("     AND ( p.pedi_tp_pedido = 'D' ")
				.append("           OR ( p.pedi_tp_pedido = 'A' AND p.pedi_fl_supervendedor = 'N' ) ")
				.append("           OR p.tpfr_cd_tipo_frete = :tipoFreteEntregaViaMoto ")
				.append("     ) ")
				.append("   ) ")
				.append("   OR ( LOWER(p.pedi_nr_ecom_cliente) LIKE :filtroLike AND p.pedi_tp_pedido IN ('F', 'E') ) ")
				.append(" ) ");

			parametros.put(FILTRO, filtro);
			parametros.put(FILTRO_LIKE, "%" + filtro + "%");
			parametros.put("tipoFreteEntregaViaMoto", TIPO_FRETE_ENTREGA_VIA_MOTO_ID);
		}

		sql.append("AND p.pedi_fl_marketplace = :parceiro ");

		List<String> fases = Arrays.asList(
			FasePedidoEnum.EXPEDIDO.getChave(),
			FasePedidoEnum.ENTREGUE.getChave(),
			FasePedidoEnum.CANCELADO.getChave(),
			FasePedidoEnum.DEVOLUCAO_TOTAL.getChave()
		);

		Query query = em.createNativeQuery(sql.toString(), Tuple.class)
				.setParameter(FASES, fases)
				.setParameter("parceiro", SimNaoEnum.getValueByBoolean(isParceiro).getDescricao());

		parametros.forEach(query::setParameter);

		List<PedidoEntregaDTO> pedidosNaoAgrupados = ((List<Tuple>) query.getResultList()).stream()
			.map(tupla ->  {
				String dddTelefoneCliente = tupla.get("ddd_telefone_cliente", String.class);
				String numeroTelefoneCliente = tupla.get("numero_telefone_cliente", String.class);

				return PedidoEntregaDTO.builder()
					.id(tupla.get(NUMERO_PEDIDO_ALIAS, Integer.class))
					.fase(FasePedidoEnum.buscarPorChave(tupla.get("fase", String.class)))
					.codigoVTEX(StringUtils.defaultString(tupla.get("numero_ecommerce", String.class), "-"))
					.nomeCliente(tupla.get("nome_cliente", String.class))
					.cpf(tupla.get(DOCUMENTO_CLIENTE_ALIAS, String.class))
					.telefone(String.format("(%s) %s", dddTelefoneCliente, numeroTelefoneCliente))
					.nomeFilial(tupla.get("nome_filial", String.class))
					.filial(tupla.get("codigo_filial", Integer.class))
					.controlado("S".equalsIgnoreCase(String.valueOf(tupla.get("is_controlado", Character.class))))
					.antibiotico("S".equalsIgnoreCase(tupla.get("is_antibiotico", String.class)))
					.superVendedor("S".equalsIgnoreCase(String.valueOf(tupla.get("is_super_vendedor", Character.class))))
					.tipoRetirada(tupla.get("tipo_retirada", String.class))
					.quatroPontoZero(tupla.get("tipo_frete", Integer.class) == 1)
					.nfEmitida(Boolean.parseBoolean(tupla.get("nf_emitida", String.class)))
					.build();
			}).collect(Collectors.toList());

		return pedidosNaoAgrupados.stream()
			.collect(Collectors.groupingBy(PedidoEntregaDTO::getId))
			.values().stream()
			.map(pedidos -> {
				boolean isControlado = pedidos.stream().anyMatch(PedidoEntregaDTO::isControlado);
				boolean isAntibiotico = pedidos.stream().anyMatch(PedidoEntregaDTO::isAntibiotico);

				PedidoEntregaDTO pedidoCompleto = pedidos.get(0);
				pedidoCompleto.setControlado(isControlado);
				pedidoCompleto.setAntibiotico(isAntibiotico);
				return pedidoCompleto;
			})
			.sorted(Comparator.comparing(PedidoEntregaDTO::getId).reversed())
			.collect(Collectors.toList());
	}

	/**
	 * Método para obter o pedido atendido por usuario.
	 *
	 * @param codigoUsuario codigo do usuario
	 * @return numero do pedido
	 */
	public Integer obterPedidoEmSeparacaoPorUsuario(Integer codigoUsuario, Integer codigoLoja) {
		StringBuilder sql = new StringBuilder()
			.append("SELECT TOP 1 p.pedi_nr_pedido ")
			.append(FROM_PEDIDO)
			.append("INNER JOIN separacao_pedido sp (NOLOCK) ON sp.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("LEFT JOIN drgtblpdcpedidocompl compl (NOLOCK) ON compl.pdcnrpedido = p.pedi_nr_pedido ")
			.append("WHERE p.polo_cd_polo = :codigoLoja ")
			.append("  AND pedi_fl_fase = '06' ")
			.append("  AND sp.usua_cd_usuario = :codigoUsuario ")
			.append("  AND pedi_fl_operacao_loja = 'S' ")
			.append("  AND COALESCE(p.pedi_fl_formula, 'N') = 'N' ")
			.append("  AND COALESCE(compl.pdcidcpapafila, 'N') = 'N' ");

		try {
			return (Integer) em.createNativeQuery(sql.toString())
				.setParameter("codigoLoja",codigoLoja)
				.setParameter("codigoUsuario", codigoUsuario)
				.getSingleResult();
		} catch (NoResultException e) {
			log.error("nenhum resultado encontrado na consulta de pedido atendido por usuario");
			return null;
		}
	}

	@Override
	public Pedido buscarPedidoParaRegistrarFalta(Long numeroPedido) {
		try {
			String sql = "SELECT p FROM Pedido p JOIN FETCH p.itensPedido ip WHERE p.numeroPedido = :numeroPedido ORDER BY ip.kit, ip.sequencialKit";
			return em.createQuery(sql, Pedido.class)
					.setParameter(NUMERO_PEDIDO, numeroPedido)
					.getSingleResult();
		} catch (NoResultException e) {
			throw new BusinessException(PEDIDO_NAO_ENCONTRADO);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public PedidoFaltaDTO buscarPedidoParaInicioFalta(Integer numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT ")
				.append("  p.pedi_nr_pedido AS numero_pedido, ")
				.append("  p.pedi_fl_fase AS fase_pedido, ")
				.append("  p.polo_cd_polo AS codigo_filial, ")
				.append("  c.clnt_ds_nome AS nome_cliente, ")
				.append("  pm.prme_cd_produto AS codigo_produto, ")
				.append("  pm.prme_nr_dv AS digito_produto, ")
				.append("  pm.prme_tx_descricao1 AS descricao_produto, ")
				.append("  SUM(ip.itpd_nr_quantidade_pedida) AS quantidade_pedida, ")
				.append("  p.pedi_tp_pedido AS tipo_pedido, ")
				.append("  IIF(ip.itpd_fl_produto_controlado = 'S', 'TRUE', 'FALSE') AS is_controlado, ")
				.append("  p.tpfr_cd_tipo_frete AS tipo_frete ")
				.append(FROM_PEDIDO)
				.append("JOIN item_pedido ip (NOLOCK) ON p.pedi_nr_pedido = ip.pedi_nr_pedido ")
				.append("JOIN produto_mestre pm (NOLOCK) ON ip.prme_cd_produto = pm.prme_cd_produto ")
				.append("JOIN cliente c (NOLOCK) ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
				.append("WHERE ip.pedi_nr_pedido = :numeroPedido ")
				.append("GROUP BY p.pedi_nr_pedido, p.pedi_fl_fase, p.polo_cd_polo, c.clnt_ds_nome, ")
				.append("  pm.prme_cd_produto, pm.prme_tx_descricao1, pm.prme_nr_dv, p.pedi_tp_pedido, ")
				.append("  ip.itpd_fl_produto_controlado, p.tpfr_cd_tipo_frete ");

		List<Tuple> result = em.createNativeQuery(sql.toString(), Tuple.class)
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList();

		if (CollectionUtils.isEmpty(result)) {
			throw new EntidadeNaoEncontradaException(PEDIDO_NAO_ENCONTRADO);
		}

		Tuple dadosPedido = result.iterator().next();

		PedidoFaltaDTO pedido = new PedidoFaltaDTO();
		pedido.setCodigo(dadosPedido.get("numero_pedido", Integer.class).longValue());
		pedido.setFase(FasePedidoEnum.buscarPorChave(dadosPedido.get("fase_pedido", String.class)));
		pedido.setCodigoFilial(dadosPedido.get("codigo_filial", Integer.class));
		pedido.setNomeCliente(dadosPedido.get("nome_cliente", String.class));
		pedido.setTipoPedido(String.valueOf(dadosPedido.get("tipo_pedido", Character.class)));
		pedido.setTipoFrete(dadosPedido.get("tipo_frete", Integer.class));
		List<TipoPagamentoEnum> tiposPagamentoPedido = buscarTiposPagamentoPedido(pedido.getCodigo());
		pedido.setTiposPagamentoEnum(tiposPagamentoPedido);

		List<ProdutoFaltaDTO> produtos = result.stream()
				.map(tupla -> {
					Integer codigoProduto = tupla.get("codigo_produto", Integer.class);
					String descricao = tupla.get("descricao_produto", String.class);
					Integer quantidadePedido = tupla.get("quantidade_pedida", Integer.class);
					Integer digito = tupla.get("digito_produto", BigDecimal.class).intValue();
					boolean isControlado = Boolean.parseBoolean(tupla.get("is_controlado", String.class));
					return new ProdutoFaltaDTO(codigoProduto, descricao, quantidadePedido, digito, isControlado);
				}).collect(Collectors.toList());
		pedido.setProdutos(produtos);

		return pedido;
	}

	public boolean isProdutoExigeReceita(Integer idProduto) {
		StringBuilder sql = new StringBuilder();
        sql.append(" SELECT top 1 CASE WHEN V.PMPV_DT_FIMVAL IS NULL AND V.TPLP_SQ_TPLISTASPS IS NOT NULL THEN 'true' ");
        sql.append("        WHEN VALORANBITIOTICO IS NOT NULL AND V.PMPV_DT_FIMVAL IS NULL  THEN 'true' ELSE 'false' END AS EXIGE_RECEITA ");
        sql.append("    FROM PROD_MESTRE_PS_VAL V WITH (NOLOCK) ");
        sql.append("    LEFT JOIN TP_LISTA_PSICOTROP L WITH (NOLOCK) ON V.TPLP_SQ_TPLISTASPS = L.TPLP_SQ_TPLISTASPS ");
        sql.append("    LEFT OUTER JOIN (SELECT COIN_TX_VALOR AS VALORANBITIOTICO FROM COSMOS_INI (NOLOCK) ");
        sql.append("        WHERE COIN_TX_APLICACAO = 'CSMPSICOTROPICO' AND COIN_TX_SESSAO = 'EXPORTACAO_PRC' ");
        sql.append("        AND COIN_TX_ENTRADA = 'LISTA_ANTIB') AS ANTIBIOTICOS  ON L.TPLP_SQ_TPLISTASPS  = VALORANBITIOTICO  ");
        sql.append(" WHERE V.PRME_CD_PRODUTO = :idProduto ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idProduto", idProduto);
		try {
			Object resultado = query.getSingleResult();
			return Boolean.parseBoolean(resultado.toString());
		} catch (NoResultException e) {
			return false;
		}
	}

	/**
	 * Método para alterar a fase do pedido.
	 *
	 * @param numeroPedido numero do pedido
	 */
	public void alterarFasePedido(Long numeroPedido, FasePedidoEnum novaFase) {
		em.createNativeQuery("UPDATE pedido SET pedi_fl_fase = ?, xxxx_dh_alt = ? WHERE pedi_nr_pedido = ? ")
				.setParameter(1, novaFase.getChave())
				.setParameter(2, new Date())
				.setParameter(3, numeroPedido)
				.executeUpdate();
	}

	/**
	 * Método para atualizar os itens do pedido na separação
	 *
	 * @param numeroPedido numero do pedido
	 *
	 */
	@Transactional
	public void atualizaItensPedidoSeparacao(Integer numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("UPDATE item_pedido SET xxxx_dh_alt = ?, ")
				.append("  itpd_nr_quantidade_separada = itpd_nr_quantidade_pedida - itpd_nr_quantidade_negociada, ")
				.append("  itpd_fl_item_separado = 'S' ")
				.append("WHERE pedi_nr_pedido = ? ");

		em.createNativeQuery(sql.toString())
				.setParameter(1, new Date())
				.setParameter(2, numeroPedido)
				.executeUpdate();
	}

	@Override
	public void atualizarQuantExpedidaDosItensDoPedido(Integer numeroPedido, Date dataAlteracao) {
		StringBuilder sql = new StringBuilder()
				.append("UPDATE item_pedido SET xxxx_dh_alt = :data, ")
				.append(" itpd_nr_quantidade_expedida = itpd_nr_quantidade_pedida ")
				.append("WHERE pedi_nr_pedido = :numeroPedido ");

		em.createNativeQuery(sql.toString())
				.setParameter("data", new Date())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.executeUpdate();
	}

	@Override
	public void atualizarTipoRetiradaEfetiva(Integer numeroPedido) {
		em.createNativeQuery("UPDATE pedido SET trlseqefetiva = trlseq WHERE pedi_nr_pedido = :numeroPedido ")
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.executeUpdate();
	}

	@Override
	public Pedido atualizarPedido(Pedido pedido) {
		return em.merge(pedido);
	}

	/**
	 * Método para verificar se a loja possui pedido atendido.
	 *
	 * @param codigoFilial codigo da loja
	 * @return true se houver pedidos na fase atendido e false caso não haja.
	 */
	public boolean isExistePedidosAgSeparacaoPorFilial(Integer codigoFilial) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT TOP 1 p.pedi_nr_pedido FROM pedido p WITH(NOLOCK) ")
				.append("WHERE p.polo_cd_polo = :codigoFilial ")
				.append(" AND p.pedi_fl_operacao_loja ='S'  ")
				.append(" AND p.pedi_fl_fase = :fase ");

		return !em.createNativeQuery(sql.toString())
				.setParameter("codigoFilial", codigoFilial)
				.setParameter("fase", FasePedidoEnum.ATENDIDO.getChave())
				.getResultList()
				.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PedidoDataMetricasDTO> buscarPedidosParaCalculoMetricas() {

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT p.PEDI_DH_INICIO_ATENDIMENTO, p.PEDI_DH_TERMINO_ATENDIMENTO, ");
		sql.append("    MIN(sp.SPPD_DH_HORARIO_INICIO) as horaInicioSeparacao, HFP.HFPDTHENTFASEATUAL, ");
		sql.append("    max(pm.PDMC_DH_REAL_CHEGADA) as horaTerminoRecebimento, rp.REPE_DH_HORA_TERM_REGISTRO, ");
		sql.append("   p.pedi_nr_pedido, fili_hr_aberdom, FILI_HR_ABERSEG, FILI_HR_ABERTER, FILI_HR_ABERQUA, FILI_HR_ABERQUI, FILI_HR_ABERSEX, ");
		sql.append("   FILI_HR_ABERSAB, FILI_HR_ABERFER, fili_hr_fechdom, FILI_HR_fechSEG, FILI_HR_fechTER, FILI_HR_fechQUA, ");
		sql.append("    FILI_HR_fechQUI, FILI_HR_fechSEX, FILI_HR_fechSAB, FILI_HR_fechFER, f.fili_cd_filial ");
		sql.append(" FROM pedido p (nolock) INNER JOIN FILIAL f (nolock) on p.polo_cd_polo = f.fili_cd_filial ");
		sql.append("    INNER JOIN SEPARACAO_PEDIDO sp (nolock) ON sp.pedi_nr_pedido = p.pedi_nr_pedido  ");
		sql.append("    inner join REGISTRO_PEDIDO rp (nolock) on p.PEDI_NR_PEDIDO = rp.PEDI_NR_PEDIDO ");
		sql.append("    LEFT JOIN DRGTBLHFPHISTFASEPEDIDO_HST HFP (nolock)  ");
		sql.append("        ON HFP.PEDI_NR_PEDIDO = p.pedi_nr_pedido ");
		sql.append("        and hfp.PEDI_FL_FASE_ATUAL='").append(FasePedidoEnum.AGUARDANDO_NEGOCIACAO.getChave()).append("' ");
		sql.append("    inner join ITEM_PEDIDO ip (nolock) ON ip.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO ");
		sql.append("   LEFT JOIN ITEM_PEDIDO_MERCADORIA ipm (nolock) on ip.ITPD_CD_ITEM_PEDIDO=ipm.ITPD_CD_ITEM_PEDIDO ");
		sql.append("   left join PEDIDO_MERCADORIA pm (nolock) on ipm.PDMC_CD_PEDIDO_MERCADORIA= pm.PDMC_CD_PEDIDO_MERCADORIA ");
		sql.append(" WHERE p.pedi_fl_fase IN ('").append(FasePedidoEnum.AGUARDANDO_EXPEDICAO.getChave()).append("') ");
		sql.append("    AND p.PEDI_FL_OPERACAO_LOJA = 'S'");
		sql.append("    AND p.PEDI_DH_INICIO_ATENDIMENTO > :data AND rp.REPE_DH_HORA_TERM_REGISTRO IS NOT NULL ");
		sql.append("   AND p.pedi_nr_pedido NOT IN (SELECT pedi_nr_pedido FROM DRGTBLMCRMETRICAS_HST (nolock) WHERE HDRDTHHOR > :data) ");
		sql.append(" GROUP BY p.pedi_nr_pedido, p.PEDI_DH_INICIO_ATENDIMENTO, p.PEDI_DH_TERMINO_ATENDIMENTO, ");
		sql.append("        HFP.HFPDTHENTFASEATUAL, HFP.HFPCOD, rp.REPE_DH_HORA_TERM_REGISTRO, ");
		sql.append("       p.pedi_nr_pedido, fili_hr_aberdom, FILI_HR_ABERSEG, FILI_HR_ABERTER, FILI_HR_ABERQUA, FILI_HR_ABERQUI, FILI_HR_ABERSEX, ");
		sql.append("       FILI_HR_ABERSAB, FILI_HR_ABERFER, fili_hr_fechdom, FILI_HR_fechSEG, FILI_HR_fechTER, FILI_HR_fechQUA, ");
		sql.append("       FILI_HR_fechQUI, FILI_HR_fechSEX, FILI_HR_fechSAB, FILI_HR_fechFER, f.fili_cd_filial  ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("data", LocalDate.now().minusMonths(1));

		List<Object[]> result = query.getResultList();
		List<PedidoDataMetricasDTO> retorno = new ArrayList<>();

		for (Object[] pedido : result) {
			PedidoDataMetricasDTO dto = new PedidoDataMetricasDTO();
			retorno.add(parseResultadosPedidosMetrica(pedido, dto));
		}
		return retorno;
	}

	private PedidoDataMetricasDTO parseResultadosPedidosMetrica(Object[] pedido, PedidoDataMetricasDTO dto){
		SimpleDateFormat formatador = new SimpleDateFormat(DATE_PATTERN);
		try {
			if (pedido[7] != null) {
				dto.setHorarioAberturaDomingo(formatador.parse(pedido[7].toString()));
			}
			if (pedido[13] != null) {
				dto.setHorarioAberturaSabado(formatador.parse(pedido[13].toString()));
			}

			if (pedido[14] != null) {
				dto.setHorarioAberturaFeriado(formatador.parse(pedido[14].toString()));
			}

			if (pedido[15] != null) {
				dto.setHorarioFechamentoDomingo(formatador.parse(pedido[15].toString()));
			}
			if (pedido[21] != null) {
				dto.setHorarioFechamentoSabado(formatador.parse(pedido[21].toString()));
			}

			if (pedido[22] != null) {
				dto.setHorarioFechamentoFeriado(formatador.parse(pedido[22].toString()));
			}
			if (pedido[3] != null) {
				dto.setDataEntrouNegociacao(formatador.parse(pedido[3].toString()));
			}

			if (pedido[4] != null) {
				dto.setDataRecebeuUltimaTransferencia(formatador.parse(pedido[4].toString()));
			}
			dto.setNumeroPedido((Integer) pedido[6]);
			dto.setFilial((Integer) pedido[23]);
			dto.setHorarioAberturaSegunda(formatador.parse(pedido[8].toString()));
			dto.setHorarioAberturaTerca(formatador.parse(pedido[9].toString()));
			dto.setHorarioAberturaQuarta(formatador.parse(pedido[10].toString()));
			dto.setHorarioAberturaQuinta(formatador.parse(pedido[11].toString()));
			dto.setHorarioAberturaSexta(formatador.parse(pedido[12].toString()));
			dto.setHorarioFechamentoSegunda(formatador.parse(pedido[16].toString()));
			dto.setHorarioFechamentoTerca(formatador.parse(pedido[17].toString()));
			dto.setHorarioFechamentoQuarta(formatador.parse(pedido[18].toString()));
			dto.setHorarioFechamentoQuinta(formatador.parse(pedido[19].toString()));
			dto.setHorarioFechamentoSexta(formatador.parse(pedido[20].toString()));
			dto.setDataInicioIntegracao(formatador.parse(pedido[0].toString()));
			dto.setDataTerminoIntegracao(formatador.parse(pedido[1].toString()));
			dto.setDataInicioSeparacao(formatador.parse(pedido[2].toString()));
			dto.setDataTerminoRegistro(formatador.parse(pedido[5].toString()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dto;
	}

	@Override
	public void atualizarUltimoCodigoRegistro(Integer codigoUltimoRegistro, Integer numeroPedido) {
		String sql = "UPDATE pedido SET repe_cd_registro_pedido = :codigoRegistro WHERE pedi_nr_pedido = :numeroPedido";
		em.createNativeQuery(sql)
				.setParameter("codigoRegistro", codigoUltimoRegistro)
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.executeUpdate();
	}

	/**
	 * Método que verifica se o pedido possui itens aguardando mercadoria.
	 *
	 * @param numeroPedido numero do pedido
	 */
	@SuppressWarnings("unchecked")
	public boolean isPedidoComItensAgMercadoria(Long numeroPedido) {

		StringBuilder sql = new StringBuilder();
		sql.append("select top 1 ip.pedi_nr_pedido from item_pedido ip (nolock) ");
		sql.append("where PEDI_NR_PEDIDO=:numeroPedido  ");
		sql.append("and ITPD_NR_QUANTIDADE_NEGOCIADA_RECEBIDA<ITPD_NR_QUANTIDADE_NEGOCIADA ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(NUMERO_PEDIDO, numeroPedido);

		List<Integer> result = query.getResultList();
		return result != null && !result.isEmpty();
	}

	@Override
	public boolean isPedidoExigeReceita(Long idPedido) {
		StringBuilder sql = new StringBuilder();
        sql.append(" SELECT TOP 1 ip.pedi_nr_pedido ");
        sql.append(" FROM ITEM_PEDIDO ip (nolock) ");
        sql.append("    INNER JOIN PROD_MESTRE_PS_VAL V WITH (NOLOCK) ON V.PRME_CD_PRODUTO = ip.PRME_CD_PRODUTO ");
        sql.append("    LEFT JOIN TP_LISTA_PSICOTROP L WITH (NOLOCK) ON V.TPLP_SQ_TPLISTASPS = L.TPLP_SQ_TPLISTASPS ");
        sql.append("    LEFT OUTER JOIN (SELECT COIN_TX_VALOR AS VALORANBITIOTICO FROM COSMOS_INI WITH (NOLOCK) ");
        sql.append("        WHERE COIN_TX_APLICACAO = 'CSMPSICOTROPICO' AND COIN_TX_SESSAO = 'EXPORTACAO_PRC' ");
        sql.append("        AND COIN_TX_ENTRADA = 'LISTA_ANTIB') AS ANTIBIOTICOS  ON L.TPLP_SQ_TPLISTASPS  = VALORANBITIOTICO  ");
        sql.append(" WHERE ip.pedi_nr_pedido = :idPedido ");
        sql.append("    AND (CASE WHEN V.PMPV_DT_FIMVAL IS NULL AND V.TPLP_SQ_TPLISTASPS IS NOT NULL THEN 'true' ");
        sql.append("        WHEN VALORANBITIOTICO IS NOT NULL AND V.PMPV_DT_FIMVAL IS NULL  THEN 'true' ELSE 'false' END) = 'true' ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idPedido", idPedido);

		return !query.getResultList().isEmpty();
	}

	/**
	 * Método que verifica se o pedido possui itens aguardando mercadoria.
	 *
	 * @param numeroPedido numero do pedido
	 */
	@SuppressWarnings("unchecked")
	public List<ItemPendenteNegociarDTO> obterItensPendentesANegociar(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT ")
				.append("  ip.itpd_cd_item_pedido, ")
				.append("  itpd_nr_quantidade_pedida - (itpd_nr_quantidade_separada + itpd_nr_quantidade_negociada)  ")
				.append("FROM item_pedido ip (NOLOCK) ")
				.append("WHERE pedi_nr_pedido = :numeroPedido  ")
				.append("  AND ")
				.append("    itpd_nr_quantidade_pedida - (itpd_nr_quantidade_separada + itpd_nr_quantidade_negociada)")
				.append("  ) > 0");

		List<Object[]> result = em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList();

		return result.stream().map(pedido -> {
			ItemPendenteNegociarDTO dto = new ItemPendenteNegociarDTO();
			dto.setCodigoItemPedido((Integer) pedido[0]);
			dto.setQuantidadePendente((Integer) pedido[1]);
			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * Método para verificar se o pedido possui produto controlado.
	 *
	 * @param numeroPedido numero do pedido
	 * @return false ou true.
	 */
	public boolean isPedidoBoxSemCodigoAberturaPreenchido(Integer numeroPedido) {

		StringBuilder sql = new StringBuilder();
		sql.append("select  ");
		sql.append("PRLCODABERTARMARIO ");
		sql.append("from pedido p (nolock) ");
		sql.append("left join DRGTBLPRLPEDIDORETLOJA rl (nolock)  on rl.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO ");
		sql.append("where PEDI_FL_OPERACAO_LOJA='S' ");
		sql.append("and TRLSEQ=4 ");
		sql.append("and p.pedi_nr_pedido =:numeroPedido ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter(NUMERO_PEDIDO, numeroPedido);

		@SuppressWarnings("unchecked")
		List<String> result = query.getResultList();
		if (result == null || result.isEmpty()) {
			return false;
		}

		return result.get(0) == null;
	}

	/**
	 * Método para obter o numero do pedido ecommerce cliente.
	 *
	 * @param numeroPedido numero do pedido
	 * @return numero do pedido
	 */
	public String obterPedidoEcommerceCliente(Integer numeroPedido) {

		StringBuilder sql = new StringBuilder();
		sql.append("select  ");
		sql.append("p.PEDI_NR_ECOM_CLIENTE ");
		sql.append("from pedido p (nolock) ");
		sql.append("where p.pedi_nr_pedido =:numeroPedido ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter(NUMERO_PEDIDO, numeroPedido);

		@SuppressWarnings("unchecked")
		List<String> result = query.getResultList();
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	@Override
	public Integer buscarNumeroPedidoRandomico() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TOP 1 p.pedi_nr_pedido ");
		sql.append(" FROM pedido p inner join item_pedido ip ON ip.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO ");
		sql.append(
				" INNER JOIN cod_barra_mestre cb (nolock) ON cb.prme_cd_produto = ip.prme_cd_produto and cb.cobm_tp_codbarra='V' and cb.cobm_fl_principal='S' ");
		sql.append(" WHERE  p.PEDI_NR_PEDIDO > 10000000 and p.PEDI_FL_OPERACAO_LOJA = 'S' ORDER BY NEWID()");

		return (Integer) em.createNativeQuery(sql.toString()).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, String> buscarCodigoBarraPorProduto(List<Integer> listProduto) {

		StringBuilder sbQuery = new StringBuilder();

		sbQuery.append("SELECT DISTINCT ST2.PRME_CD_PRODUTO as codProduto,  ");
		sbQuery.append("    SUBSTRING( ");
		sbQuery.append("    ( ");
		sbQuery.append("        SELECT ';'+ST1.COBM_CD_BARRA  AS [text()] ");
		sbQuery.append("        FROM dbo.COD_BARRA_MESTRE ST1 (nolock) ");
		sbQuery.append("        WHERE ST1.PRME_CD_PRODUTO = ST2.PRME_CD_PRODUTO ");
		sbQuery.append("        ORDER BY ST1.PRME_CD_PRODUTO ");
		sbQuery.append("    FOR XML PATH ('') ");
		sbQuery.append("    ), 2, 1000) [codBarras] ");
		sbQuery.append(" FROM dbo.COD_BARRA_MESTRE ST2 (nolock) ");
		sbQuery.append(" WHERE ST2.PRME_CD_PRODUTO IN (:listProduto) ");

		Query query = em.createNativeQuery(sbQuery.toString());
		query.setParameter("listProduto", listProduto);

		Map<Integer, String> mapCodBarras = new HashMap<>();

		List<Object[]> result = query.getResultList();

		if (result != null && !result.isEmpty()) {
			for (Object[] obj : result) {
				mapCodBarras.put((Integer) obj[0], (String) obj[1]);
			}
		}

		return mapCodBarras;
	}

	@Override
	public Object[] buscarTipoEntrega(Integer codigoPedido) {

		try {

			StringBuilder sbQuery = new StringBuilder();

			sbQuery.append("SELECT tf.TPFR_FL_TIPO_ENTREGA, p.PEDI_TP_PEDIDO ");
			sbQuery.append(" FROM PEDIDO P ");
			sbQuery.append(" INNER JOIN TIPO_FRETE_DROGATEL tf ON p.TPFR_CD_TIPO_FRETE = tf.TPFR_CD_TIPO_FRETE ");
			sbQuery.append(" WHERE PEDI_NR_PEDIDO = :numeroPedido ");

			Query query = em.createNativeQuery(sbQuery.toString());
			query.setParameter(NUMERO_PEDIDO, codigoPedido);

			return (Object[]) query.getSingleResult();

		} catch (Exception e) {
			log.error(String.format("Tipo de entrega indefinido para o pedido %s.", codigoPedido));
			return new Object[0];
		}
	}

	@Override
	public ExpedicaoPedido buscarExpedicaoPedidoPorNumeroPedido(Long codPedido) {
		try {
			StringBuilder sbQuery = new StringBuilder()
					.append(" SELECT ep FROM ExpedicaoPedido ep ")
					.append(" WHERE ep.pedido.numeroPedido = :numeroPedido ")
					.append(" ORDER BY ep.codigo DESC ");

			return em.createQuery(sbQuery.toString(), ExpedicaoPedido.class)
					.setParameter(NUMERO_PEDIDO, codPedido)
					.setMaxResults(1)
					.getSingleResult();
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TipoPedidoDTO> buscarTiposPedido() {
		try {
			String sql = "SELECT tppseq AS codigo, tppdestippedido AS descricao FROM omcgcpdbs..gcptbltpptipopedido WHERE tppcodtippedido NOT IN :tiposPedidosBloqueados";
			List<String> tiposPedidosBloqueados = 
					Arrays.asList(TipoPedidoEnum.CASSI.getChave(),
								  TipoPedidoEnum.MERCADORIA.getChave(),
								  TipoPedidoEnum.PESSOA_JURIDICA.getChave(),
						     	  TipoPedidoEnum.SERVICO.getChave(),
								  TipoPedidoEnum.ARAUJO_EXPRESS.getChave());
			
			List<Tuple> result = this.em.createNativeQuery(sql, Tuple.class)
										.setParameter("tiposPedidosBloqueados", tiposPedidosBloqueados)
										.getResultList();
			return result.stream()
				.map(tupla -> {
					TipoPedidoDTO tipoPedidoDTO = new TipoPedidoDTO();
					tipoPedidoDTO.setCodigo(tupla.get("codigo", Integer.class));
					tipoPedidoDTO.setDescricao(tupla.get("descricao", String.class));
					return tipoPedidoDTO;
				}).collect(Collectors.toList());
		} catch (Exception exception) {
			return Collections.emptyList();
		}
	}

	private String obterQueryPedidosPendentes(boolean isCountQuery) {
		if (isCountQuery) {
			return new StringBuilder()
				.append("SELECT SUM(qtd) FROM ( ")
				.append("  SELECT COUNT(p.pedi_nr_pedido) qtd")
				.append(   FROM_PEDIDO)
				.append("  LEFT JOIN drgtblpdcpedidocompl compl (NOLOCK) ON compl.pdcnrpedido = p.pedi_nr_pedido ")
				.append("  WHERE p.polo_cd_polo = :idFilial AND pedi_fl_operacao_loja = 'S' ")
				.append("    AND ( ")
				.append("      pedi_fl_fase IN ('06','08','18','20') ")
				.append("      OR ( ")
				.append("        pedi_fl_fase = '29' ")
				.append("        AND NOT EXISTS ( ")
				.append("          SELECT 1 FROM receita_produto_controlado rpc (NOLOCK) ")
				.append("          JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = rpc.itpd_cd_item_pedido ")
				.append("          WHERE ip.pedi_nr_pedido = p.pedi_nr_pedido ) ")
				.append("        ) ")
				.append("      ) ")
				.append("      AND COALESCE(p.pedi_fl_formula, 'N') = 'N' ")
				.append("      AND COALESCE(compl.pdcidcpapafila, 'N') = 'N' ")
				.append("  UNION ALL ")
				.append("  SELECT COUNT(p.pedi_nr_pedido) qtd ")
				.append(   FROM_PEDIDO)
				.append("  WHERE p.polo_cd_polo = :idFilial ")
				.append("    AND p.pedi_dh_termino_atendimento > '20211111' AND pedi_fl_operacao_loja = 'S' ")
				.append("    AND pedi_fl_fase = :cancelado ")
				.append("    AND EXISTS ( " )
				.append("      SELECT 1 FROM drgtblhfphistfasepedido_hst hist (NOLOCK) ")
				.append("      WHERE hist.pedi_nr_pedido = p.pedi_nr_pedido AND hist.pedi_fl_fase_atual = '29' ")
				.append("    )")
				.append("    AND NOT EXISTS ( " )
				.append("      SELECT 1 FROM drgtblcpccancpedcontrol cpc (NOLOCK) ")
				.append("      WHERE cpc.pedi_nr_pedido = p.pedi_nr_pedido AND cpc.cpcdthfim IS NOT NULL ")
				.append("    ) ")
				.append("  UNION ALL")
				.append("  SELECT COUNT(p.pedi_nr_pedido) qtd ")
				.append(   FROM_PEDIDO)
				.append("  WHERE pedi_fl_fase = '29' ")
				.append("    AND p.polo_cd_polo = :idFilial AND pedi_fl_operacao_loja = 'S' ")
				.append("    AND EXISTS( " )
				.append("      SELECT 1 FROM receita_produto_controlado rpc (NOLOCK) ")
				.append("      JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = rpc.itpd_cd_item_pedido ")
				.append("      WHERE ip.pedi_nr_pedido = p.pedi_nr_pedido ")
				.append("    ) ")
				.append(") tb ")
				.toString();
		}

		return new StringBuilder()
			.append("SELECT")
			.append("  pedido, ")
			.append("  fase, ")
			.append("  data_atualizacao, ")
			.append("  cliente, ")
			.append("  usuario, ")
			.append("  is_araujo_tem, ")
			.append("  has_apontamento_falta_araujo_tem ")
			.append("FROM ( ")
			.append("  SELECT")
			.append("    p.pedi_nr_pedido AS pedido, ")
			.append("    p.pedi_fl_fase AS fase, ")
			.append("    p.xxxx_dh_alt AS data_atualizacao, ")
			.append("    c.clnt_ds_nome AS cliente, ")
			.append("    (  ")
			.append("      SELECT MIN(u.usua_nm_usuario) ")
			.append("      FROM separacao_pedido sp (NOLOCK) ")
			.append("      INNER JOIN usuario u (NOLOCK) ON u.usua_cd_usuario = sp.usua_cd_usuario ")
			.append("      WHERE sp.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("    ) AS usuario, ")
			.append("    IIF(p.pedi_tp_pedido = 'A' AND nfd.pedi_nr_pedido IS NULL, 'TRUE', 'FALSE') AS is_araujo_tem, ")
			.append("    IIF(EXISTS( ")
			.append("      SELECT 1 FROM drgtblhfphistfasepedido_hst hist (NOLOCK) ")
			.append("      WHERE hist.pedi_nr_pedido = p.pedi_nr_pedido AND hist.pedi_fl_fase_atual = '15' ")
			.append("    ) AND ( p.pedi_tp_pedido = 'A' ), 'TRUE', 'FALSE') AS has_apontamento_falta_araujo_tem ") //OR EXISTS( SELECT 1 FROM modalidade_pagamento mp WHERE mp.pedi_nr_pedido = p.pedi_nr_pedido AND mp.tppg_cd_tipo_pagamento in (10,13,14,17,18) )
			.append(   FROM_PEDIDO)
			.append("  LEFT JOIN cliente (NOLOCK) c ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
			.append("  LEFT JOIN nota_fiscal_drogatel (NOLOCK) nfd ON p.pedi_nr_pedido = nfd.pedi_nr_pedido ")
			.append("  LEFT JOIN drgtblpdcpedidocompl compl (NOLOCK) ON compl.pdcnrpedido = p.pedi_nr_pedido ")
			.append("  WHERE p.polo_cd_polo = :idFilial ")
			.append("    AND pedi_fl_operacao_loja = 'S' ")
			.append("    AND ( ")
			.append("      pedi_fl_fase IN ('06','08','18','20') ")
			.append("      OR ( ")
			.append("        pedi_fl_fase = '29' ")
			.append("        AND NOT EXISTS( ")
			.append("          SELECT 1 FROM receita_produto_controlado rpc (NOLOCK) ")
			.append("          JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = rpc.itpd_cd_item_pedido ")
			.append("          WHERE ip.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("        ) ")
			.append("      ) ")
			.append("    ) ")
			.append("    AND COALESCE(p.pedi_fl_formula, 'N') = 'N' ")
			.append("    AND COALESCE(compl.pdcidcpapafila, 'N') = 'N' ")
			.append("  UNION ALL ")
			.append("  SELECT ")
			.append("    p.pedi_nr_pedido AS pedido, ")
			.append("    p.pedi_fl_fase AS fase, ")
			.append("    p.xxxx_dh_alt AS data_atualizacao, ")
			.append("    c.clnt_ds_nome AS cliente, ")
			.append("    ( ")
			.append("      SELECT u.usua_nm_usuario  ")
			.append("      FROM drgtblcpccancpedcontrol cpc (NOLOCK) ")
			.append("      INNER JOIN usuario u (NOLOCK) ON u.usua_cd_usuario = cpc.hdrcodusu ")
			.append("      WHERE cpc.cpcseq = ( ")
			.append("        SELECT MAX(cpcseq) FROM drgtblcpccancpedcontrol cc (NOLOCK) ")
			.append("        WHERE cc.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("      ) ")
			.append("    ) AS usuario, ")
			.append("    IIF(p.pedi_tp_pedido = 'A' AND nfd.pedi_nr_pedido IS NULL, 'TRUE', 'FALSE') AS is_araujo_tem, ")
			.append("    IIF(EXISTS( ")
			.append("      SELECT 1 FROM drgtblhfphistfasepedido_hst hist (NOLOCK) ")
			.append("      WHERE hist.pedi_nr_pedido = p.pedi_nr_pedido AND hist.pedi_fl_fase_atual = '15' ")
			.append("    ) AND ( p.pedi_tp_pedido = 'A'  ), 'TRUE', 'FALSE') AS has_apontamento_falta_araujo_tem ") //OR EXISTS( SELECT 1 FROM modalidade_pagamento mp WHERE mp.pedi_nr_pedido = p.pedi_nr_pedido AND mp.tppg_cd_tipo_pagamento in (10,13,14,17,18) )
			.append(FROM_PEDIDO)
			.append("  LEFT JOIN cliente (NOLOCK) c ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
			.append("  LEFT JOIN nota_fiscal_drogatel (NOLOCK) nfd ON p.pedi_nr_pedido = nfd.pedi_nr_pedido ")
			.append("  WHERE p.polo_cd_polo = :idFilial ")
			.append("    AND p.pedi_dh_termino_atendimento > '20211111' ")
			.append("    AND pedi_fl_operacao_loja = 'S' ")
			.append("    AND pedi_fl_fase = :cancelado ")
			.append("    AND EXISTS ( ")
			.append("      SELECT 1 FROM drgtblhfphistfasepedido_hst hist (NOLOCK) ")
			.append("      WHERE hist.pedi_nr_pedido = p.pedi_nr_pedido AND hist.pedi_fl_fase_atual = '29' ")
			.append("    ) ")
			.append("    AND NOT EXISTS ( ")
			.append("      SELECT 1 FROM drgtblcpccancpedcontrol cpc (NOLOCK) ")
			.append("      WHERE cpc.pedi_nr_pedido = p.pedi_nr_pedido AND cpc.cpcdthfim IS NOT NULL ")
			.append("    ) ")
			.append("  UNION ALL ")
			.append("  SELECT ")
			.append("    p.pedi_nr_pedido AS pedido, ")
			.append("    p.pedi_fl_fase AS fase, ")
			.append("    p.xxxx_dh_alt AS data_atualizacao, ")
			.append("    c.clnt_ds_nome AS cliente, ")
			.append("    ( ")
			.append("      SELECT TOP 1 u.usua_nm_usuario FROM receita_produto_controlado rpc (NOLOCK) ")
			.append("      JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = rpc.itpd_cd_item_pedido ")
			.append("      JOIN usuario u (NOLOCK) ON u.usua_cd_usuario = rpc.xxxx_cd_usualt ")
			.append("      WHERE ip.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("    ) AS usuario, ")
			.append("    IIF(p.pedi_tp_pedido = 'A' AND nfd.pedi_nr_pedido IS NULL, 'TRUE', 'FALSE') AS is_araujo_tem, ")
			.append("    IIF(EXISTS( ")
			.append("      SELECT 1 FROM drgtblhfphistfasepedido_hst hist (NOLOCK) ")
			.append("      WHERE hist.pedi_nr_pedido = p.pedi_nr_pedido AND hist.pedi_fl_fase_atual = '15' ")
			.append("    ) AND ( p.pedi_tp_pedido = 'A'  ), 'TRUE', 'FALSE') AS has_apontamento_falta_araujo_tem ") //OR EXISTS( SELECT 1 FROM modalidade_pagamento mp WHERE mp.pedi_nr_pedido = p.pedi_nr_pedido AND mp.tppg_cd_tipo_pagamento in (10,13,14,17,18) ) 
			.append(FROM_PEDIDO)
			.append("  LEFT JOIN cliente (NOLOCK) c ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
			.append("  LEFT JOIN nota_fiscal_drogatel (NOLOCK) nfd ON p.pedi_nr_pedido = nfd.pedi_nr_pedido ")
			.append("  WHERE pedi_fl_fase = '29' ")
			.append("    AND p.polo_cd_polo = :idFilial ")
			.append("    AND pedi_fl_operacao_loja = 'S' ")
			.append("    AND EXISTS (  ")
			.append("      SELECT 1 FROM receita_produto_controlado rpc (NOLOCK) ")
			.append("      JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = rpc.itpd_cd_item_pedido ")
			.append("      WHERE ip.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("    ) ")
			.append(") tb ")
			.append("ORDER BY pedido ")
			.toString();
	}

	@Override
	public int obterQuantidadePedidosPendente(Integer filial) {
		return (Integer) em.createNativeQuery(this.obterQueryPedidosPendentes(true))
			.setParameter("idFilial", filial)
			.setParameter("cancelado", FasePedidoEnum.CANCELADO.getChave())
			.getSingleResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PedidoDTO> obterPedidosPendente(Integer idFilial) {
		List<Tuple> resultado = em.createNativeQuery(this.obterQueryPedidosPendentes(false), Tuple.class)
			.setParameter("idFilial", idFilial)
			.setParameter("cancelado", FasePedidoEnum.CANCELADO.getChave())
			.getResultList();

		return resultado.stream()
			.map(tupla -> {
				FasePedidoEnum fase = FasePedidoEnum.buscarPorChave(tupla.get("fase", String.class));

				PedidoDTO pedidoDto = new PedidoDTO();
				pedidoDto.setNumeroPedido(tupla.get("pedido", Integer.class));
				pedidoDto.setDescricaoFase(fase == null ? " - " : fase.getDescricaoPainel());
				pedidoDto.setData(tupla.get("data_atualizacao", Date.class));
				pedidoDto.setNomeUsuario(StringUtils.defaultString(tupla.get("usuario", String.class), "-"));
				pedidoDto.setNomeCliente(tupla.get("cliente", String.class));
				pedidoDto.setAraujoTem(Boolean.parseBoolean(tupla.get("is_araujo_tem", String.class)));
				pedidoDto.setHasApontamentoFaltaAraujoTem(
					Boolean.parseBoolean(tupla.get("has_apontamento_falta_araujo_tem", String.class))
				);

				return pedidoDto;
			})
			.sorted(Comparator.comparing(PedidoDTO::isHasApontamentoFaltaAraujoTem, Comparator.reverseOrder()))
			.collect(Collectors.toList());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public PageWrapper<PedidoPendente25DiasDTO> obterPedidosPendente25Dias(Integer idFilial, int page) {
		int quantidadePorPagina = 10;
		int pagina = page - 1;
		
		List<Tuple> resultado = em.createNativeQuery(this.obterQueryPedidosPendentes25Dias(false), Tuple.class)
			.setParameter("idFilial", idFilial)
			.setFirstResult(pagina * quantidadePorPagina)
			.setMaxResults(quantidadePorPagina)
			.getResultList();

		 List<PedidoPendente25DiasDTO> pedidosPendentes = resultado.stream()
			.map(tupla -> {
				FasePedidoEnum fase = FasePedidoEnum.buscarPorChave(tupla.get("fase", String.class));
				PedidoPendente25DiasDTO pedidoDto = new PedidoPendente25DiasDTO();
				pedidoDto.setNumeroPedido(tupla.get("pedido", Integer.class));
				pedidoDto.setDescricaoFase(fase == null ? " - " : fase.getDescricaoPainel());
				pedidoDto.setData(tupla.get("data_atualizacao", Date.class));
				pedidoDto.setNomeUsuario(StringUtils.defaultString(tupla.get("usuario", String.class), "-"));
				pedidoDto.setNomeCliente(tupla.get("cliente", String.class));

				return pedidoDto;
			}).collect(Collectors.toList());
		
		
		Query queryCount = em.createNativeQuery(obterQueryPedidosPendentes25Dias(true)).setParameter("idFilial", idFilial);
		int totalRegistros = (int) queryCount.getSingleResult();
		
		return new PageWrapper<>(pedidosPendentes, page, quantidadePorPagina, totalRegistros);
	}

	private String obterQueryPedidosPendentes25Dias(boolean isCountQuery) {
		if(isCountQuery) {
			return new StringBuilder()
					.append("SELECT COUNT(p.pedi_nr_pedido)")
					.append(   FROM_PEDIDO)
					.append("  LEFT JOIN cliente (NOLOCK) c ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
					.append("  LEFT JOIN nota_fiscal_drogatel (NOLOCK) nfd ON p.pedi_nr_pedido = nfd.pedi_nr_pedido ")
					.append("  LEFT JOIN USUARIO (NOLOCK) u on u.USUA_CD_USUARIO = p.USUA_CD_USUARIO ")
					.append("  WHERE p.pedi_fl_fase = '19' ")
					.append("    AND p.polo_cd_polo = :idFilial ")
					.append("    AND p.pedi_fl_operacao_loja = 'S' ")
					.append("    AND p.tpfr_cd_tipo_frete = 15 ")
					.append("    AND DATEDIFF(DAY, nfd.nofi_dh_nota, CURRENT_TIMESTAMP) > 25 ")
					.toString();
		}
		
		return new StringBuilder()
				.append("SELECT")
				.append("  p.PEDI_NR_PEDIDO as pedido,  ")
				.append("  p.PEDI_FL_FASE as fase,  ")
				.append("  p.XXXX_DH_ALT as data_atualizacao,  ")
				.append("  c.clnt_ds_nome AS cliente, ")
				.append("  u.USUA_NM_USUARIO as usuario ")
				.append(   FROM_PEDIDO)
				.append("  LEFT JOIN cliente (NOLOCK) c ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
				.append("  LEFT JOIN nota_fiscal_drogatel (NOLOCK) nfd ON p.pedi_nr_pedido = nfd.pedi_nr_pedido ")
				.append("  LEFT JOIN USUARIO (NOLOCK) u on u.USUA_CD_USUARIO = p.USUA_CD_USUARIO ")
				.append("  WHERE p.pedi_fl_fase = '19' ")
				.append("    AND p.polo_cd_polo = :idFilial ")
				.append("    AND p.pedi_fl_operacao_loja = 'S' ")
				.append("    AND p.tpfr_cd_tipo_frete = 15 ")
				.append("    AND DATEDIFF(DAY, nfd.nofi_dh_nota, CURRENT_TIMESTAMP) > 25 ")
				.append("ORDER BY pedido ")
				.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CaptacaoLoteDTO> buscaLoteBipadoPorNumeroPedido(Long numeroPedido) {

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("  f.FILI_CD_FILIAL, ");
		sql.append("  ip.PEDI_NR_PEDIDO, ");
		sql.append("  ip.PRME_CD_PRODUTO, ");
		sql.append("  lb.LTBP_NR_LOTE, ");
		sql.append("  lb.ITPD_CD_ITEM_PEDIDO ");
		sql.append(" from ");
		sql.append("  LOTE_BIPADO lb (nolock) ");
		sql.append("  INNER JOIN ITEM_PEDIDO ip (nolock) on lb.ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO ");
		sql.append("  INNER JOIN PEDIDO pd (nolock) on ip.PEDI_NR_PEDIDO = pd.PEDI_NR_PEDIDO ");
		sql.append("  INNER JOIN FILIAL  f (nolock) ON pd.polo_cd_polo = f.fili_cd_filial ");
		sql.append("  where ip.PEDI_NR_PEDIDO = :numeroPedido ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(NUMERO_PEDIDO, numeroPedido);

		List<Object[]> result = query.getResultList();
		List<CaptacaoLoteDTO> retorno = new ArrayList<>();

		if (result != null) {
			result.forEach(item -> {
				CaptacaoLoteDTO captacaoLoteDTO = new CaptacaoLoteDTO();
				captacaoLoteDTO.setIdLoja(((Integer) item[0]).longValue());
				captacaoLoteDTO.setIdPedido(((Integer) item[1]).longValue());
				captacaoLoteDTO.setIdProduto(((Integer) item[2]).longValue());
				captacaoLoteDTO.setLote((String) item[3]);
				captacaoLoteDTO.setCodigoItemCR(((Integer) item[4]).longValue());
				retorno.add(captacaoLoteDTO);
			});
		}

		return retorno;
	}

	@SuppressWarnings("unchecked")
	public List<ProdutoDTO> buscarProdutosPorPedido(Long idPedido, String urlBaseImagem, boolean captacao, String nrReceita) {
		StringBuilder sql = new StringBuilder();

		if (!captacao) {
            sql.append("SELECT  itpd_cd_item_pedido,prme_cd_produto,PRME_NR_DV,PRME_TX_DESCRICAO1,");
            sql.append("        ITPD_NR_QUANTIDADE_PEDIDA, ITPD_NR_QUANTIDADE_SEPARADA, PRFI_QT_ESTOQATUAL, ");
            sql.append("        cod_barra,CAGC_CD_CATEGORIA,CAGC_NM_CATEGORIA,CONTROLADO,ANTIBIOTICO,");
            sql.append("        PRME_FL_GELADEIRA, TPLP_SG_PSICO, RCPC_FL_USO_PROLONGADO,");
            sql.append("        sum(rcpc_nr_caixas) as rcpc_nr_caixas, ITPD_VL_PRECO_UNITARIO, ITPD_VL_PRECO_PROMOCIONAL,TIRE_SQ_RECEITA");
            sql.append(" from (");
		}

        sql.append(" SELECT ip.itpd_cd_item_pedido, pm.prme_cd_produto, pm.PRME_NR_DV, ");
        sql.append("        pm.PRME_TX_DESCRICAO1, ip.ITPD_NR_QUANTIDADE_PEDIDA, ip.ITPD_NR_QUANTIDADE_SEPARADA, ");
        sql.append("        ISNULL(pf.PRFI_QT_ESTOQATUAL,0) as PRFI_QT_ESTOQATUAL,  ");
        sql.append("        ISNULL(SUBSTRING((  ");
        sql.append("            SELECT ','+ST1.COBM_CD_BARRA  AS [text()]  ");
        sql.append("            FROM dbo.COD_BARRA_MESTRE ST1 (nolock)  ");
        sql.append("            WHERE ST1.PRME_CD_PRODUTO = pm.PRME_CD_PRODUTO ");
        sql.append("            ORDER BY ST1.PRME_CD_PRODUTO  ");
        sql.append("            FOR XML PATH ('')  ");
        sql.append("        ), 2, 1000),'0') as cod_barra, ");
        sql.append("        gc.CAGC_CD_CATEGORIA, gc.CAGC_NM_CATEGORIA, ");
        sql.append("            CASE WHEN ip.ITPD_FL_PRODUTO_CONTROLADO = 'S' THEN 'true' ELSE 'false' END AS CONTROLADO, ");
        sql.append("            CASE WHEN ip.ITPD_FL_PRODUTO_ANTIBIOTICO = 'S' THEN 'true' ELSE 'false' END AS ANTIBIOTICO, ");
        sql.append("        pm.PRME_FL_GELADEIRA, L.TPLP_SG_PSICO, NULL as RCPC_FL_USO_PROLONGADO, ISNULL(rpc.rcpc_nr_caixas, 0 ) as rcpc_nr_caixas , ITPD_VL_PRECO_UNITARIO, ITPD_VL_PRECO_PROMOCIONAL");
        sql.append("        ,L.TIRE_SQ_RECEITA ");
        sql.append("      FROM ITEM_PEDIDO ip (NOLOCK) ");
        sql.append("        INNER JOIN PEDIDO p (NOLOCK) ON p.PEDI_NR_PEDIDO = ip.PEDI_NR_PEDIDO ");
        sql.append("        INNER JOIN PRODUTO_MESTRE pm (NOLOCK) ON pm.PRME_CD_PRODUTO = ip.PRME_CD_PRODUTO ");
        sql.append("        LEFT JOIN PRODUTO_FILIAL pf (NOLOCK) ON pm.PRME_CD_PRODUTO = pf.PRME_CD_PRODUTO AND pf.FILI_CD_FILIAL = p.polo_cd_polo ");
        sql.append("        LEFT JOIN RECEITA_PRODUTO_CONTROLADO rpc (NOLOCK) on rpc.ITPD_CD_ITEM_PEDIDO = ip.ITPD_CD_ITEM_PEDIDO  ");
        sql.append("        LEFT JOIN CATEGORIA_GC gc (NOLOCK) ON gc.CAGC_CD_CATEGORIA = pm.CAGC_CD_CATEGORIA ");
        sql.append("        inner JOIN PROD_MESTRE_PS_VAL V WITH (NOLOCK) ON V.PRME_CD_PRODUTO = pm.PRME_CD_PRODUTO and PMPV_DT_FIMVAL is null ");
        sql.append("        inner JOIN TP_LISTA_PSICOTROP L WITH (NOLOCK) ON V.TPLP_SQ_TPLISTASPS = L.TPLP_SQ_TPLISTASPS ");
        sql.append("        inner join TIPO_RECEITA tr (NOLOCK) on L.TIRE_SQ_RECEITA = tr.TIRE_SQ_RECEITA ");
        sql.append("        LEFT OUTER JOIN (SELECT COIN_TX_VALOR AS VALORANBITIOTICO ");
        sql.append("                         FROM COSMOS_INI WITH (NOLOCK) ");
        sql.append("                         WHERE COIN_TX_APLICACAO = 'CSMPSICOTROPICO' AND COIN_TX_SESSAO = 'EXPORTACAO_PRC' ");
        sql.append("                               AND COIN_TX_ENTRADA = 'LISTA_ANTIB') AS ANTIBIOTICOS ON L.TPLP_SQ_TPLISTASPS  = VALORANBITIOTICO  ");
        sql.append("      WHERE p.PEDI_NR_PEDIDO = :idPedido ");
		if (nrReceita != null) {
			sql.append("        and rpc.RCPC_NR_NUMERO_RECEITA = :nrReceita ");
		}
		if (!captacao) {
            sql.append(" ) as p ");
            sql.append(" GROUP BY ");
            sql.append("    itpd_cd_item_pedido, ");
            sql.append("    prme_cd_produto, ");
            sql.append("    PRME_NR_DV, ");
            sql.append("    PRME_TX_DESCRICAO1, ");
            sql.append("    ITPD_NR_QUANTIDADE_PEDIDA, ");
            sql.append("    ITPD_NR_QUANTIDADE_SEPARADA, ");
            sql.append("    PRFI_QT_ESTOQATUAL, ");
            sql.append("    cod_barra, ");
            sql.append("    CAGC_CD_CATEGORIA, ");
            sql.append("    CAGC_NM_CATEGORIA, ");
            sql.append("    CONTROLADO, ");
            sql.append("    ANTIBIOTICO, ");
            sql.append("    PRME_FL_GELADEIRA, ");
            sql.append("    TPLP_SG_PSICO, ");
            sql.append("    RCPC_FL_USO_PROLONGADO, ");
            sql.append("    ITPD_VL_PRECO_UNITARIO, ");
            sql.append("    ITPD_VL_PRECO_PROMOCIONAL, ");
            sql.append("    TIRE_SQ_RECEITA ");
            sql.append("    HAVING sum(rcpc_nr_caixas) < ITPD_NR_QUANTIDADE_PEDIDA ");
		}

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("idPedido", idPedido);
		if (nrReceita != null) {
			query.setParameter("nrReceita", nrReceita);
		}

		return ((List<Object[]>) query.getResultList()).stream().map(p -> {
			ProdutoDTO dto = new ProdutoDTO();
			dto.setId(((Integer) p[0]).longValue());
			dto.setIdProduto(((Integer) p[1]).longValue());
			dto.setDvProduto(((BigDecimal) p[2]).intValue());
			dto.setDescricaoProduto(p[3].toString());
			dto.setQuantidadePedida((Integer) p[4]);
			dto.setQuantidadeSeparada((Integer) p[5]);
			dto.setQuantidadeEstoque(((BigDecimal) p[6]).intValue());
			dto.setEans(p[7].toString().split(","));
			dto.setIdCategoria(p[8].toString());
			dto.setDescricaoCategoria(p[9].toString());
			dto.setControlado(Boolean.parseBoolean(p[10].toString()));
			dto.setAntibiotico(Boolean.parseBoolean(p[11].toString()));
			dto.setGeladeira(SimNaoEnum.S.getDescricao().equals(p[12].toString()));
			dto.setPrecoDe(((BigDecimal) p[16]).doubleValue());
			dto.setPrecoPor(((BigDecimal) p[17]).doubleValue());
			dto.setQuantidadeReceita(0);
			dto.setIdTipoReceita(Integer.valueOf((Short) p[18]));

			boolean usoProlongado = p[14] != null && "S".equals(p[14].toString());

			dto.setAntibioticoUsoProlongado(usoProlongado);

			if (p[15] == null) {
				dto.setNumeroCaixas(dto.getQuantidadePedida());
			} else {
				dto.setNumeroCaixas((Integer) p[15]);
			}

			dto.setProximaReceita(dto.getNumeroCaixas() > 0);
			dto.setTipoReceita(p[13].toString());
			dto.setExigeReceita(dto.isControlado() || dto.isAntibiotico());
			dto.setUrlImagem(imagemService.montarURLImagens(dto.getIdProduto().intValue(), urlBaseImagem));
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void ajustarQuantidadeSeparadaItemPedido(Integer numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append(" UPDATE item_pedido SET itpd_nr_quantidade_separada = itpd_nr_quantidade_pedida ")
				.append(" WHERE pedi_nr_pedido = :numeroPedido ");

		em.createNativeQuery(sql.toString()).setParameter(NUMERO_PEDIDO, numeroPedido).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PedidoRetornoMotociclistaDTO consultarPedidoRetornoMotociclista(String filtro) {
		StringBuilder sql = new StringBuilder()
			.append("SELECT ")
			.append("  numero_pedido, ")
			.append("  codigo_item_pedido, ")
			.append("  codigo_produto, ")
			.append("  digito_produto, ")
			.append("  descricao_produto, ")
			.append("  SUM(quantidade_produto) AS quantidade_produto, ")
			.append("  lote_produto, ")
			.append("  tipo_receita, ")
			.append("  codigos_ean ")
			.append("FROM (")
			.append("  SELECT ")
			.append("    p.pedi_nr_pedido AS numero_pedido, ")
			.append("    ip.itpd_cd_item_pedido AS codigo_item_pedido, ")
			.append("    pm.prme_cd_produto AS codigo_produto, ")
			.append("    pm.prme_nr_dv AS digito_produto, ")
			.append("    pm.prme_tx_descricao1 AS descricao_produto, ")
			.append("    CASE WHEN lb.ltbp_nr_lote is null THEN ip.itpd_nr_quantidade_pedida ELSE lb.ltbp_nr_quantidade_bipada END AS quantidade_produto, ")
			.append("    lb.ltbp_nr_lote AS lote_produto, ")
			.append("    tlp.tplp_sg_psico AS tipo_receita, ")
			.append("    ISNULL(SUBSTRING(( ")
			.append("      SELECT ';' + st1.cobm_cd_barra AS [text()] ")
			.append("      FROM dbo.cod_barra_mestre ST1 (NOLOCK) ")
			.append("      WHERE st1.prme_cd_produto = pm.prme_cd_produto ")
			.append("      ORDER BY st1.prme_cd_produto FOR XML PATH ('') ")
			.append("    ), 2, 1000), '0') AS codigos_ean ")
			.append(   FROM_PEDIDO)
			.append("  LEFT JOIN nota_fiscal_drogatel nfd (NOLOCK) ON p.pedi_nr_pedido = nfd.pedi_nr_pedido ")
			.append("  JOIN item_pedido ip (NOLOCK) ON ip.pedi_nr_pedido = p.pedi_nr_pedido ")
			.append("  LEFT JOIN lote_bipado lb (NOLOCK) ON lb.itpd_cd_item_pedido = ip.itpd_cd_item_pedido ")
			.append("  JOIN produto_mestre pm (NOLOCK) ON pm.prme_cd_produto = ip.prme_cd_produto ")
			.append("  LEFT JOIN prod_mestre_ps_val pmpv (NOLOCK) ON pm.prme_cd_produto = pmpv.prme_cd_produto AND pmpv.pmpv_dt_fimval IS NULL ")
			.append("  LEFT JOIN tp_lista_psicotrop tlp (NOLOCK) ON pmpv.tplp_sq_tplistasps = tlp.tplp_sq_tplistasps ")
			.append("  WHERE nfd.nofi_ch_acesso = :filtro OR CAST(p.pedi_nr_pedido AS VARCHAR) = :filtro")
			.append(") tb ")
			.append("GROUP BY numero_pedido, codigo_item_pedido, codigo_produto, digito_produto, ")
			.append("         descricao_produto, quantidade_produto, lote_produto, tipo_receita, codigos_ean ");

		List<Tuple> result = em.createNativeQuery(sql.toString(), Tuple.class)
			.setParameter(FILTRO, filtro)
			.getResultList();

		if (CollectionUtils.isEmpty(result)) {
			throw new BusinessException(PEDIDO_NAO_ENCONTRADO);
		}

		Tuple dadosPedido = result.get(0);
		Integer numeroPedido = dadosPedido.get(NUMERO_PEDIDO_ALIAS, Integer.class);

		List<ItemPedidoRetornoMotociclistaDTO> itens = result.stream()
			.map(
				tupla -> ItemPedidoRetornoMotociclistaDTO.builder()
					.codigoItem(tupla.get("codigo_item_pedido", Integer.class))
					.codigoProduto(tupla.get("codigo_produto", Integer.class))
					.digitoProduto(tupla.get("digito_produto", BigDecimal.class).intValue())
					.descricao(tupla.get("descricao_produto", String.class))
					.quantidadeNota(tupla.get("quantidade_produto", Integer.class))
					.lote(tupla.get("lote_produto", String.class))
					.tipoReceita(tupla.get("tipo_receita", String.class))
					.codigosEan(tupla.get("codigos_ean", String.class).split(";"))
					.build()
			)
			.collect(Collectors.groupingBy(ItemPedidoRetornoMotociclistaDTO::getCodigoItem))
			.values().stream()
			.map(itensAgrupadosPorCodigo -> {
				List<LoteBipadoDTO> lotes = itensAgrupadosPorCodigo.stream()
					.filter(itemPedidoDTO -> StringUtils.isNotBlank(itemPedidoDTO.getLote()))
					.map(
						itemPedidoDTO -> LoteBipadoDTO.builder()
							.lote(Objects.isNull(itemPedidoDTO.getLote()) ? null : itemPedidoDTO.getLote().toUpperCase())
							.quantidade(itemPedidoDTO.getQuantidadeNota())
							.build()
					).collect(Collectors.toList());

				ItemPedidoRetornoMotociclistaDTO itemPedidoRetornoMotociclistaDTO = itensAgrupadosPorCodigo.get(0);
				itemPedidoRetornoMotociclistaDTO.setLote(StringUtils.EMPTY);
				itemPedidoRetornoMotociclistaDTO.setLotes(lotes);
				itemPedidoRetornoMotociclistaDTO.setQuantidadeNota(
					itensAgrupadosPorCodigo.stream().mapToInt(ItemPedidoRetornoMotociclistaDTO::getQuantidadeNota).sum()
				);

				return itemPedidoRetornoMotociclistaDTO;
			})
			.collect(Collectors.toList());

		return PedidoRetornoMotociclistaDTO.builder()
			.numeroPedido(numeroPedido)
			.itens(itens)
			.build();
	}

	public boolean isSuperVendedor(Long numeroPedido) {
		String sql = "select PEDI_NR_PEDIDO from pedido where PEDI_NR_PEDIDO = :numeroPedido and PEDI_FL_SUPERVENDEDOR = 'S'";
		boolean isSuperVendedor = true;
		try {
			em.createNativeQuery(sql)
			  .setParameter(NUMERO_PEDIDO, numeroPedido)
			  .getSingleResult();

		} catch (NoResultException e) {
			isSuperVendedor = false;
		}
		return isSuperVendedor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PedidoDTO buscarPedidoParaEmitirNotaFiscalSap(Integer numeroPedido) {

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT p.pedi_nr_pedido, p.pedi_fl_fase, p.polo_cd_polo, c.clnt_ds_nome, pm.prme_cd_produto, ");
		sql.append(" pm.prme_tx_descricao1, SUM(ip.itpd_nr_quantidade_pedida) as qtd, pm.prme_nr_dv, p.fili_cd_filial_araujo_tem, ");
		sql.append(" vc.IVCDATVALIDADEPEDIDO as vecCurtoDataValidadePedid, nf.nofi_cd_nota, p.pedi_tp_pedido, ");
		sql.append(" CASE  WHEN V.PMPV_DT_FIMVAL IS NULL AND V.TPLP_SQ_TPLISTASPS IS NOT NULL THEN 'S'  ELSE 'N'  END AS psicotropico, ");
		sql.append(" ip.ITPD_FL_PRODUTO_ANTIBIOTICO AS antibiotico , ");
		sql.append(" SUM(ip.itpd_nr_quantidade_pedida) as qtd_pedida, "); // 14
		sql.append(" ip.itpd_fl_produto_controlado, "); // 15
		sql.append(" tlp.TPLP_SG_PSICO, "); // 16
		sql.append(" p.PEDI_FL_SUPERVENDEDOR, "); // 17
		sql.append(" p.pedi_fl_operacao_loja, "); // 18
		sql.append(" nf.nofi_ch_acesso "); // 19
		sql.append(FROM_PEDIDO);
		sql.append(" left join nota_fiscal_drogatel nf (nolock) on nf.pedi_nr_pedido = p.pedi_nr_pedido ");
		sql.append(" INNER JOIN item_pedido ip (nolock) ON p.pedi_nr_pedido = ip.pedi_nr_pedido ");
		sql.append(" INNER JOIN produto_mestre pm (nolock) ON ip.prme_cd_produto = pm.prme_cd_produto ");
		sql.append(" INNER JOIN cliente c (nolock) ON c.clnt_cd_cliente = p.clnt_cd_cliente ");
		sql.append(" LEFT JOIN DRGTBLIVCITEMVENCIMCURTO vc (nolock) ON ip.ITPD_CD_ITEM_PEDIDO = vc.ITPD_CD_ITEM_PEDIDO ");
		sql.append(" LEFT JOIN PROD_MESTRE_PS_VAL V WITH (nolock)  ON V.PRME_CD_PRODUTO = ip.PRME_CD_PRODUTO AND V.PMPV_DT_FIMVAL IS NULL ");
		sql.append(" LEFT  join TP_LISTA_PSICOTROP tlp  (NOLOCK) on V.TPLP_SQ_TPLISTASPS = tlp.TPLP_SQ_TPLISTASPS ");
		sql.append(" LEFT JOIN drgtblpdcpedidocompl compl (NOLOCK) ON compl.pdcnrpedido = p.pedi_nr_pedido ");
		sql.append(" WHERE ip.pedi_nr_pedido = :numeroPedido ");
		sql.append("   AND COALESCE(p.pedi_fl_formula, 'N') = 'N' ");
		sql.append("   AND COALESCE(compl.pdcidcpapafila, 'N') = 'N' ");
		sql.append(
				" GROUP BY p.pedi_nr_pedido, p.pedi_fl_fase, p.polo_cd_polo, c.clnt_ds_nome, pm.prme_cd_produto, pm.prme_tx_descricao1, "
						+ " pm.prme_nr_dv, p.fili_cd_filial_araujo_tem, vc.IVCDATVALIDADEPEDIDO, nf.nofi_cd_nota, p.pedi_tp_pedido, V.PMPV_DT_FIMVAL,"
						+ " V.PMPV_DT_FIMVAL, V.TPLP_SQ_TPLISTASPS, ip.ITPD_FL_PRODUTO_ANTIBIOTICO, ip.itpd_fl_produto_controlado, "
					  + " tlp.TPLP_SG_PSICO, p.PEDI_FL_SUPERVENDEDOR, p.pedi_fl_operacao_loja, nf.nofi_ch_acesso ");

		List<Object[]> resultado = em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList();

		if (CollectionUtils.isEmpty(resultado)) {
			throw new EntidadeNaoEncontradaException(PEDIDO_NAO_ENCONTRADO);
		}

		PedidoDTO dto = new PedidoDTO();
		dto.setNumeroPedido((Integer) resultado.get(0)[0]);
		dto.setFase(FasePedidoEnum.buscarPorChave((String) resultado.get(0)[1]));
		dto.setCodFilial((Integer) resultado.get(0)[2]);
		dto.setAraujoTem(resultado.get(0)[8] != null);
		dto.setItens(new ArrayList<>());
		dto.setIdNotaFiscal((Integer) resultado.get(0)[10]);
		dto.setTipoPedido(TipoPedidoEnum.buscarPorChave((String.valueOf(resultado.get(0)[11]))));
		dto.setPedidoSuperVendedor("S".equals(String.valueOf(resultado.get(0)[17])));
		dto.setPedidoLoja(SimNaoEnum.getValueByString(String.valueOf(resultado.get(0)[18])));
		dto.setChaveNotaFiscal((String) resultado.get(0)[19]);

		resultado.forEach(item -> {
			ItemPedidoDTO itemPedidoDTO = new ItemPedidoDTO();
			itemPedidoDTO.setCodigoProduto((Integer) item[4]);
			itemPedidoDTO.setDescricaoProduto(String.valueOf(item[5]));
			itemPedidoDTO.setDigitoProduto(((BigDecimal) item[7]).intValue());
			itemPedidoDTO.setAntibiotico("S".equals(String.valueOf(item[13])));
			itemPedidoDTO.setQuantidadePedida((Integer) item[14]);
			itemPedidoDTO.setControlado("S".equals(String.valueOf(item[15])));
			itemPedidoDTO.setTipoReceita(item[16] == null ? "": item[16].toString());

			if (item[9] != null) {
				itemPedidoDTO.setDtValVencCurt(new SimpleDateFormat(SIMPLE_DATE_PATTERN).format(((Date) item[9])));
			}
			dto.getItens().add(itemPedidoDTO);
		});

		return dto;
	}

	@Override
	@Transactional("drogatelTransactionManager")
	public void updateStatusIntegrationPedido(String statusPedido, String mensagem, Integer numeroPedido) {
		StringBuilder sql = new StringBuilder();
		if(selectParaUpdateFasePainel(numeroPedido)) {
			sql.append("INSERT INTO DRGTBLCFPCONTRFASINTPED ")
			   .append("(HDRDTHHOR, HDRCODUSU, HDRCODLCK, HDRDTHINS, HDRCODETC, HDRCODPRG, PEDI_NR_PEDIDO, PEDI_TX_FASE_INTEGRACAO_DRIN, PEDI_TX_ERRO_INTEGRACAO)")
			   .append(" VALUES( :dataAlt, 1, 1, :dataAlter, '', :appAlterador, :numeroPedido, :faseProcesso, :mensagem)");
		} else {
			sql.append(" UPDATE DRGTBLCFPCONTRFASINTPED ")
			   .append(" SET HDRDTHHOR = :dataAlt , ")
			   .append("     HDRDTHINS = :dataAlter , ")
			   .append("     HDRCODPRG = :appAlterador , ")
			   .append("     PEDI_TX_FASE_INTEGRACAO_DRIN = :faseProcesso , ")
			   .append("     PEDI_TX_ERRO_INTEGRACAO = :mensagem,  ")
			   .append("     HDRCODUSU = 1, HDRCODLCK = 1 ")
			   .append(" WHERE pedi_nr_pedido = :numeroPedido ");
		}

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("dataAlt", new Date());
		query.setParameter("dataAlter", new Date());
		query.setParameter("appAlterador", "Clique Retire");
		query.setParameter(NUMERO_PEDIDO, numeroPedido);
		query.setParameter("faseProcesso", statusPedido);
		query.setParameter("mensagem", definirMensagem(mensagem));
		query.executeUpdate();

	}

	private String definirMensagem(String mensagem) {
		if (StringUtils.isBlank(mensagem))
			return "";

		return mensagem.length() > 2000 ? mensagem.substring(0, 2000) : mensagem;
	}

	public boolean selectParaUpdateFasePainel(Integer numeroPedido) {
		String sql = "SELECT 1 FROM DRGTBLCFPCONTRFASINTPED DP (nolock) WHERE DP.PEDI_NR_PEDIDO = :numeroPedido";

		return em.createNativeQuery(sql)
				 .setParameter(NUMERO_PEDIDO, numeroPedido)
				 .getResultList()
				 .isEmpty();
	}


	@Override
	public Boolean isPagamentoSAP(Integer numeroPedido, Integer numeroTipoPagamento) {
		String sql = "SELECT COUNT(*) FROM MODALIDADE_PAGAMENTO M WHERE M.PEDI_NR_PEDIDO = :numeroPedido and M.TPPG_CD_TIPO_PAGAMENTO = :numeroTipoPagamento";

		Integer result = (Integer) em.createNativeQuery(sql)
			.setParameter(NUMERO_PEDIDO, numeroPedido)
			.setParameter("numeroTipoPagamento", numeroTipoPagamento)
			.getSingleResult();

		return result > 0;
	}

	@Override
    public RelatorioPedidoDevolucaoAraujoTemDTO obterPedidoFaltaAraujoTemDevolucao(Long numeroPedido) {
        StringBuilder sql = new StringBuilder()
			.append(" SELECT ")
            .append("    p.pedi_nr_pedido AS numeroPedido, ")
            .append("    c.clnt_ds_nome AS nomeCliente, ")
            .append("    filialOrigem.fili_nm_fantasia AS filialOrigem, ")
            .append("    f.fili_nm_fantasia AS filialDestino, ")
            .append("    p.pedi_fl_supervendedor AS sv, ")
            .append("    CASE ")
            .append("        WHEN tp.tppg_ds_descricao LIKE '%parcelado%' OR tp.tppg_ds_descricao = 'ROTATIVO' THEN 'CRÉDITO' ")
            .append("        WHEN tp.tppg_ds_descricao = 'DEBITO' THEN 'DÉBITO' ")
            .append("        WHEN tp.tppg_ds_descricao LIKE '%PIX%' THEN 'PIX' ")
            .append("        ELSE tp.tppg_ds_descricao ")
            .append("    END AS tipoPagamento ")
            .append(FROM_PEDIDO)
            .append(" INNER JOIN CLIENTE c (nolock)  ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
            .append(" INNER JOIN  FILIAL f (nolock)  ON p.polo_cd_polo = f.fili_cd_filial ")
            .append(" JOIN MODALIDADE_PAGAMENTO mp (NOLOCK) ON mp.pedi_nr_pedido = p.pedi_nr_pedido ")
            .append(" JOIN TIPO_PAGAMENTO tp (NOLOCK) ON tp.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
            .append(" LEFT JOIN FILIAL filialOrigem (nolock) ON p.fili_cd_filial_araujo_tem = filialOrigem.fili_cd_filial ")
            .append(" WHERE p.pedi_nr_pedido = :numeroPedido ");

        Tuple dadosPedido = (Tuple) em.createNativeQuery(sql.toString(), Tuple.class)
            .setParameter(NUMERO_PEDIDO, numeroPedido)
            .getSingleResult();

        return RelatorioPedidoDevolucaoAraujoTemDTO.builder()
            .numeroPedido(dadosPedido.get(NUMERO_PEDIDO, Integer.class))
            .nomeCliente(dadosPedido.get("nomeCliente", String.class))
            .filialOrigem(dadosPedido.get("filialOrigem", String.class))
            .filialDestino(dadosPedido.get("filialDestino", String.class))
            .sv(Character.valueOf('S').equals(dadosPedido.get("sv", Character.class)))
            .tipoPagamento(dadosPedido.get("tipoPagamento", String.class))
            .build();
    }

    @Override
    public String obterPedidoControladoParaCancelamento(Integer numeroPedido) {
        StringBuilder sql = new StringBuilder()
				.append(" DECLARE ")
				.append(" @tipo_pagamento CHAR(1), ")
				.append(" @tipo_pedido VARCHAR(30), ")
				.append(" @modalidade_pedido VARCHAR(20) = '', ")
				.append(" @convenio int = 0 ")
				.append(" SELECT  ")
				.append("   @tipo_pagamento = p.pedi_TP_PEDIDO, ")
				.append("   @tipo_pedido = ")
				.append("   CASE ")
				.append("       WHEN tp.tppg_ds_descricao LIKE '%parcelado%' OR tp.tppg_ds_descricao = 'ROTATIVO' THEN 'CRÉDITO' ")
				.append("       WHEN tp.tppg_fl_tipo  = '07' THEN 'DINHEIRO' ")
				.append("       WHEN tp.tppg_ds_descricao = 'BOLETO BANCARIO' ")
				//.append("           OR tp.tppg_ds_descricao = 'DEPÓSITO BANCÁRIO'  ")
				.append("           OR tp.tppg_ds_descricao = 'DEBITO' THEN 'ANTECIPADO' ")
				.append("       WHEN tp.tppg_ds_descricao LIKE '%PIX%' THEN 'PIX' ")
				.append("   END, ")
				.append(" @convenio =  (select  count(1) ")
				.append("     from PAGAMENTO_EM_CONVENIO (nolock) ")
				.append("     where MDPG_CD_MODALIDADE_PAGAMENTO in ( ")
				.append("         select MDPG_CD_MODALIDADE_PAGAMENTO ")
				.append("         from MODALIDADE_PAGAMENTO (nolock) ")
				.append("         where PEDI_NR_PEDIDO = :numeroPedido)) ")
				.append(" FROM tipo_pagamento tp (nolock) ")
				.append(" JOIN MODALIDADE_PAGAMENTO mp (nolock) ON tp.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
				.append(" JOIN PEDIDO p (nolock) ON p.pedi_nr_pedido = mp.pedi_nr_pedido ")
				.append(" WHERE p.PEDI_NR_PEDIDO = :numeroPedido ")
				.append(" if (@tipo_pedido = 'DINHEIRO' AND @tipo_pagamento = 'D')  ")
				.append(" begin ")
				.append("   select @modalidade_pedido = 'DIN' ")
				.append(" end ")
				.append(" if (@tipo_pedido = 'CRÉDITO' OR @convenio > 0) AND @tipo_pagamento in ('F','D','E') ")
				.append(" begin ")
				.append("   select @modalidade_pedido = 'CAC' ")
				.append(" end ")
				.append(" if ((@tipo_pedido = 'PIX' AND @tipo_pagamento in ('F','D','E')) OR  @tipo_pedido = 'ANTECIPADO') ")
				.append(" BEGIN ")
				.append("   select @modalidade_pedido = 'PAN' ")
				.append(" END ")
				.append(" select @modalidade_pedido ");

        return (String) em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getSingleResult();
    }

	@Override
	@SuppressWarnings("unchecked")
	public List<TipoPagamentoEnum> buscarTiposPagamentoPedido(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT tp.tppg_fl_tipo FROM modalidade_pagamento mp (NOLOCK) ")
				.append("JOIN tipo_pagamento tp (NOLOCK) ON tp.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
				.append("WHERE mp.pedi_nr_pedido = :numeroPedido");

		Stream<String> tipos = em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList().stream()
				.map(String::valueOf);

		return tipos.map(TipoPagamentoEnum::porCodigo).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	public Optional<ModalidadePagamentoDTO> buscarModalidadePagamentoPedido(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT ")
				.append("  mp.mdpg_cd_modalidade_pagamento AS codigo_modalidade, ")
				.append("  mp.mdpg_vl_pago AS valor_pago, ")
				.append("  mdpg_vl_parcela AS valor_parcela, ")
				.append("  mdpg_nr_qtde_parcelas AS quantidade_parcelas, ")
				.append("  tp.tppg_fl_tipo AS tipo_pagamento ")
				.append("FROM modalidade_pagamento mp (NOLOCK) ")
				.append("JOIN tipo_pagamento tp (NOLOCK) ON tp.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
				.append("LEFT JOIN pagamento_cartao pc (NOLOCK) ")
				.append("  ON pc.mdpg_cd_modalidade_pagamento = mp.mdpg_cd_modalidade_pagamento ")
				.append("WHERE mp.pedi_nr_pedido = :numeroPedido ");

		List<Tuple> result = em.createNativeQuery(sql.toString(), Tuple.class)
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList();

		if (CollectionUtils.isEmpty(result)) {
			return Optional.empty();
		}

		Tuple tupla = result.iterator().next();

		ModalidadePagamentoDTO modalidade = new ModalidadePagamentoDTO();
		modalidade.setCodigo(tupla.get("codigo_modalidade", Integer.class));
		modalidade.setValorPago(tupla.get("valor_pago", BigDecimal.class).doubleValue());

		BigDecimal valorParcela = tupla.get("valor_parcela", BigDecimal.class);
		modalidade.setValorParcela(valorParcela == null ? null : valorParcela.doubleValue());
		Integer quantidadeParcelas = tupla.get("quantidade_parcelas", Integer.class);
		modalidade.setQuantidadeParcelas(quantidadeParcelas == null ? 0 : quantidadeParcelas);

		modalidade.setTipoPagamento(TipoPagamentoEnum.porCodigo(tupla.get("tipo_pagamento", String.class)));

		return Optional.of(modalidade);
	}

	@Transactional
	public void atualizaValoresModalidadePagamento(ModalidadePagamentoDTO modalidadePagamento) {
		StringBuilder updateModalidade = new StringBuilder()
				.append("UPDATE modalidade_pagamento ")
				.append("SET mdpg_vl_pago = :valorPago, mdpg_vl_parcela = :valorParcela, xxxx_dh_alt = CURRENT_TIMESTAMP ")
				.append("WHERE mdpg_cd_modalidade_pagamento = :codigoModalidadePagamento ");

		em.createNativeQuery(updateModalidade.toString())
				.setParameter("valorPago", modalidadePagamento.getValorPago())
				.setParameter("valorParcela", modalidadePagamento.getValorParcela())
				.setParameter("codigoModalidadePagamento", modalidadePagamento.getCodigo())
				.executeUpdate();
	}

	@Override
	public boolean isPedidoComConvenio(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT 1 FROM pagamento_em_convenio ")
				.append("WHERE mdpg_cd_modalidade_pagamento IN ( ")
				.append("  SELECT mdpg_cd_modalidade_pagamento FROM modalidade_pagamento ")
				.append("  WHERE pedi_nr_pedido = :numeroPedido ")
				.append(")");

		return !em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList()
				.isEmpty();
	}

	@Override
	public PedidoEditadoEmailDTO buscarDadosPedidoParaEnvioEmail(Long numeroPedido) {
		try {
			StringBuilder sql = new StringBuilder()
					.append("SELECT ")
					.append("  p.pedi_nr_ecom_cliente AS numero_ecommerce, ")
					.append("  c.clnt_ds_email AS email_cliente, ")
					.append("  CASE WHEN cc.cacr_nm_cartao IS NULL THEN tp.tppg_ds_descricao ELSE cc.cacr_nm_cartao END AS tipo_cartao, ")
					.append("  cc.cacr_tp_cartao AS tipo_pagamento, ")
					.append("  mp.mdpg_vl_pago AS valor_total_pedido, ")
					.append("  p.pedi_vl_total_itens_pedido AS valor_total_itens_pedido, ")
					.append("  mp.mdpg_nr_qtde_parcelas AS quantidade_parcelas, ")
					.append("  p.pedi_vl_taxa_entrega AS valor_frete, ")
					.append("  mp.mdpg_vl_acrescimo AS valor_juros ")
					.append("FROM pedido p (NOLOCK) ")
					.append("JOIN cliente c (NOLOCK) ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
					.append("JOIN modalidade_pagamento mp (NOLOCK) ON mp.pedi_nr_pedido = p.pedi_nr_pedido ")
					.append("JOIN tipo_pagamento tp (NOLOCK) ON tp.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
					.append("LEFT JOIN cartao_credito cc (NOLOCK) ON cc.cacr_sq_cartao = tp.cacr_sq_cartao ")
					.append("WHERE p.pedi_nr_pedido = :numeroPedido ");

			Tuple dadosPedido = (Tuple) em.createNativeQuery(sql.toString(), Tuple.class)
					.setParameter(NUMERO_PEDIDO, numeroPedido)
					.getSingleResult();

			PagamentoComunicacaoDTO pagamento = new PagamentoComunicacaoDTO();
			Character tipoPagamento = dadosPedido.get("tipo_pagamento", Character.class);
			pagamento.setTipoPagamento(Objects.nonNull(tipoPagamento) ? String.valueOf(tipoPagamento): "");
			pagamento.setTipoCartao(dadosPedido.get("tipo_cartao", String.class));
			pagamento.setQuantidadeParcelas(dadosPedido.get("quantidade_parcelas", Integer.class));
			pagamento.setValorTotalPedido(dadosPedido.get("valor_total_pedido", BigDecimal.class).doubleValue());
			pagamento.setValorTotalItensPedido(dadosPedido.get("valor_total_itens_pedido", BigDecimal.class).doubleValue());
			pagamento.setValorFrete(dadosPedido.get("valor_frete", BigDecimal.class).doubleValue());
			pagamento.setValorJuros(dadosPedido.get("valor_juros", BigDecimal.class).doubleValue());

			PedidoEditadoEmailDTO pedido = new PedidoEditadoEmailDTO();
			pedido.setNumeroPedido(numeroPedido);
			pedido.setEmailCliente(dadosPedido.get("email_cliente", String.class));
			String numeroEcommerce = dadosPedido.get("numero_ecommerce", String.class);
			pedido.setNumeroPedidoEcommerce(Objects.nonNull(numeroEcommerce) ? numeroEcommerce : "");
			pedido.setPagamento(pagamento);
			return pedido;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new BusinessException("Ocorreu um erro ao buscar os dados do pedido para envio de email.");
		}
	}
	
	@Override
	public String obterEmailClientePedido(Long numeroPedido) {
		try {
			StringBuilder sql = new StringBuilder()
					.append("SELECT c.clnt_ds_email AS email_cliente ")
					.append("FROM pedido p (NOLOCK) ")
					.append("JOIN cliente c (NOLOCK) ON c.clnt_cd_cliente = p.clnt_cd_cliente ")
					.append("WHERE p.pedi_nr_pedido = :numeroPedido ");
			
			return (String) em.createNativeQuery(sql.toString())
	                    	  .setParameter(NUMERO_PEDIDO, numeroPedido)
	                    	  .getSingleResult();
		} catch (NoResultException ex) {
			log.error(ex.getMessage(), ex);
			throw new BusinessException("Ocorreu um erro ao buscar o email do cliente.");
		}
	}

	@Override
	public int buscarQuantidadeDeApontamentoDeFaltaEmLojaDiferente(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT hf.codigoPolo FROM HistoricoFasePedido hf ")
				.append("WHERE hf.pedido.numeroPedido = :numeroPedido AND hf.fasePedidoAtual = :fase ")
				.append("GROUP BY hf.codigoPolo");

		return em.createQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.setParameter("fase", FasePedidoEnum.AGUARDANDO_NEGOCIACAO)
				.getResultList()
				.size();
	}

	@Override
	public List<PrePedidoSiac> buscarPrePedidoSiac(Integer numeroPedido) {
		return em.createQuery("SELECT pps FROM PrePedidoSiac pps WHERE pps.numeroPedido = :numeroPedido", PrePedidoSiac.class)
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList();
	}

	@Override
	@Transactional
	public void atualizarFasePrePedido(String codigoOrigemPrePedido) {
		em.createNativeQuery("UPDATE OMCGCPDBS.dbo.GCPTBLPEDPEDIDO SET FPDSEQ = 4 WHERE PEDSEQ = :codigoOrigemPrePedido ")
		  .setParameter("codigoOrigemPrePedido", codigoOrigemPrePedido)
		  .executeUpdate();
	}

	@Override
	public boolean isPagamentoEmDinheiro4ponto0Drogatel(Long numeroPedido){
		StringBuilder sql = new StringBuilder()
				.append("SELECT tp.tppg_fl_tipo FROM tipo_pagamento tp (nolock) ")
				.append("JOIN modalidade_pagamento mp (nolock) ON tp.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
				.append("JOIN pedido p (nolock) ON p.pedi_nr_pedido = mp.pedi_nr_pedido ")
				.append("WHERE mp.pedi_nr_pedido = :numeroPedido ")
				.append("      AND p.pedi_fl_operacao_loja = 'S' ")
				.append("      AND tp.tppg_fl_tipo = :tipoPagamento ")
				.append("      AND p.pedi_tp_pedido in (:tipoDrogatel, :tipoAraujoTem) ")
				.append("      AND p.tpfr_cd_tipo_frete = :frete ")
				.append("      AND p.pedi_fl_supervendedor <> 'S'  ");
		
		return !em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.setParameter("tipoPagamento",TipoPagamentoEnum.DINHEIRO.getCodigo())
				.setParameter("tipoDrogatel",TipoPedidoEnum.DROGATEL.getChave())
				.setParameter("tipoAraujoTem",TipoPedidoEnum.ARAUJOTEM.getChave())
				.setParameter("frete", TIPO_FRETE_ENTREGA_VIA_MOTO_ID)				
				.getResultList().isEmpty();
	}
	
	public PedidoDTO obtemPedidoParaNormalizarPreco(Integer numeroPedido) {
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		sql.append("    pedi_vl_total_itens_pedido AS totalItens,");
		sql.append("    pedi_vl_total_pedido AS totalPedido, ");
		sql.append("    pedi_fl_operacao_loja AS operacao, ");
		sql.append("    pedi_tp_pedido AS tipo ");
		sql.append("FROM pedido p (nolock) ");
		sql.append("WHERE p.pedi_nr_pedido = :numeroPedido ");

		PedidoDTO dto = null;
		try {
			Tuple pedido = 
				(Tuple) em.createNativeQuery(sql.toString(), Tuple.class)
					 	  .setParameter(NUMERO_PEDIDO, numeroPedido)
                          .getSingleResult();
			
			dto = new PedidoDTO();
			dto.setTotalItensPedido(pedido.get("totalItens", BigDecimal.class).doubleValue());
			dto.setTotalPedido(pedido.get("totalPedido", BigDecimal.class).doubleValue());
			dto.setPedidoLoja(SimNaoEnum.valueOf(pedido.get("operacao", Character.class).toString()));
			dto.setTipoPedido(TipoPedidoEnum.buscarPorChave(pedido.get("tipo", Character.class).toString()));
			dto.setNumeroPedido(numeroPedido);
			
		} catch (NoResultException e) {
			log.info("Pedido {} não encontrado!!", numeroPedido);
		}
		
		return dto;
	}

	/**
	 * Método para obter informações da modalidade pagamento.
	 * 
	 * @param numeroPedido
	 * @return retorna um DTO da modalidade_pagamento
	 */
	public PagamentoDinheiroDTO obterModalidadePagamentoDinheiro(Integer numeroPedido) {
		StringBuilder sql = 
			new StringBuilder("SELECT ")
					  .append("  pd.mdpg_cd_modalidade_pagamento as pagamentoCartao, ")
					  .append("  mp.mdpg_vl_pago as valorPago, ")
					  .append("  pd.pgdn_vl_troco as troco ")
					  .append("FROM modalidade_pagamento mp (nolock) ")
					  .append("INNER JOIN pagamento_dinheiro pd (nolock) ON pd.mdpg_cd_modalidade_pagamento = mp.mdpg_cd_modalidade_pagamento ")
					  .append("WHERE mp.pedi_nr_pedido = :numeroPedido ");

		PagamentoDinheiroDTO dto = null;
		try {
			Tuple modalidade = 
				(Tuple) em.createNativeQuery(sql.toString(), Tuple.class)
					 	  .setParameter(NUMERO_PEDIDO, numeroPedido)
                          .getSingleResult();
			
			dto = new PagamentoDinheiroDTO();
			dto.setCodigoModalidadePagamento(modalidade.get("pagamentoCartao", Integer.class));
			dto.setValor(modalidade.get("valorPago", BigDecimal.class).doubleValue());
			dto.setTroco(modalidade.get("troco", BigDecimal.class).doubleValue());
		} catch (NoResultException e) {
			log.info("Pagamento em dinheiro não identificado para o pedido {}.", numeroPedido);
		}
		
		return dto;
	}

	public void atualizaPrecoPedidoSIAC(PagamentoDinheiroDTO dinheiroDTO, PedidoDTO pedidoDTO) {

		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE pedido SET pedi_vl_total_itens_pedido = :totalItens , ");
		sql.append(" pedi_vl_total_pedido = :totalPedido , xxxx_dh_alt = :data ");
		sql.append(" WHERE pedi_nr_pedido = :numeroPedido   ");

		em.createNativeQuery(sql.toString())
		  .setParameter("totalItens", pedidoDTO.getTotalItensPedido())
		  .setParameter("totalPedido", pedidoDTO.getTotalPedido())
		  .setParameter("data", new Date())
		  .setParameter(NUMERO_PEDIDO, pedidoDTO.getNumeroPedido())
		  .executeUpdate();

		sql = new StringBuilder();
		sql.append(" UPDATE modalidade_pagamento ");
		sql.append(" SET mdpg_vl_pago = :valorPago, xxxx_dh_alt = :data ");
		sql.append(" WHERE mdpg_cd_modalidade_pagamento = :codigoModalidadePagamento  ");

		em.createNativeQuery(sql.toString())
		  .setParameter("valorPago", dinheiroDTO.getValor())
		  .setParameter("data", new Date())
		  .setParameter("codigoModalidadePagamento", dinheiroDTO.getCodigoModalidadePagamento())
		  .executeUpdate();
		
		sql = new StringBuilder();
		sql.append(" UPDATE pagamento_dinheiro SET pgdn_vl_troco = :troco ");
		sql.append(" WHERE mdpg_cd_modalidade_pagamento = :codigoModalidadePagamento ");

		em.createNativeQuery(sql.toString())
		  .setParameter("troco", dinheiroDTO.getTroco())
		  .setParameter("codigoModalidadePagamento", dinheiroDTO.getCodigoModalidadePagamento())
		  .executeUpdate();

	}
	
	@Override
	public boolean isProdutoGeladeira(Long numeroPedido) {
		StringBuilder sql = new StringBuilder()
				.append("SELECT pm.prme_fl_geladeira FROM PRODUTO_MESTRE pm (nolock) ")
				.append("JOIN ITEM_PEDIDO ip (nolock) ON pm.prme_cd_produto = ip.prme_cd_produto ")
				.append("WHERE ip.pedi_nr_pedido = :numeroPedido and pm.prme_fl_geladeira = 'S'");
		
		return !em.createNativeQuery(sql.toString())
				.setParameter(NUMERO_PEDIDO, numeroPedido)
				.getResultList().isEmpty();
	}
}