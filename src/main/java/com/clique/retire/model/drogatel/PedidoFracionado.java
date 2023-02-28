package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="DRGTBLPDCPEDIDOCOMPL")
public class PedidoFracionado extends BaseEntityAraujo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="PDCNRPEDIDO")
	private Integer id;
	
	@Enumerated(EnumType.STRING)
	@Column(name="PDCIDCFRACIONADO")
	private SimNaoEnum fracionado;
	
	@Column(name = "PDCIDTSENHAFRACIONAM")
	private String senha;
	
	public PedidoFracionado(String codigoUsuario) {
		super(codigoUsuario);
	}
	
	public PedidoFracionado() {	}
	
}