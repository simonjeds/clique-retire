package com.clique.retire.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemNotaFiscalDTO {

	private Integer codigoItemPedido;
	private Integer codigoProduto;
	private Integer digitoProduto;
	private String descricaoProduto;
	private String codigosEan;
	private Integer quantidade;
	private Integer codigoImagemProduto;
	private String urlImagem;
	private String tipoReceita;
	private boolean controlado;
	private String lote;
	private Double preco;
	private List<LoteBipadoDTO> lotes = new ArrayList<>();
	
	public String getCodigoProdutoFormatado() {
		return codigoProduto.toString().concat("-").concat(digitoProduto.toString());
	}
}

