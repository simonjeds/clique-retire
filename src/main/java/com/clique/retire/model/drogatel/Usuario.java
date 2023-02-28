package com.clique.retire.model.drogatel;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name="USUARIO")
public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="usua_cd_usuario")
	private Integer codigoUsuario;
	
	@Column(name="usua_tx_matricula")
	private String matricula;
	
	@Column(name="usua_nm_usuario")
	private String nome;
	
	@Column(name="usua_fl_hab_desab")
	private String habilitado;
	
	@Transient
	private String token;

	public Usuario(Integer codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

}
