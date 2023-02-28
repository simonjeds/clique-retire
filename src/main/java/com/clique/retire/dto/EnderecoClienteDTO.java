package com.clique.retire.dto;

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
public class EnderecoClienteDTO {
	
	@SerializedName(value = "TipoLogradouro")
	private String tipoLogradouro;

	@SerializedName(value = "Cep")
	private String cep;

	@SerializedName(value = "Logradouro")
	private String logradouro;

	@SerializedName(value = "Numero")
	private Integer numero;

	@SerializedName(value = "Complemento")
	private String complemento;

	@SerializedName(value = "Bairro")
	private String bairro;

	@SerializedName(value = "Cidade")
	private String cidade;

	@SerializedName(value = "CidadeNome")
	private String cidadeNome;

	@SerializedName(value = "Estado")
	private String estado;

}
