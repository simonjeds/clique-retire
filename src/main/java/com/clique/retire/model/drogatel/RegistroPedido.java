package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.StatusPrePedidoSiacEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name="REGISTRO_PEDIDO")
public class RegistroPedido extends BaseEntity{

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "repe_cd_registro_pedido")
	@EqualsAndHashCode.Include
	private Integer codigo;
	
	@OneToOne
	@JoinColumn(name = "usua_cd_respons_ecf", updatable = false, insertable = false)
	private Usuario responsavelECF;	
	
	@Column(name = "usua_cd_respons_ecf")
	private Long responsavelECFCodigo;		

	@ManyToOne
	@JoinColumn(name = "pedi_nr_pedido")
	private Pedido pedido;

	@Column(name = "repe_dh_hora_inic_registro")
	private Date dataInicioRegistro;

	@Column(name = "repe_dh_hora_term_registro")
	private Date dataFimRegistro;

	@Enumerated(EnumType.STRING)
	@Column(name="PPOSTAPREPEDSIAC")
	private StatusPrePedidoSiacEnum statusPrePedidoSIAC;

	@Enumerated(EnumType.STRING)
	@Column(name = "repe_fl_reg_loja")
	private SimNaoEnum registroLoja;

	@Column(name = "uppcodprepedido")
	private Integer codigoPrePedido;

	@Column(name = "fili_cd_filial")
	private Integer codigoFilial;

	@Column(name = "PPODTHASSOCIAORIGPP")
	private Integer dataHoraAssocPrePedidoECF;

	@Enumerated(EnumType.STRING)
	@Column(name = "REPE_FL_AGUARD_CONFIR_SENF")
	private SimNaoEnum aguardandoConfirmacaoDoSenf;

	@OneToOne
	@JoinColumn(name="usua_cd_responsavel")
	private Usuario responsavelRegistro;
	
	@Column(name="usua_cd_responsavel", insertable = false, updatable = false)
	private Long responsavelRegistroCodigo;	
	
	public RegistroPedido(String codigoUsuario) {
		super(codigoUsuario);
	}

	public RegistroPedido() {
	}	
	
	
}
