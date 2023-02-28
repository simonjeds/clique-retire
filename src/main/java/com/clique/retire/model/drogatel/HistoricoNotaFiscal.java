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

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name="HISTORICO_NOTA_FISCAL")
public class HistoricoNotaFiscal extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="HINF_CD_NOTA")
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name="HINF_MS_ERRO")
	private String descricaoErro;
	
	@ManyToOne
	@JoinColumn(name="PEDI_NR_PEDIDO")
	private Pedido pedido;
	
}