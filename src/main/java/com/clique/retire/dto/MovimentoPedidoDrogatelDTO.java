package com.clique.retire.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentoPedidoDrogatelDTO {

    private Integer numeroPedido;
    private Long numeroPedidoEcommerce;
    private String numeroPedidoEcommerceCliente;
    private String dataModificacao;
    private String fasePedido;
    private List<MovimentoPedidoItemDrogatelDTO> itens;
    private Double valorTotal;
    private Double valorFrete;

}