package com.clique.retire.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PBMPrescritorDTO {

  private Long id;

  @SerializedName("UF")
  private String uf;

  @SerializedName("TipoConselho")
  private Integer tipoConselho;

  @SerializedName("NumeroRegistroConselho")
  private Integer numeroRegistroConselho;

  @SerializedName("NomeProfissionalSaude")
  private String nomeProfissionalSaude;

  @SerializedName("DataReceita")
  private String dataReceita;

//  Campo utilizado para o endpoint prescritores-usuarios-clientes (post e get)
//  private EnderecoDTO endereco

  public PBMPrescritorDTO(PrescritorDTO prescritor) {
    uf = prescritor.getEstadoRegistro();
    tipoConselho = prescritor.getTipoRegistro();
    numeroRegistroConselho = prescritor.getNumeroRegistro();
    nomeProfissionalSaude = prescritor.getNome();
  }

}
