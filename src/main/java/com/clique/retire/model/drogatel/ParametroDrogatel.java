package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "PARAMETRO_DROGATEL")
public class ParametroDrogatel extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "PRDG_CD_PARAMETRO_DROGATEL")
	private Integer codigo;
	
	@ManyToOne
	@JoinColumn(name = "DEPO_CD_DEPOSITO_PADRAO")
	private Deposito depositoPadrao;

}
