package com.clique.retire.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "lote", "codigoImagemProduto" })
public class ItemPedidoRetornoMotociclistaDTO implements UrlImagemDTO {

  private String urlImagem;
  private String[] codigosEan;
  private Integer codigoItem;
  private Integer codigoProduto;
  private Integer digitoProduto;
  private String descricao;
  private String tipoReceita;
  private Integer quantidadeNota;
  @Builder.Default
  private Integer quantidadeBipada = 0;
  private boolean antibiotico;
  @Builder.Default
  private List<LoteBipadoDTO> lotes = new ArrayList<>();

  private String lote;
  private Integer codigoImagemProduto;

  public String getCodigoProdutoFormatado() {
    return codigoProduto + "-" + digitoProduto;
  }

}
