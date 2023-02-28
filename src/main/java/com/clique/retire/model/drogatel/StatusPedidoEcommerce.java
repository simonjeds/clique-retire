package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Framework
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "DRGTBLISEINTESTAPEDECOM")
public class StatusPedidoEcommerce extends BaseEntityAraujo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ISESEQSTP")
	private Integer codigo;
	
	@Column(name = "ISEXMLSERVICO")
	private String xml;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ISEIDCSUCESSO")
	private SimNaoEnum enviada;
	
	@Column(name = "PEDI_NR_PEDIDO")
	private Integer codigoPedidoDrogatel;

	public StatusPedidoEcommerce(Integer codigoUsuario) {
		super(codigoUsuario);
	}
}