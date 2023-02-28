package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name="DRGTBLIPPITEMPEDIDOPBM")
public class ItemPedidoPBM extends BaseEntityAraujo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ITPD_CD_ITEM_PEDIDO")
	@EqualsAndHashCode.Include
	private Integer codigo;
	
	@OneToOne
	@JoinColumn(name="ITPD_CD_ITEM_PEDIDO")
	private ItemPedido itemPedido;
	
	@Column(name="CPBM_CD_CONVENIO_PBM")
	private Integer codigoPMBConveniosWeb;
	
	@Column(name="apbm_id_idt")
	private Integer autorizadoraPBM;
	
	@Column(name="IPPNUMAUTORIZPBM")
	private String autorizacaoPBM;

	public ItemPedidoPBM(String codigoUsuario) {
		super(codigoUsuario);
	}
}
