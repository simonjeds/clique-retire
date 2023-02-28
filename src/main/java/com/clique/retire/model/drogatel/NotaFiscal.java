package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name="NOTA_FISCAL_DROGATEL")
public class NotaFiscal extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="NOFI_CD_NOTA")
	@EqualsAndHashCode.Include
	private Integer id;
	
	@Column(name="NOFI_NR_NOTA")
	private Integer numeroNota;

	@Column(name = "NOFI_CH_ACESSO")
	private String chaveNota;
	
	@OneToOne
	@JoinColumn(name="PEDI_NR_PEDIDO")
	private Pedido pedido;
	
}