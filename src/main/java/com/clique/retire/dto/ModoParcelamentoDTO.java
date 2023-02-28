package com.clique.retire.dto;

import com.clique.retire.enums.TipoPagamentoEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class ModoParcelamentoDTO {

    private Short codigoCartao;
    private String nomeCartao;
    private TipoPagamentoEnum tipoPagamento;
    private Double valorMinimoParcelamento;
    private Double valorMinimoPorParcela;
    private Integer numeroMaximoParcelas;
    private Integer numeroMaximoParcelasSemJuros;
    private Double valorPercentualJuros;
    private List<ParcelamentoDTO> parcelamentos;

    public Double getValorMinimoParcelamento() {
        return Optional.ofNullable(valorMinimoParcelamento).orElse(0.);
    }

    public Double getValorMinimoPorParcela() {
        return Optional.ofNullable(valorMinimoPorParcela).orElse(0.);
    }

    public Integer getNumeroMaximoParcelas() {
        return Optional.ofNullable(numeroMaximoParcelas).orElse(0);
    }

    public Integer getNumeroMaximoParcelasSemJuros() {
        return Optional.ofNullable(numeroMaximoParcelasSemJuros).orElse(0);
    }

}
