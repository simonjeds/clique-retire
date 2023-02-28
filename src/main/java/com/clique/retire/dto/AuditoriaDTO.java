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
public class AuditoriaDTO {

	@SerializedName(value = "CodigoUsuario")
	private String codigoUsuario;

	@SerializedName(value = "CodigoEstacao")
	private String codigoEstacao;

}
