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
public class ClienteCaptacaoDTO {

	@SerializedName(value = "Endereco")
	private EnderecoClienteDTO enderecoClienteDTO;

	@SerializedName(value = "Id")
	private Integer id;

	@SerializedName(value = "Cpf")
	private String cpf;

	@SerializedName(value = "Nome")
	private String nome;

	@SerializedName(value = "DataNascimento")
	private Date dataNascimento;

	@SerializedName(value = "Sexo")
	private String sexo;

	@SerializedName(value = "Documento")
	private String documento;

	@SerializedName(value = "TipoDocumento")
	private Integer tipoDocumento;

	@SerializedName(value = "EstadoDocumento")
	private String estadoDocumento;

	@SerializedName(value = "OrgaoDocumento")
	private String orgaoDocumento;

	@SerializedName(value = "Telefone1")
	private String telefone1;

	@SerializedName(value = "Telefone2")
	private String telefone2;

	@SerializedName(value = "Email")
	private String email;

	@SerializedName(value = "Versao")
	private Integer versao;

	@SerializedName(value = "PermiteTemMaisAraujo")
	private Boolean permiteTemMaisAraujo;

}
