package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.clique.retire.enums.FasePedidoEnum;
import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.converter.FasePedidoEnumConverter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Table(name = "PEDIDO_SERVICO")
public class PedidoServico extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PESE_NR_SERVICO")
	private Long codigo;

	@Column(name = "PESE_TX_DESCRICAO")
	private String descricao;

	@ManyToOne
	@JoinColumn(name = "PEDI_NR_PEDIDO")
	private Pedido pedido;

	@Column(name = "PESE_DH_HORARIO_ENTREGA")
	private Date dataHorarioEntrega;
	
	@Column(name = "PESE_DH_HORARIO_ENTREGA_FINAL")
	private Date dataHorarioEntregaFinal;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "PESE_FL_TAXA_ENTREGA_ISENTADA")
	private SimNaoEnum taxaEntregaIsentada;
	
	@Column(name = "PESE_NR_VALOR_FRETE")
	private Double valorFrete;
	
	@Column(name = "TISE_CD_TIPOSERV")
	private Integer idTipoServico;

	@Column(name = "EDRC_CD_ENDERECO")
	private Integer idEndereco;

	@Column(name = "PESE_FL_TIPO_HORARIO_ENTREGA")
	private String horarioEntrega;
	
	@Convert(converter = FasePedidoEnumConverter.class)
	@Column(name = "pedi_fl_fase")
	private FasePedidoEnum fasePedido;

	public PedidoServico(String codigoUsuario) {
		super(codigoUsuario);
	}
}