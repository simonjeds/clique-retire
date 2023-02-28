package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.converter.FasePedidoEnumConverter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DRGTBLHFPHISTFASEPEDIDO_HST")
public class HistoricoFasePedido extends BaseEntityAraujo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HFPCOD")
	@EqualsAndHashCode.Include
	private Integer codigo;

	@ManyToOne
	@JoinColumn(name = "PEDI_NR_PEDIDO")
	private Pedido pedido;

	@Column(name = "POLO_CD_POLO")
	private Integer codigoPolo;

	@Convert(converter = FasePedidoEnumConverter.class)
	@Column(name = "PEDI_FL_FASE_ATUAL")
	private FasePedidoEnum fasePedidoAtual;

	@Column(name = "HFPDTHENTFASEATUAL")
	private Date dataEntrouFaseAtual;

	public HistoricoFasePedido(String codigoUsuario) {
		super(codigoUsuario);
	}
}
