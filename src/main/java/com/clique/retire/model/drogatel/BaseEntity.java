package com.clique.retire.model.drogatel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="xxxx_dh_alt")
	private Date ultimaAlteracao;
	
	@Column(name="xxxx_ct_lock")
	private Integer versaoRegistro;

	@Column(name="xxxx_cd_usualt")
	private String codigoUsuarioAlteracao;
	
	protected BaseEntity(String codigoUsuario) {
		this.ultimaAlteracao= new Date();
		this.codigoUsuarioAlteracao = codigoUsuario;
		this.versaoRegistro = versaoRegistro == null ? 0 : versaoRegistro + 1;
	}
	
}
