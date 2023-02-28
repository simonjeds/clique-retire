package com.clique.retire.enums;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.clique.retire.infra.exception.BusinessException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoPagamentoEnum {

    ROTATIVO("01", "ROTATIVO"),
    PARCELADO_ARAUJO("02","PARCELADO ARAÚJO"),
    PARCELADO_ADMINISTRADORA("03","PARCELADO ADMINISTRADORA"),
    A_VISTA("04","À VISTA"),
    PRE_DATADO("05","PRÉ-DATADO"),
    PRE_DATADO_PARCELADO("06","PRÉ-DATADO PARCELADO"),
    DINHEIRO("07","DINHEIRO"),
    CONVENIO("08","CONVÊNIO"),
    PARCELADO_SUPER_NOSSO("09","PARCELADO SUPER NOSSO"),
    BOLETO_BANCARIO("10","BOLETO BANCÁRIO"),
    PARCELADO_ARAUJO_ESPECIAL("11","PARCELADO ARAUJO ESPECIAL"),
    VALE("12","VALE"),
    DEPOSITO_BANCARIO("13","DEPÓSITO BANCÁRIO"),
    PAGAMENTO_ANTECIPADO("14","PAGAMENTO ANTECIPADO"),
    DEBITO("15","DÉBITO"),
    MARKETPLACE("16","MARKETPLACE"),
    PAGAMENTO_ANTECIPADO_PIX("17","PAGAMENTO ANTECIPADO PIX"),
    PIX("18","PIX"),
    ORDEM_DE_TROCA("20","ORDEM DE TROCA");

    private final String codigo;
    private final String descricao;

    public static TipoPagamentoEnum porCodigo(String codigo) {
        return Arrays.stream(TipoPagamentoEnum.values())
                .filter(tipo -> tipo.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Tipo de pagamento não identificado."));
    }

    public static boolean isPagamentoAntecipado(List<TipoPagamentoEnum> tiposPagamento) {
        return CollectionUtils.isNotEmpty(tiposPagamento) && Arrays.asList(
                BOLETO_BANCARIO, DEPOSITO_BANCARIO, PIX, PAGAMENTO_ANTECIPADO, PAGAMENTO_ANTECIPADO_PIX
        ).containsAll(tiposPagamento);
    }

    public static boolean isPagamentosNoCredito(List<TipoPagamentoEnum> tiposPagamento) {
        return CollectionUtils.isNotEmpty(tiposPagamento) && Arrays.asList(
                ROTATIVO, PARCELADO_ARAUJO, PARCELADO_ADMINISTRADORA, PARCELADO_ARAUJO_ESPECIAL
        ).containsAll(tiposPagamento);
    }

    public static boolean isPagamentosComConvenio(List<TipoPagamentoEnum> tiposPagamento) {
        return CollectionUtils.isNotEmpty(tiposPagamento) && Arrays.asList(
                CONVENIO, MARKETPLACE
        ).containsAll(tiposPagamento);
    }

    public static boolean isPagamentosNoCreditoOuConvenio(List<TipoPagamentoEnum> tiposPagamento) {
        return isPagamentosNoCredito(tiposPagamento) || isPagamentosComConvenio(tiposPagamento);
    }

}
