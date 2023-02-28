package com.clique.retire.dto;

import com.clique.retire.enums.TipoRegistroPrescritorEnum;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "arg0")
public class SolicitacaoAutorizacaoConvenioDTO {

    private boolean aplicarRegraSaldoVirtual;
    private String codigoAutorizadoraPBM;
    private String numeroAutorizacaoPBM;
    private Integer codigoEmpresaConveniada;
    private Integer codigoLoja;
    private Integer codigoUsuario;
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ItemSolicitacaoAutorizacaoConvenioDTO> itensSolicitacaoAutorizacao;
    private String nomeDependente;
    private String numeroDocumentoIdentificacao;
    private String numeroOrdemDeCompra;
    private Integer numeroPedido;
    private Integer numeroPreAutorizacao;
    private Integer numeroPrescritor;
    private String numeroReceita;
    private Date dataReceita;
    private TipoRegistroPrescritorEnum tipoPrescritor;
    private String ufPrescritor;
    private Boolean usoProlongado;
    @Builder.Default
    private String tipoDocumentoIdentificacao = "CARTAO";
    private boolean valorOpcionalVistaSelecionado;
    private Double valorOrdemDeCompra;

}
