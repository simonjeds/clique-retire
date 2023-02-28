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
public class PBMProdutoResponseDTO {

  @SerializedName("CPF")
  private String CPF;

  @SerializedName("CodProduto")
  private Long codProduto;

  @SerializedName("NumeroAutorizacao")
  private String numeroAutorizacao;

  @SerializedName("DataAutorizacao")
  private Date dataAutorizacao;

  @SerializedName("EAN")
  private String ean;

  @SerializedName("ValorBaseProduto")
  private Double valorBaseProduto;

  @SerializedName("ValorPMC")
  private Double valorPMC;

  @SerializedName("PrecoVenda")
  private Double precoVenda;

  @SerializedName("Pontuacao")
  private Double pontuacao;

  @SerializedName("QuantidadeVendida")
  private Integer quantidadeVendida;

  @SerializedName("PercentualDesconto")
  private Double percentualDesconto;

  @SerializedName("DescontoSobrePreco")
  private String descontoSobrePreco;

  @SerializedName("Status")
  private boolean status;

  @SerializedName("DigitalizacaoReceita")
  private boolean digitalizacaoReceita;

  @SerializedName("Mensagem")
  private String mensagem;

  @SerializedName("Erros")
  private List<PBMAutorizadorErrorDTO> erros;

}