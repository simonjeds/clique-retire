package com.clique.retire.model.drogatel;

import javax.persistence.*;

import com.clique.retire.enums.SimNaoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name="ITEM_PEDIDO")
public class ItemPedido extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="itpd_cd_item_pedido")
	@EqualsAndHashCode.Include
	private Integer codigo;

	@ManyToOne
	@JoinColumn(name="pedi_nr_pedido")
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name="prme_cd_produto")
	private Produto produto;

	@Column(name="pedi_nr_pedido", insertable = false, updatable = false )
	private Long numeroPedido;
	
	@Column(name="prme_cd_produto", insertable = false, updatable = false )
	private Long codigoProduto;
	
	@Column(name = "ITPD_FL_ITEM_REGISTRADO")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum itemRegistrado;

	@Column(name = "ITPD_FL_PRODUTO_CONTROLADO")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum produtoControlado;

	@Column(name = "ITPD_FL_PRODUTO_ANTIBIOTICO")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum produtoAntibiotico;

	@Column(name = "itpd_nr_quantidade_pedida")
	private Integer quantidadePedida;

	@Column(name = "itpd_nr_quantidade_separada")
	private Integer quantidadeSeparada;

	@Column(name = "itpd_nr_quantidade_negociada")
	private Integer quantidadeNegociada;

	@Column(name = "itpd_nr_quantidade_negociada_recebida")
	private Integer quantidadeNegociadaRecebida;

	@Column(name = "itpd_nr_quantidade_registrada")
	private Integer quantidadeRegistrada;

	@Column(name = "itpd_nr_quantidade_devolvida")
	private Integer quantidadeDevolvida;

	@Column(name = "ITPD_VL_PRECO_UNITARIO")
	private Double precoUnitario;
	
	@Column(name = "ITPD_VL_PRECO_VENDA")
	private Double precoVenda;

	@Column(name = "SKVCODKITVIRTUAL")
	private String kit;

	@Column(name = "SKVSEQVENDAKIT")
	private Integer sequencialKit;

	@OneToOne(mappedBy = "itemPedido")
	private ItemPedidoPBM itemPedidoPBM;

	public ItemPedido(String codigoUsuario) {
		super(codigoUsuario);
	}

	public int getQuantidadeARegistrar() {
		if ((quantidadeSeparada + quantidadeNegociadaRecebida) > quantidadePedida) {
			return quantidadePedida - (quantidadeRegistrada + quantidadeDevolvida);
		}
		return (quantidadeSeparada + quantidadeNegociadaRecebida) - (quantidadeRegistrada + quantidadeDevolvida);
	}

	public void addQuantidadeNegociadaRecebida(Integer quantidadeNegociadaRecebida) {
		this.quantidadeNegociadaRecebida = this.quantidadeNegociadaRecebida + quantidadeNegociadaRecebida;
	}
	
	public int getQuantidadeFaltaSeparar() {
		return (quantidadePedida - quantidadeNegociada);
	}
	
	public boolean isSeparacaoCompleta() {
		int separada = quantidadeSeparada == null ? 0 : quantidadeSeparada;
		int pedida = quantidadePedida == null ? 0 : quantidadePedida;
		
		return pedida <= separada;
	}	

	public boolean isReceitaControlada(Integer quantidade) {
		int separada = quantidade == null ? 0 : quantidade;
		int pedida = quantidadePedida == null ? 0 : quantidadePedida;
		
		return pedida <= separada;
	}	

}
