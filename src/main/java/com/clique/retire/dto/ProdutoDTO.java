package com.clique.retire.dto;

import com.clique.retire.enums.TipoProblemaEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoDTO {

	private Long id;
	private Long idProduto;
	private Integer dvProduto;
	private String descricaoProduto;
	private String urlImagem;
	private Integer quantidadePedida;
	private Integer quantidadeEstoque;
	private Integer quantidadeSeparada;
	private Integer quantidadeReceita;
	private String ean;
	private String[] eans;
	private String[] codigosEan;
	private String lote;
	private String idCategoria;
	private String descricaoCategoria;
	private TipoProblemaEnum tipoProblema;
	private Integer quantidadeFalta;
	private String dtValVencCurt;
	private String dtValInformada;
	private String tipoReceita;
	private Integer idTipoReceita;
	private boolean exigeReceita = false;
	private boolean controlado = false;
	private boolean antibiotico = false;
	private boolean geladeira = false;
	private boolean antibioticoUsoProlongado = false;
	private Integer numeroCaixas;
	private Double precoDe;
	private Double precoPor;
	private boolean proximaReceita;

	public Integer getQuantidadeDisponivelSeparacao() {
		this.quantidadePedida = quantidadePedida == null ? 0 : quantidadePedida;
		this.quantidadeSeparada = quantidadeSeparada == null ? 0 : quantidadeSeparada;

		return quantidadePedida - quantidadeSeparada;
	}

	public void addQuantidadeSeparada(Integer quantidadeSeparada) {
		if(this.quantidadeSeparada == null)
			this.quantidadeSeparada = 0;

		this.quantidadeSeparada += quantidadeSeparada;
	}

	public String getCodigoProdutoFormatado() {
		return idProduto.toString().concat("-").concat(dvProduto.toString());
	}
}