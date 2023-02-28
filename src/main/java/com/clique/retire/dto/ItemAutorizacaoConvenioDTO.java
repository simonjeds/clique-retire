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
public class ItemAutorizacaoConvenioDTO {

    private Integer codigoItem;
    private Integer codigoProduto;
    private Integer quantidadeAutorizada;
    private Integer quantidadeSolicitada;
    private Double valorPagoConvenio;
    private Double valorPagoAVista;
    private Double precoVenda;
    private Double precoUnitario;

}
