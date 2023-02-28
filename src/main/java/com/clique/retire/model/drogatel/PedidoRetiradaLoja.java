package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="DRGTBLPRLPEDIDORETLOJA")
public class PedidoRetiradaLoja extends BaseEntityAraujo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6149467284328969608L;

	@Id
	@Column(name="PEDI_NR_PEDIDO")
	private Integer numeroPedido;
	
	@Column(name="PRLXMLDADOSFINCARTAO")
	private String xmldadosCartao;
	
	@Column(name="PRLCODABERTARMARIO")
	private String codigoAberturaArmario;
	
	public PedidoRetiradaLoja() {	
	}
	
	public PedidoRetiradaLoja(Integer codigoUsuario) {
		super(codigoUsuario.toString());
	}
}
