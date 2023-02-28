package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PBMProdutoDTO {

  @SerializedName("CodProduto")
  private Long codigo;

  @SerializedName("Quantidade")
  private Integer quantidade;

  @SerializedName("PrecoBrutoEstabelecimento")
  private Double precoBrutoEstabelecimento;

  @SerializedName("PrecoLiquidoEstabelecimento")
  private Double precoLiquidoEstabelecimento;

  @SerializedName("EAN")
  private String ean;

  @SerializedName("CodigoPatologia")
  private Integer codigoPatologia = 1;

  @SerializedName("TipoDesconto")
  private Integer tipoDesconto;

  @SerializedName("CodAutorizadora")
  private String codigoAutorizadora;

  @SerializedName("Prescritor")
  private PBMPrescritorDTO prescritor;

  @SerializedName("Paciente")
  private PBMPacienteDTO paciente;

}
