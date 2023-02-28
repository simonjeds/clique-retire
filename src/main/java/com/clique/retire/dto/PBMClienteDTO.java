package com.clique.retire.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PBMClienteDTO {

  @SerializedName("ID")
  private Long id;

  @SerializedName("CPF")
  private String cpf;

  @SerializedName("NomeCompleto")
  private String nomeCompleto;

  @SerializedName("DataNascimento")
  private String dataNascimento;

  @SerializedName("Sexo")
  private String sexo;

  @SerializedName("CEP")
  private String cep;

  @SerializedName("TipoLogradouro")
  private String tipoLogradouro;

  @SerializedName("Logradouro")
  private String logradouro;

  @SerializedName("Numero")
  private Integer numero;

  @SerializedName("Complemento")
  private String complemento;

  @SerializedName("Bairro")
  private String bairro;

  @SerializedName("Cidade")
  private String cidade;

  @SerializedName("UF")
  private String uf;

  @SerializedName("TelefoneResidencial")
  private String telefoneResidencial;

  @SerializedName("Celular")
  private String celular;

  @SerializedName("TelefoneComercial")
  private String telefoneComercial;

  @SerializedName("Email")
  private String email;

  @SerializedName("PermiteUsoDeDadosPorTerceiros")
  private Boolean permiteUsoDeDadosPorTerceiros;

  @SerializedName("PermiteReceberMaterial")
  private Boolean permiteReceberMaterial;

  @SerializedName("PermiteContatoViaEmail")
  private Boolean permiteContatoViaEmail;

  @SerializedName("PermiteContatoViaSMS")
  private Boolean permiteContatoViaSMS;

  @SerializedName("PermiteContatoViaTelefone")
  private Boolean permiteContatoViaTelefone;

  @SerializedName("PermiteContatoViaCorreios")
  private Boolean permiteContatoViaCorreios;

}
