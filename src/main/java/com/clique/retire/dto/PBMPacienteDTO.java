package com.clique.retire.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PBMPacienteDTO {

  @SerializedName("NomeCompleto")
  private String nomeCompleto;

  @SerializedName("DataNascimento")
  private String dataNascimento;

  @SerializedName("Sexo")
  private String sexo;

  @SerializedName("Titular")
  private String titular = "true";

  @SerializedName("CPF")
  private String cpf;

}