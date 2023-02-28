package com.clique.retire.dto;

import com.clique.retire.util.NumberUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParcelamentoDTO {

    private int numeroParcelas;
    private double valorParcela;
    private boolean possuiJuros;

    public double getValorTotalPedido() {
        return NumberUtil.round(numeroParcelas * valorParcela, 2);
    }

}
