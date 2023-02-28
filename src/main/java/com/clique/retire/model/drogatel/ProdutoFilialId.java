package com.clique.retire.model.drogatel;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Embeddable
public class ProdutoFilialId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "FILI_CD_FILIAL")
	private Filial filial;
	
	@ManyToOne
	@JoinColumn(name = "PRME_CD_PRODUTO")
	private Produto produtoMestre;
	
	

}
