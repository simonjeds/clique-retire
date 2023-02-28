package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="DRGTBLMCRMETRICAS_HST")
public class HistoricoMetrica extends BaseEntityAraujo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="MCRSEQ")
	private Integer id;
	
	@Column(name="PEDI_NR_PEDIDO")
	private Integer numeroPedido;
	
	@Column(name="POLO_CD_POLO")
	private Integer numeroFilial;
	
	@Column(name="MCRTPOINTEGRAR")
	private Integer tempoIntegracao;
	
	@Column(name="MCRTPOSEPARAR")
	private Integer tempoSeparacao;
	
	@Column(name="MCRTPOREGISTRAR")
	private Integer tempoRegistro;
	
	public HistoricoMetrica(Integer codigoUsuario) {
		super(codigoUsuario.toString());
	}
	
	public HistoricoMetrica(Integer codigoUsuario, String nomeEstacaoUltimaAlteracao) {
		super(codigoUsuario, nomeEstacaoUltimaAlteracao);
	}
}