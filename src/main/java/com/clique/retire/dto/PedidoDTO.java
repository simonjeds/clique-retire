package com.clique.retire.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.TipoPedidoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PedidoDTO {

	private Long id;
	private Long idUsuario;
	private Integer filial;
	private Integer numeroPedido;
	private Integer codFilial;
	private Double totalItensPedido;
	private Double totalPedido;
	private Double valorTotalComJuros;
	private Double valorTotalSemJuros;
	private SimNaoEnum pedidoLoja;
	private TipoPedidoEnum tipoPedido;
	private List<ProdutoDTO> itensPedido = new ArrayList<>();
	private List<ReceitaProdutoControladoDTO> receitasPedido = new ArrayList<>();
	private List<ItemPedidoDTO> itens;
	private FasePedidoEnum fase;
	private boolean araujoTem;
	private Integer idNotaFiscal;
	private Integer tipoPagamento;
	private Integer codigoPolo;
	private boolean pedidoSuperVendedor;
	private String nomeCliente;
	private String nomeUsuario;
	private String descricaoFase;
	private Date data;
	private String chaveNotaFiscal;
	
	private boolean contemControlado;
	
	private Long numeroPedidoEcommerce;
	private String numeroPedidoEcommerceCliente;
	
	private boolean emitirNota = false;
	private boolean hasApontamentoFaltaAraujoTem;
	private String prePedido;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Sao_Paulo")
	private Date dataAssociacao;
	
	public boolean isEcommerceAppCancelado() {
		return (FasePedidoEnum.CANCELADO.equals(fase)
				&& (TipoPedidoEnum.APLICATIVO.equals(tipoPedido) 
						|| TipoPedidoEnum.E_COMMERCE.equals(tipoPedido)));

	}

	public boolean hasItemControlado() {
		return itens.stream().anyMatch(ItemPedidoDTO::isControlado)
			|| itensPedido.stream().anyMatch(ProdutoDTO::isControlado);
	}

}
