package com.clique.retire.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImpressaoDTO implements Serializable{
	private static final long serialVersionUID = -8759570295246129813L;
	
	private String servidor;
	private String nomeImpressora;
	private String documento;
	private List<String> receitaDigital;
	private boolean erroImpressao;
	private boolean frenteVerso = true;
	
	private boolean falhaSinalizacaoZeroBalcao;
}
