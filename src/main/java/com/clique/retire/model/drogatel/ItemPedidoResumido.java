package com.clique.retire.model.drogatel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
@Table(name = "ITEM_PEDIDO")
public class ItemPedidoResumido implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "itpd_cd_item_pedido")
	private Long codigo;

	@ManyToOne
	@JoinColumn(name = "pedi_nr_pedido")
	private PedidoResumido pedido;

	@ManyToOne
	@JoinColumn(name="prme_cd_produto")
    private Produto produto;
	
	@Column(name = "ITPD_NR_QUANTIDADE_PEDIDA")
	private Integer quantidadePedida;
	
	@Transient
	private int quantidadeBipada;

}
