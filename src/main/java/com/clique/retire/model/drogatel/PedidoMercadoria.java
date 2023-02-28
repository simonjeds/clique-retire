package com.clique.retire.model.drogatel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.converter.FasePedidoEnumConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "PEDIDO_MERCADORIA")
public class PedidoMercadoria extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "pdmc_cd_pedido_mercadoria")
	private Integer codigo;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pedidoMercadoria", cascade = CascadeType.ALL)
	private List<ItemPedidoMercadoria> listItemPedidoMercadoria = new ArrayList<>();

	@OneToOne(mappedBy="pedidoMercadoria")
	private ExpedicaoPedido expedicaoPedido;

	@Convert(converter = FasePedidoEnumConverter.class)
	@Column(name = "pdmc_fl_fase")
	private FasePedidoEnum fasePedido;

	@Column(name = "PDMC_DH_REAL_CHEGADA")
	private Date dataChegada;

}
