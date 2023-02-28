package com.clique.retire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoEmConvenioDTO {

    private Integer numeroAutorizacao;
    private Double valorPagoConvenio;
    private Double valorPagoAVista;
    private List<ItemAutorizacaoConvenioDTO> itensAutorizacao;

}
