package com.clique.retire.model.drogatel;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "PRODUTO_DEPOSITO")
public class ProdutoDeposito extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ProdutoDepositoId produtoDepositoId;
	
	@Column(name = "PRDP_VL_PRECOVENDA")
    private Double precoVenda;
	
	@Column(name = "PRDP_VL_PRCPROM")
    private Double precoPromocional;
	
	@Column(name = "PRDP_DT_INIPRCPROM")
    private Date inicioPrecoPromocional;
	
	@Column(name = "PRDP_DT_FIMPRCPROM")
    private Date fimPrecoPromocional;
	
	@Column(name = "PRDP_FL_SITUACAO")
    private String situacaoProduto;
	
	@Column(name = "PRDP_QT_ESTOQATUAL")
    private Integer quantidadeEstoque;
}
