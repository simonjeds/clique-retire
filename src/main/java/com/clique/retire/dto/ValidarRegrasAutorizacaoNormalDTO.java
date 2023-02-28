package com.clique.retire.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "validarRegrasAutorizacaoNormal")
public class ValidarRegrasAutorizacaoNormalDTO {

    @JacksonXmlProperty(localName = "arg0")
    private SolicitacaoAutorizacaoConvenioDTO solicitacao;

}
