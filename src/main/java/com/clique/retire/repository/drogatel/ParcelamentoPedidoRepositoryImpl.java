package com.clique.retire.repository.drogatel;

import com.clique.retire.dto.ModoParcelamentoDTO;
import com.clique.retire.enums.TipoPagamentoEnum;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Repository
public class ParcelamentoPedidoRepositoryImpl {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<ModoParcelamentoDTO> buscarParcelamentosDisponiveisProCartaoDoPedido(Long numeroPedido) {
        StringBuilder sql = new StringBuilder()
                .append("SELECT ")
                .append("  cc.cacr_sq_cartao AS codigo_cartao, ")
                .append("  cc.cacr_nm_cartao AS nome_cartao, ")
                .append("  tp.tppg_fl_tipo AS tipo_pagamento, ")
                .append("  tp.tppg_vl_minimo_parcelamento AS valor_minimo_parcelamento, ")
                .append("  tp.tppg_vl_minimo_por_parcela AS valor_minimo_por_parcela, ")
                .append("  tp.tppg_nr_max_parcelas AS numero_maximo_parcelas, ")
                .append("  tp.tppg_nr_max_parcelas_sem_juros AS numero_maximo_parcelas_sem_juros, ")
                .append("  v.vans_pr_txadmin AS valor_percentual_juros ")
                .append("FROM tipo_pagamento tp (NOLOCK) ")
                .append("JOIN tipo_finalizador tf (NOLOCK) ON tf.tpfi_cd_tp_flz = tp.tpfi_cd_tp_flz ")
                .append("JOIN cartao_credito cc (NOLOCK) ON cc.cacr_sq_cartao = tp.cacr_sq_cartao ")
                .append("JOIN van v (NOLOCK) ON v.vans_cd_van = cc.vans_cd_van ")
                .append("WHERE cc.cacr_sq_cartao IN (")
                .append("  SELECT cc2.cacr_sq_cartao FROM pedido p (NOLOCK) ")
                .append("  JOIN modalidade_pagamento mp (NOLOCK) ON mp.pedi_nr_pedido = p.pedi_nr_pedido ")
                .append("  JOIN tipo_pagamento tp2 (NOLOCK) ON tp2.tppg_cd_tipo_pagamento = mp.tppg_cd_tipo_pagamento ")
                .append("  JOIN cartao_credito cc2 (NOLOCK) ON cc2.cacr_sq_cartao = tp2.cacr_sq_cartao ")
                .append("  WHERE p.pedi_nr_pedido = :numeroPedido ")
                .append(")");

        List<Tuple> result = em.createNativeQuery(sql.toString(), Tuple.class)
                .setParameter("numeroPedido", numeroPedido)
                .getResultList();

        ToDoubleFunction<BigDecimal> bigDecimalToDouble =
                value -> Optional.ofNullable(value).map(BigDecimal::doubleValue).orElse(0.);

        return result.stream().map(tupla -> {
            BigDecimal valorMinimoParcelamento = tupla.get("valor_minimo_parcelamento", BigDecimal.class);
            BigDecimal valorMinimoPorParcela = tupla.get("valor_minimo_por_parcela", BigDecimal.class);
            BigDecimal valorPercentualJuros = tupla.get("valor_percentual_juros", BigDecimal.class);

            ModoParcelamentoDTO modo = new ModoParcelamentoDTO();
            modo.setCodigoCartao(tupla.get("codigo_cartao", Short.class));
            modo.setNomeCartao(tupla.get("nome_cartao", String.class));
            modo.setTipoPagamento(TipoPagamentoEnum.porCodigo(tupla.get("tipo_pagamento", String.class)));
            modo.setValorMinimoParcelamento(bigDecimalToDouble.applyAsDouble(valorMinimoParcelamento));
            modo.setValorMinimoPorParcela(bigDecimalToDouble.applyAsDouble(valorMinimoPorParcela));
            modo.setNumeroMaximoParcelas(tupla.get("numero_maximo_parcelas", Integer.class));
            modo.setNumeroMaximoParcelasSemJuros(tupla.get("numero_maximo_parcelas_sem_juros", Integer.class));
            modo.setValorPercentualJuros(bigDecimalToDouble.applyAsDouble(valorPercentualJuros));
            return modo;
        }).collect(Collectors.toList());
    }

}
