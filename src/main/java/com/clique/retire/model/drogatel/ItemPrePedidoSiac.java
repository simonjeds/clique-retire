package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.clique.retire.model.enums.AtivoInativoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper=false)
@Data
@Entity
@Table(name="DRGTBLIPSITEMPREPEDSIAC")
public class ItemPrePedidoSiac extends BaseEntityAraujo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "IPSSEQ")
	private Long codigo;

	@Column(name = "IPSIDCATIVO")
	@Enumerated(EnumType.STRING)
	private AtivoInativoEnum ativoInativo;

	@ManyToOne
	@JoinColumn(name = "PPSSEQ", insertable = true, updatable = false)
	private PrePedidoSiac prePedido;

	@Column(name = "ITPD_CD_ITEM_PEDIDO")
	private Integer codigoItemPedido;
	
	public ItemPrePedidoSiac(String codigoUsuario) {
		super(codigoUsuario);
	}
}
