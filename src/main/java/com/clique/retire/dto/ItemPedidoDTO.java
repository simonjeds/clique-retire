package com.clique.retire.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemPedidoDTO {

	private Integer codigo;
	private Integer quantidadePedida;
	private Integer quantidadeNegociada;
	private Integer quantidadeNegociadaRecebida;
	private Date dataAlteracao;
	private PedidoDTO pedidoDTO;
	private Integer codigoProduto;
	private Integer digitoProduto;
	private String descricaoProduto;
	private String codigosEan;
	private Integer codigoImagemProduto;
	private String urlImagem;
	private String dtValVencCurt;
	private String dtValInformada;
	private String tipoReceita;
	private boolean controlado;
	private boolean antibiotico;
	private Integer quantidadeEstoque;
	private Integer quantidadeSeparada;
	private Integer quantidadeRegistrada;
	private Integer quantidadeDevolvida;

	private String lote;

	private List<String> lotes = new ArrayList<>();

	public String getCodigoProdutoFormatado() {
		return Objects.nonNull(codigoProduto) && Objects.nonNull(digitoProduto)
			? codigoProduto.toString().concat("-").concat(digitoProduto.toString())
			: null;
	}

	public Integer getQuantidadeDisponivelSeparacao() {
		this.quantidadePedida = quantidadePedida == null ? 0 : quantidadePedida;
		this.quantidadeSeparada = quantidadeSeparada == null ? 0 : quantidadeSeparada;

		return quantidadePedida - quantidadeSeparada;
	}

	public void addQuantidadeSeparada(Integer quantidadeSeparada) {
		if (this.quantidadeSeparada == null)
			this.quantidadeSeparada = 0;

		this.quantidadeSeparada += quantidadeSeparada;
	}
}
