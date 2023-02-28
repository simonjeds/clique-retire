package com.clique.retire.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidarRegrasAutorizacaoNormalResponseDTO {

    @JacksonXmlProperty(localName = "return")
    private ConvenioResponse convenioResponse;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConvenioResponse {

        @JacksonXmlElementWrapper(useWrapping = false)
        private List<ItemSolicitacaoAutorizacaoConvenioDTO> itensAutorizacao;
        private String numeroAutorizacao;
        private Double valorTotalPagoAVista;
        private Double valorTotalPedido;

    }

}


