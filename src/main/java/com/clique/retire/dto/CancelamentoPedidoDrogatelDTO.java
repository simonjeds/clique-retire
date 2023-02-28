package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelamentoPedidoDrogatelDTO {

    private Integer numeroPedido;
    private String descricaoMotivoCancelamento;
    private Long codigoMotivoCancelamento;
    private Integer matriculaResponsavel;

}