package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="ITEM_PEDIDO_MERCADORIA")
public class ItemPedidoMercadoria extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="itpm_cd_item_pedido_mercadoria")
    private Integer codigo;

	@ManyToOne
	@JoinColumn(name="pdmc_cd_pedido_mercadoria", insertable = true, updatable = false)
    private PedidoMercadoria pedidoMercadoria;
	
	@OneToOne
	@JoinColumn(name="itpd_cd_item_pedido")
    private ItemPedido itemPedido;
	
	@Column(name="itpm_nr_quantidade_enviada")
    private Integer quantidadeEnviada;
	
	@Column(name="itpm_nr_quantidade_pedida")
    private Integer quantidadePedida;
	
	@Column(name="itpm_nr_quantidade_recebida")
    private Integer quantidadeRecebida;
	
	/**
	 * Usado para agrupar os ItensMercadorias por Pedido
	 * @return
	 */
	@Transient
	public Pedido getPedido() {
		return itemPedido.getPedido();
	}
	
}
