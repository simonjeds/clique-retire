package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "MODALIDADE_PAGAMENTO")
public class ModalidadePagamento extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "mdpg_cd_modalidade_pagamento")
	private Long id;

	@OneToOne
	@JoinColumn(name="PEDI_NR_PEDIDO")
	private Pedido pedido;

	@Column(name = "mdpg_nr_qtde_parcelas")
	private Integer qtdeParcelas;

}
