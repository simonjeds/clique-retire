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
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;

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
@Table(name = "PENDENCIA_PEDIDO_DROGATEL")
public class PendenciaPedidoDrogatel  extends BaseEntity {

	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PEPD_CD_PENDENCIA_PEDI_DROGATEL")
	private Integer codigo;
	
	@Column(name="PEPD_DS_PENDENCIA")
    private String descricaoPendencia;
	
	@ManyToOne
	@JoinColumn(name="USUA_CD_RESP_PENDENCIA")
    private Usuario responsavelPendencia;
	

	@Enumerated(EnumType.STRING)
	@Column(name="PEPD_FL_PENDENCIA_RESOLVIDA")
    private SimNaoEnum pendenciaResolvida;
	
	@Column(name="PEDI_NR_PEDIDO")
    private Integer numeroPedido;
	
	@Column(name="MTDR_CD_MOTIVO_DROGATEL")
    private Integer codigoMotivoDrogatel;


	@Column(name="PEPD_DH_DATA_CRIACAO")
    private Date dataCriacao;

	@Column(name="PEPD_FL_TIPO_PENDENCIA")
    private String tipoPendencia;

	public PendenciaPedidoDrogatel(String codigoUsuario) {
		super(codigoUsuario);
	}
}
