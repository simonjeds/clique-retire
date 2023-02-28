package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="DRGTBLPCRPARAMETRO")
public class ParametroCliqueRetire extends BaseEntityAraujo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PCRSEQ")
	private Integer id;
	
	@Column(name="PCRNOM")
	private String nome;
	
	@Column(name="PCRVAL")
	private String valor;
	
	@Column(name="PCRDES")
	private String descricao;
	
	public ParametroCliqueRetire() {
	}
	
	public ParametroCliqueRetire(String codigoUsuario) {
		super(codigoUsuario);
	}
}