package com.clique.retire.dto;

import java.util.Date;

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
public class ReceitaDTO {
	
	@SerializedName(value = "Id")
	private Integer id;

	@SerializedName(value = "IdTipoReceita")
	private Integer idTipoReceita;

	@SerializedName(value = "IdCliente")
	private Integer idCliente;

	@SerializedName(value = "NumeroSerie")
	private String numeroSerie;

	@SerializedName(value = "DataEmissao")
	private Date dataEmissao;

	@SerializedName(value = "DataValidade")
	private Date dataValidade;

	@SerializedName(value = "DataUltimaCompra")
	private Date dataUltimaCompra;

	@SerializedName(value = "NumeroCaptacaoCsmMovLoja")
	private String numeroCaptacaoCsmMovLoja;

	@SerializedName(value = "NumeroAutorizacaoFP")
	private String numeroAutorizacaoFP;

	@SerializedName(value = "DescricaoTipoReceita")
	private String descricaoTipoReceita;

	@SerializedName(value = "UsoProlongado")
	private Boolean usoProlongado;
	
	@SerializedName(value = "Prescritor")
	private PrescritorDTO prescritorDTO;

}
