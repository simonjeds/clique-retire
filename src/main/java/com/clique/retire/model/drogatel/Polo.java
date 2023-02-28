package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@Entity
@Table(name="POLO")
public class Polo extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="polo_cd_polo")
	@EqualsAndHashCode.Include
	private Integer codigo;
	
	public Polo(String codigoUsuario) {
		super(codigoUsuario);
	}
	
	public Polo() {
		super(null);
	}
}
