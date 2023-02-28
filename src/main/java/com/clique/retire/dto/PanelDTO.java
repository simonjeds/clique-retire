package com.clique.retire.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PanelDTO {

	private Integer pedido;
    private String polo;
    private String cdPolo;
   
    private String tipoPedido;
    private String detalheErro;
    private String faseProcesso;
    private String situacaoPedido;
    private String faseProcessoCD;
    private String tipoFinalizador;
    private String processamentoSAP;

    private LocalDateTime dataAlteracaoFase;
    private LocalDateTime dataUltimaExecucao;
    
    private String tipoEntrega;
}
