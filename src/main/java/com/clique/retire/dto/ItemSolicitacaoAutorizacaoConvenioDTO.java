package com.clique.retire.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSolicitacaoAutorizacaoConvenioDTO {

    private Integer codigoItem;
    private Integer codigoProduto;
    private boolean convenioPBM;
    private boolean formulaManipulada;
    private Double precoUnitario;
    private boolean produtoBonus;
    private Integer quantidadePrescrita;
    private Integer quantidadeSolicitada;
    private Double valorPrecoConvenioPBM;
    private boolean precoDrogatel;
    private Double valorPrecoDrogatel;
    private Double valorPrecoPMCDrogatel;

    // Response
    private Double precoUnitarioPagoAVista;
    private Double precoVenda;
    private Integer quantidadeAprovada;
    private String descricao;

    // Business attribute
    private boolean itemUsado;

}
