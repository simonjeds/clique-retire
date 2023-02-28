package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelamentoPedidoControladoDTO {

    private boolean pagamentoAntecipado;
    private boolean matriculaValida;
    private RespostaPedidoDrogatelDTO pedidoCanceladoDTO;

}