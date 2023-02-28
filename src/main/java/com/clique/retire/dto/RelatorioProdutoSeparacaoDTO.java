package com.clique.retire.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class RelatorioProdutoSeparacaoDTO {

	private static final String VACINA = "vacina";
	private static final Integer COD_PRODUTO_VACINA_HEXAVALENTE = 89141;

  private String codigo;
  private String descricao;
  private String descricaoLonga;
  private String codBarras;
  private String quantidade;
  private String estoque;
  private String secao;
  private String numPrePedido;
  private String produtoControlado;
  private String produtoPBM;
  private String produtoFarmaciaPopular;
  private String produtoConvenio;
  private String produtoAntibiotico;
  private String produtoGeladeira;
  private String termolabil;
  private String url;
  private Integer codigoProduto;
  private Date dataValidadePedidoVencCurto;
  private String dataValidadePedidoVencCurtoFormat;
  private boolean modificado;

  @JsonIgnore
  public boolean isVacina() {
  	return (StringUtils.isNotBlank(descricaoLonga) && descricaoLonga.toLowerCase().trim().startsWith(VACINA))
      || COD_PRODUTO_VACINA_HEXAVALENTE.equals(codigoProduto);
  }

  @JsonIgnore
  public boolean isProdutoAntibiotico() {
    return Boolean.parseBoolean(produtoAntibiotico);
  }

  @JsonIgnore
  public boolean isProdutoControlado() {
    return Boolean.parseBoolean(produtoControlado);
  }

  @JsonIgnore
  public boolean isProdutoGeladeira() {
    return "S".equalsIgnoreCase(produtoGeladeira);
  }

  @JsonIgnore
  public void definirPropriedadesEspeciaisProduto(){
    if (this.isProdutoControlado()) {
      this.setSecao("Armário de Controlados");
      if (this.isProdutoAntibiotico()) {
        this.setProdutoControlado("FALSE");
        this.setSecao("Sequência de Antibióticos");
      }
    } else if (this.isVacina()) {
      this.setSecao("Geladeira de Vacinas");
    } else if (this.isProdutoGeladeira()) {
      this.setSecao("Geladeira");
    }
  }
}
