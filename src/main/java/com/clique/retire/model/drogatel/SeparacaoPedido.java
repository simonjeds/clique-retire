package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="SEPARACAO_PEDIDO")
public class SeparacaoPedido extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sppd_cd_separacao_pedido")
	private Integer id;
	
	@Column(name="pedi_nr_pedido")
	private Integer numeroPedido;
		
	@Column(name="USUA_CD_USUARIO")
	private Integer responsavelSeparacao;
	
	@Column(name="sppd_dh_horario_inicio")
	private Date dataInicio;
	
	@Column(name="sppd_dh_horario_termino")
	private Date dataTermino;
	
	@Column(name="sppd_fl_separacao_finalizada")
	@Enumerated(EnumType.STRING)
	private SimNaoEnum separacaoFinalizada;

	public SeparacaoPedido(String codigoUsuario) {
		super(codigoUsuario);
	}
}
