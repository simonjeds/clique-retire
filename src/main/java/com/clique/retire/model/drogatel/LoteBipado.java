package com.clique.retire.model.drogatel;

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
@EqualsAndHashCode(callSuper = false)
@Entity
@NoArgsConstructor
@Table(name = "lote_bipado")
public class LoteBipado extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LTBP_CD_LOTE_BIPADO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "LTBP_NR_LOTE")
	private String lote;

	@Column(name = "ITNO_CD_ITEM")
	private Long codigoItemNotaFiscal;

	@Column(name = "LTBP_NR_QUANTIDADE_BIPADA")
	private Integer quantidade;

	@ManyToOne
	@JoinColumn(name = "ITPD_CD_ITEM_PEDIDO")
	private ItemPedidoResumido itemPedido;

	@Column(name = "LTBP_FL_TIPO_BIPAGEM")
	private String tipoBipagem = "R";
	
	public LoteBipado(Integer codigoUsuario) {
		super(codigoUsuario.toString());
	}
}