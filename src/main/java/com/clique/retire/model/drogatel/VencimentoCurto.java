package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@Entity
@Table(name="DRGTBLIVCITEMVENCIMCURTO")
public class VencimentoCurto extends BaseEntityAraujo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="IVCSEQ")
	private Integer codigo;
	
	@ManyToOne
	@JoinColumn(name="USUA_CD_USUARIO")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name="ITPD_CD_ITEM_PEDIDO")
	private ItemPedido itemPedido;
	
	@Column(name="IVCDATVALIDADEPEDIDO ")
	private Date dataValidade;
	
	@Column(name="IVCDATVALIDADESEPARAD")
	private Date dataValidadeSeparado;
	
	public VencimentoCurto(String codigoUsuario) {
		super(codigoUsuario);
	}
}
