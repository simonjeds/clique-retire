package com.clique.retire.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
public class CaptacaoIntegracaoDTO {

	private String dataEmissao;
	private String documento;
	private String tipo;
	private String orgao;
	private String uf;
	private String tipoReceita;
	private String numeroAutorizacao;
	private String nome;
	private String sexo;
	private Integer idade;
	private String tipoIdade;
	private String registro;
	private List<ItensCaptacaoIntegracaoDTO> itens;

}
