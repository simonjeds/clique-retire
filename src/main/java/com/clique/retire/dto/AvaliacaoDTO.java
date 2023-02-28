package com.clique.retire.dto;

import com.clique.retire.enums.AvaliacaoEnum;

import lombok.Data;

@Data
public class AvaliacaoDTO{

	private AvaliacaoEnum avaliacaoEnum;
    private Integer quantidadeSeparacoesRealizadas;
    private Integer quantidadeControladosRealizados;
    private Integer quantidadeEntregasRealizadas;
    private Integer quantidadeApontamentosFaltasRealizadas;
    private Integer quantidadeRecebimentosMercadoriasRealizadas;
    private Integer quantidadeFluxosExperidadosRealizados;
    
    public String toString() {
    	return "quantidadeSeparacoesRealizadas " + quantidadeSeparacoesRealizadas +
    			"\nquantidadeControladosRealizados " + quantidadeControladosRealizados +
    			"\nquantidadeEntregasRealizadas " + quantidadeEntregasRealizadas +
    			"\nquantidadeApontamentosFaltasRealizadas " + quantidadeApontamentosFaltasRealizadas +
    			"\nquantidadeRecebimentosMercadoriasRealizadas " + quantidadeRecebimentosMercadoriasRealizadas +
    			"\nquantidadeFluxosExperidadosRealizados " + quantidadeFluxosExperidadosRealizados;
    }
	
}
