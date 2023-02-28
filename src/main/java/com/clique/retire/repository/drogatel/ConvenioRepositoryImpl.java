package com.clique.retire.repository.drogatel;

import static com.clique.retire.util.Constantes.NUMERO_PEDIDO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.ItemSolicitacaoAutorizacaoConvenioDTO;
import com.clique.retire.dto.PagamentoEmConvenioDTO;
import com.clique.retire.dto.SolicitacaoAutorizacaoConvenioDTO;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoRegistroPrescritorEnum;
import com.clique.retire.infra.exception.BusinessException;
import com.clique.retire.util.Constantes;
import com.clique.retire.util.SecurityUtils;

@Repository
public class ConvenioRepositoryImpl implements ConvenioRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    
    @Override
    @Transactional
    public void removerDadosSobreAutorizacaoConvenioEmExcesso(Long numeroPedido) {
    	StringBuilder sqlDeleteItensAutorizacaoEmExcesso = new StringBuilder()
        		.append("DELETE FROM item_autorizacao_convenio ")
                .append("WHERE mdpg_cd_modalidade_pagamento IN :modalidadesPagamento ")
                .append("  AND iaco_cd_item_autorizacao NOT IN ( ")
                .append("      SELECT iaco_cd_item_autorizacao ")
                .append("      FROM item_autorizacao_convenio iac (nolock) ")
                .append("      INNER JOIN item_pedido ip (nolock) ON iac.itpd_cd_item_pedido = ip.itpd_cd_item_pedido AND iaco_vl_pago_convenio = ip.itpd_vl_preco_unitario  ")
                .append("                                            AND iaco_nr_qtd_autorizada = ip.itpd_nr_quantidade_pedida ")
                .append("      WHERE ip.pedi_nr_pedido = :numeroPedido ")
                .append("  )");
    	
        em.createNativeQuery(sqlDeleteItensAutorizacaoEmExcesso.toString())
		        .setParameter("modalidadesPagamento", obterCodigosModalidadesPagamento(numeroPedido))
		        .setParameter(NUMERO_PEDIDO, numeroPedido)
		        .executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SolicitacaoAutorizacaoConvenioDTO obterDadosParaRegerarAutorizacaoConvenio(List<Integer> codigosItem, Integer idLojaSolicitacao) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT ")
                .append("  isnull(au.acpb_cd_integrador, a.apbm_cd_codigo) AS codigo_autorizadora_pbm, ")
                .append("  p.pedi_nr_autorizacao_pbm AS numero_autorizacao_pbm, ")
                .append("  pec.empr_cd_empresa AS codigo_empresa_conveniada, ")
                .append("  pec.peco_tx_ident_conveniado AS identificacao_conveniado, ")
                .append("  IIF(pec.peco_vl_pago_vista IS NULL OR pec.peco_vl_pago_vista = 0, 'FALSE', 'TRUE') AS pago_a_vista, ")
                .append("  pec.peco_dh_receita AS data_receita, ")
                .append("  pec.peco_tx_dependente AS dependente, ")
                .append("  rpc.rcpc_nr_numero_receita AS numero_receita, ")
                .append("  rpc.rcpc_fl_uso_prolongado AS uso_prolongado, ")
                .append("  (select sum(rcpc_nr_caixas) from receita_produto_controlado r where r.itpd_cd_item_pedido = ip.itpd_cd_item_pedido) AS quantidade_receita, ")
                .append("  m.medi_tp_prescritor AS tipo_prescritor, ")
                .append("  m.medi_sg_uf_crm AS uf_prescritor, ")
                .append("  pec.chave_medico AS codigo_medico, ")
                .append("  ip.prme_cd_produto AS codigo_produto, ")
                .append("  ip.itpd_cd_item_pedido AS codigo_item, ")
                .append("  IIF(pbm.ippnumautorizpbm is not null, 'TRUE', 'FALSE') AS is_pbm, ")
                .append("  ip.itpd_vl_preco_unitario AS preco_unitario, ")
                .append("  ip.itpd_nr_quantidade_pedida AS quantidade_pedida, ")
                .append("  p.pedi_fl_marketplace AS e_marketplace, ")
                .append("  p.pedi_nr_pedido AS numero_pedido ")
                .append("FROM pagamento_em_convenio pec (NOLOCK) ")
                .append("JOIN item_autorizacao_convenio iac (NOLOCK) ON iac.mdpg_cd_modalidade_pagamento = pec.mdpg_cd_modalidade_pagamento ")
                .append("JOIN item_pedido ip (NOLOCK) ON ip.itpd_cd_item_pedido = iac.itpd_cd_item_pedido ")
                .append("JOIN pedido p (NOLOCK) ON p.pedi_nr_pedido = ip.pedi_nr_pedido ")
                .append("LEFT JOIN autorizadora_pbm a (NOLOCK) ON p.apbm_id_idt = a.apbm_id_idt ")
                .append("LEFT JOIN drgtblippitempedidopbm pbm (NOLOCK) ON pbm.itpd_cd_item_pedido = ip.itpd_cd_item_pedido ")
                .append("LEFT JOIN lksql01.cosmos.dbo.cve_convenio_pbm convpb (NOLOCK) ON convpb.cpbm_cd_convenio_pbm = pbm.cpbm_cd_convenio_pbm ")
                .append("LEFT JOIN lksql01.cosmos.dbo.cve_autorizador_convenio_pbm au (NOLOCK) ON convpb.acpb_cd_autorizador = au.acpb_cd_autorizador ")
                .append("LEFT JOIN receita_produto_controlado rpc (NOLOCK) ON rpc.itpd_cd_item_pedido = ip.itpd_cd_item_pedido ")
                .append("LEFT JOIN medico m (NOLOCK) ON (m.medi_nv_chave = rpc.chave_medico OR m.medi_nv_chave = ip.chave_medico) ")
                .append("WHERE ip.itpd_cd_item_pedido IN :codigosItem");

        List<Tuple> result = em.createNativeQuery(sql.toString(), Tuple.class)
                .setParameter("codigosItem", codigosItem)
                .getResultList();

        if (CollectionUtils.isEmpty(result)) {
            throw new BusinessException("Dados de pagamento em convênio não encontrados para o pedido");
        }

        List<ItemSolicitacaoAutorizacaoConvenioDTO> itens = result.stream()
                .map(tupla -> {
                	Double precoUnitario = Optional.ofNullable(tupla.get("preco_unitario", BigDecimal.class))
                			.map(BigDecimal::doubleValue).orElse(null);
                    
                	ItemSolicitacaoAutorizacaoConvenioDTO item = new ItemSolicitacaoAutorizacaoConvenioDTO();
                    item.setCodigoItem(tupla.get("codigo_item", Integer.class));
                    item.setCodigoProduto(tupla.get("codigo_produto", Integer.class));
                    item.setConvenioPBM(Boolean.parseBoolean(tupla.get("is_pbm", String.class)));
                    item.setQuantidadeSolicitada(tupla.get("quantidade_pedida", Integer.class));
                    item.setQuantidadePrescrita(tupla.get("quantidade_receita", Integer.class));
                    if (item.isConvenioPBM())
                    	item.setValorPrecoConvenioPBM(precoUnitario);
                    
                    boolean isFormula = Constantes.CODIGO_PRODUTO_FORMULA_MANIPULADA.equals(item.getCodigoProduto());
                    item.setFormulaManipulada(isFormula);
                    
                    boolean isMarketplace = 'S' == tupla.get("e_marketplace", Character.class);
                    if (isMarketplace || isFormula)
                    	item.setPrecoUnitario(precoUnitario);
                    
                    if (isMarketplace) {
                    	item.setPrecoDrogatel(true);
                    	item.setValorPrecoDrogatel(precoUnitario);
                    	item.setValorPrecoPMCDrogatel(precoUnitario);
                    }
                    
                    return item;
                })
                .collect(Collectors.toList());
        
        Tuple pedido = result.get(0);

        String codigoTipoPrescritor = String.valueOf(pedido.get("tipo_prescritor", Character.class));
        TipoRegistroPrescritorEnum tipoPrescritor = TipoRegistroPrescritorEnum.getValueByCodigo(codigoTipoPrescritor);

        String usoProlongadoSimNaoStr = String.valueOf(pedido.get("uso_prolongado", Character.class));

        return SolicitacaoAutorizacaoConvenioDTO.builder()
                .numeroPedido(pedido.get("numero_pedido", Integer.class))
                .codigoUsuario(SecurityUtils.getCodigoUsuarioLogado())
                .codigoLoja(idLojaSolicitacao)
                .codigoAutorizadoraPBM(pedido.get("codigo_autorizadora_pbm", String.class))
                .numeroAutorizacaoPBM(pedido.get("numero_autorizacao_pbm", String.class))
                .codigoEmpresaConveniada(pedido.get("codigo_empresa_conveniada", Integer.class))
                .numeroDocumentoIdentificacao(pedido.get("identificacao_conveniado", String.class))
                .valorOpcionalVistaSelecionado(Boolean.parseBoolean(pedido.get("pago_a_vista", String.class)))
                .dataReceita(pedido.get("data_receita", Date.class))
                .numeroReceita(pedido.get("numero_receita", String.class))
                .usoProlongado(SimNaoEnum.getValueByString(usoProlongadoSimNaoStr).booleanValue())
                .tipoPrescritor(tipoPrescritor)
                .ufPrescritor(pedido.get("uf_prescritor", String.class))
                .numeroPrescritor(pedido.get("codigo_medico", Integer.class))
                .nomeDependente(pedido.get("dependente", String.class))
                .itensSolicitacaoAutorizacao(itens)
                .build();
    }

    @Override
    @Transactional
    public void atualizarDadosPagamentoEmConvenio(Long numeroPedido, PagamentoEmConvenioDTO pagamentoEmConvenioDTO) {
        
        StringBuilder updatePagamentoEmConvenio = new StringBuilder()
                .append("UPDATE pagamento_em_convenio SET ")
                .append("  peco_nr_autorizacao = :numeroAutorizacao, ")
                .append("  peco_vl_pago_convenio = :valorPagoConvenio ")
                .append("WHERE mdpg_cd_modalidade_pagamento IN :codigosModalidadesPagamento");
        em.createNativeQuery(updatePagamentoEmConvenio.toString())
                .setParameter("codigosModalidadesPagamento", obterCodigosModalidadesPagamento(numeroPedido))
                .setParameter("numeroAutorizacao", pagamentoEmConvenioDTO.getNumeroAutorizacao())
                .setParameter("valorPagoConvenio", pagamentoEmConvenioDTO.getValorPagoConvenio())
                .executeUpdate();

        StringBuilder updateItemAutorizacaoConvenio = new StringBuilder()
                .append("UPDATE item_autorizacao_convenio SET ")
                .append("  iaco_nr_qtd_autorizada = :quantidadeAutorizada, ")
                .append("  iaco_nr_qtd_solicitada = :quantidadeSolicitada, ")
                .append("  iaco_vl_pago_convenio = :valorPagoConvenio, ")
                .append("  iaco_vl_pago_vista = :valorPagoAVista ")
                .append("WHERE prme_cd_produto = :codigoProduto AND itpd_cd_item_pedido = :codigoItem");
        pagamentoEmConvenioDTO.getItensAutorizacao()
                .forEach(item -> em.createNativeQuery(updateItemAutorizacaoConvenio.toString())
                        .setParameter("codigoProduto", item.getCodigoProduto())
                        .setParameter("codigoItem", item.getCodigoItem())
                        .setParameter("quantidadeAutorizada", item.getQuantidadeAutorizada())
                        .setParameter("quantidadeSolicitada", item.getQuantidadeSolicitada())
                        .setParameter("valorPagoConvenio", item.getValorPagoConvenio())
                        .setParameter("valorPagoAVista", item.getValorPagoAVista())
                        .executeUpdate()
                );
    }
    
    @SuppressWarnings("unchecked")
    private List<Integer> obterCodigosModalidadesPagamento(Long numeroPedido) {
        return em.createNativeQuery(
        		 	new StringBuilder()
        		 	.append("SELECT mp.mdpg_cd_modalidade_pagamento FROM modalidade_pagamento mp (nolock) ")
        		 	.append("WHERE mp.pedi_nr_pedido = :numeroPedido").toString())
                 .setParameter(NUMERO_PEDIDO, numeroPedido)
                 .getResultList();
    }

}
