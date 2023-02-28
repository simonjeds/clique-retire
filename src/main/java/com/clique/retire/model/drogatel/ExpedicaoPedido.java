package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
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
import com.clique.retire.enums.TipoPedidoEnum;
import com.clique.retire.enums.converter.TipoPedidoEnumConverter;
import com.clique.retire.enums.converter.TipoTaxaEntregaEnumConverter;
import com.clique.retire.model.enums.TipoTaxaEntregaEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Framework
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EXPEDICAO_PEDIDO")
public class ExpedicaoPedido extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EXPD_CD_EXPEDICAO_PEDIDO")
	private Integer codigo;
	
	@Column(name = "EXPD_VL_TAXA_ENTREGA")
	private Double valorTaxaEntrega;
	
	@Column(name = "EXPD_DH_PREVISAO_ENTREGA")
	private Date dataHoraPrevisaoEntrega;
	
	@Column(name = "EXPD_DH_REAL_ALTERADO")
	private Date dataHoraRealAlterado;
	
	@Column(name = "EXPD_DH_REAL_ENTREGA")
	private Date dataHoraRealEntrega;
	
	@Column(name = "EXPD_DH_ENTREGA_PEDIDO_MOBILE")
	private Date dataEntregaMobile;
	
	
	@Column(name = "MTDR_CD_MOTIVO_NAO_ENTREGA")
	private Integer codMotivoNaoEntrega;
	
	@Column(name = "EXPD_DS_NAO_ENTREGA")
	private String motivoNaoEntrega;
	
	@Column(name="EXPD_DH_CHEGOU_CLIENTE")
	private Date dataChegouCliente;
	
	@Convert(converter = TipoTaxaEntregaEnumConverter.class)
	@Column(name = "EXPD_TP_TAXA_ENTREGA")
	private TipoTaxaEntregaEnum tipoTaxaEntrega;
	
	@ManyToOne
	@JoinColumn(name = "EXPE_CD_EXPEDICAO")
	private Expedicao expedicao;
	
	@OneToOne
	@JoinColumn(name = "PDMC_CD_PEDIDO_MERCADORIA")
	private PedidoMercadoria pedidoMercadoria;
	
	@ManyToOne
	@JoinColumn(name = "PEDI_NR_PEDIDO")
	private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name = "PESE_NR_SERVICO")
	private PedidoServico pedidoServico;
	
	@Column(name = "EXPD_VL_SEQ_ENTREGA")
	private Integer sequenciaEntrega;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "EXPD_FL_RETORNO")
	private SimNaoEnum indicadorRetorno;
	
	@Convert(converter=TipoPedidoEnumConverter.class)
	@Column(name = "EXPD_TP_PEDIDO")
	private TipoPedidoEnum tipoPedido;
	
}
