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
public class PrescritorDTO {

	@SerializedName("Id")
	private Integer id;

	@SerializedName("TipoRegistro")
	private Integer tipoRegistro;

	@SerializedName("NumeroRegistro")
	private Integer numeroRegistro;

	@SerializedName("EstadoRegistro")
	private String estadoRegistro;

	@SerializedName("Nome")
	private String nome;

	@SerializedName("Endereco")
	private EnderecoClienteDTO enderecoPrescritorDTO;

}
