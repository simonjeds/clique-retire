package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="IMPRESSAO_PEDIDO")
@NoArgsConstructor
@AllArgsConstructor
public class ImpressaoPedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IPPD_CD_IMPRESSAO_PEDIDO")
	private Integer id;
	
	@Column(name = "PEDI_NR_PEDIDO")
	private Integer numeroPedido;

	@Column(name = "USUA_CD_USUARIO")
	private Integer codigoUsuario;
	
	@Column(name = "IPPD_DH_HORA_IMPRESSAO")
	private Date data;
	
	public ImpressaoPedido(Integer numeroPedido, Integer codigoUsuario, Date data) {
		super();
		this.numeroPedido = numeroPedido;
		this.codigoUsuario = codigoUsuario;
		this.data = data;
	}
}