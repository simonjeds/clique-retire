package com.clique.retire.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
public class EntradaDTO {

	@SerializedName(value = "CodigoAutorizacaoExterna")
	private String codigoAutorizacaoExterna;

	@SerializedName(value = "CodigoFilial")
	private Integer codigoFilial;

	@SerializedName(value = "Receita")
	private ReceitaDTO receitaDTO;

	@SerializedName(value = "Cliente")
	private ClienteCaptacaoDTO clienteDTO;

	@SerializedName(value = "Paciente")
	private PacienteDTO pacienteDTO;

	@SerializedName(value = "Gbm")
	private GbmDTO gbmDTO;

	@SerializedName(value = "Produtos")
	private List<ProdutoEntradaDTO> produtoEntradaDTO;

}
