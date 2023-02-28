package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "PRODUTO_FILIAL")
public class ProdutoFilial extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ProdutoFilialId produtoFilialId;

	@ManyToOne
	@JoinColumn(name = "DEPO_CD_DEPOSITO")
	private Deposito deposito;

	@Column(name = "PRFI_QT_ESTOQATUAL")
    private Integer estoqueAtual;

	@Column(name = "PRFI_VL_DEMAX")
	private Double demanda;

	@Column(name = "PRFI_VL_PRCPROM")
	private Double precoPromocional;

	@Column(name = "PRFI_VL_PRECOVENDA")
	private Double precoVenda;

	@Transient
	private Integer quantidadeReservada;

}
