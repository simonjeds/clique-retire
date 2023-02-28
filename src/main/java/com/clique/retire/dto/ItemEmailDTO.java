package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemEmailDTO {

    private Long codigoProduto;
    private Double preco;
    private String descricao;
    private Integer quantidade;
    private String primeiraLetra;
    private Boolean maisQueUm;
    private String urlImagemProduto;
    private Long quantidadeTotalItens;

}