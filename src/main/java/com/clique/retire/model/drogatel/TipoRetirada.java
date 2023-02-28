package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="DRGTBLTRLTIPORETLOJA")
public class TipoRetirada extends BaseEntityAraujo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="TRLSEQ")
    private Integer codigo;

	@Column(name="TRLNOMTIPORETIRADA")
	private Integer nome;
	
	@Column(name = "TRLDESTIPORETIRADA")
	private Long descricao	;

	public TipoRetirada(String codigoUsuario) {
		super(codigoUsuario);
	}
	
	public TipoRetirada() {
	}
}
