package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="PRODUTO_MESTRE")

public class Produto extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="prme_cd_produto")
    private Integer codigo;
	
	@Column(name="prme_tx_descricao1")
    private String descricao;

	@Column(name="prme_nr_dv")
    private Integer digito;
	
	@Column(name="prme_fl_geladeira")
    private String geladeira;
	
	public Produto(String codigoUsuario) {
		super(codigoUsuario);
	}
	

}
