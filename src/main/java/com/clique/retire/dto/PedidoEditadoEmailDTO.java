package com.clique.retire.dto;

import com.clique.retire.enums.TipoEdicaoPedido;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoEditadoEmailDTO {

    private String emailCliente;
    private Long numeroPedido;
    private String numeroPedidoEcommerce;
    private TipoEdicaoPedido tipoNotificacao;
    private List<ItemEmailDTO> itensPedido;
    private List<ItemEmailDTO> itensRemovidos;
    private PagamentoComunicacaoDTO pagamento;

}