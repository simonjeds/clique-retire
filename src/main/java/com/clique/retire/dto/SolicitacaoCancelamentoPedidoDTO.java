package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitacaoCancelamentoPedidoDTO {

    private String matricula;
    private String senha;
    private String motivo;
    private Integer numeroPedido;

}
