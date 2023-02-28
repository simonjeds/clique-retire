package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AutorizacaoResponseDTO {
	
    @SerializedName("Data")
    private AutorizacaoSupervendedorClienteDTO autorizacaoClienteResponse;
}
