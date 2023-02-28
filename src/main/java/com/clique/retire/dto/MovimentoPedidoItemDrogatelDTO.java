package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentoPedidoItemDrogatelDTO {

    private Integer codigoProduto;
    private Integer quantidade;
    private Double valorUnitario;

}