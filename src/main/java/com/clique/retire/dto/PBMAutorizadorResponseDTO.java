package com.clique.retire.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PBMAutorizadorResponseDTO {

  @SerializedName("CPF")
  private String cpf;

  @SerializedName("StatusAutorizacao")
  private Boolean statusAutorizacao;

  @SerializedName("DigitalizacaoReceita")
  private Boolean digitalizacaoReceita;

  @SerializedName("NumeroSequencial")
  private Integer numeroSequencial;

  @SerializedName("DataHoraAutorizacao")
  private Date dataHoraAutorizacao;

  @SerializedName("ValorTotalAutorizacao")
  private Double valorTotalAutorizacao;

  @SerializedName("ValorTotalPMC")
  private Double valorTotalPMC;

  @SerializedName("ValorTotalDesconto")
  private Double valorTotalDesconto;

  @SerializedName("Produtos")
  private List<PBMProdutoResponseDTO> produtos;

  @SerializedName("IsValid")
  private Boolean valid;

  @SerializedName("Message")
  private String message;

  @SerializedName("Errors")
  private List<PBMAutorizadorErrorDTO> errors;

  private Boolean beneficiarioCadastrado;

  private PBMAutorizadorDTO PBMAutorizadorDTO;

}
