package com.clique.retire.model.drogatel;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProdutoDepositoId implements Serializable{

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "DEPO_CD_DEPOSITO")
	private Deposito deposito;
	
	@ManyToOne
	@JoinColumn(name="prme_cd_produto")
	private Produto produtoMestre;
}
