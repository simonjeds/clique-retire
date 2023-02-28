package com.clique.retire.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutorizacaoSupervendedorClienteDTO {
	
    @SerializedName("Codigo")
    private Long codigo;

    @SerializedName("Nome")
    private String nome;

    @SerializedName("Matricula")
    private String matricula;

    @SerializedName("Login")
    private String login;

    @SerializedName("CodigoCargo")
    private Long codigoCargo;

    @SerializedName("Cargo")
    private String cargo;

}
