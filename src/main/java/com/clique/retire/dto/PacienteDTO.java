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
public class PacienteDTO {

	@SerializedName(value = "Id")
	private Integer id;

	@SerializedName(value = "Cpf")
	private String cpf;

	@SerializedName(value = "DataNascimento")
	private Date dataNascimento;

	@SerializedName(value = "Nome")
	private String nome;

	@SerializedName(value = "Sexo")
	private String sexo;

	@SerializedName(value = "Idade")
	private Integer idade;

	@SerializedName(value = "TipoIdade")
	private String tipoIdade;

	@SerializedName(value = "CodigoPatologia")
	private String codigoPatologia;

}
