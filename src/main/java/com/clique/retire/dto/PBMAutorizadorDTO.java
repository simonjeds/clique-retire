package com.clique.retire.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PBMAutorizadorDTO {

  @SerializedName("Id")
  private Long id;

  @SerializedName("CodFilial")
  private Integer idLoja;

  @SerializedName("CodCanalDeVenda")
  @Setter(AccessLevel.PRIVATE)
  private Integer codigoCanalDeVenda = 16;

  @SerializedName("Cliente")
  private PBMClienteDTO cliente;

  @SerializedName("Produtos")
  private List<PBMProdutoDTO> produtos = new ArrayList<>();

}
