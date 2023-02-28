package com.clique.retire.model.drogatel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.clique.retire.util.WebUtils;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntityAraujo implements Serializable {

	private static final String CLIQUE_RETIRE = "CLIQUE-RETIRE";
	protected static final Integer NUMERO_VERSAO_PAINEL_CLIQUE_RETIRE = 3;

	private static final long serialVersionUID = 1L;

	@Column(name = "HDRDTHHOR")
	private Date dataAlteracao = new Date();

	@Column(name = "HDRCODUSU")
	private String codigoUsuarioAlteracao;

	@Column(name = "HDRCODLCK")
	private Integer versaoRegistro;

	@Column(name = "HDRDTHINS")
	private Date dataInsercao = new Date();

	@Column(name = "HDRCODETC")
	private String estacaoUltimaAtualizacao;

	@Column(name = "HDRCODPRG")
	private String programaUltimaAtualizacao = CLIQUE_RETIRE;

	public BaseEntityAraujo() {
	}
	
	private BaseEntityAraujo(String codigoUsuario, String estacaoUltimaAtualizacao) {
		this.codigoUsuarioAlteracao = codigoUsuario;
		this.estacaoUltimaAtualizacao = estacaoUltimaAtualizacao;
		this.versaoRegistro = versaoRegistro == null ? 0 : versaoRegistro + 1;
	}
	
	public BaseEntityAraujo(String codigoUsuario) {
		this(codigoUsuario, WebUtils.getHostName());
	}

	public BaseEntityAraujo(Integer codigoUsuario) {
		this(codigoUsuario.toString());
	}
	
	public BaseEntityAraujo(Integer codigoUsuario, String estacaoUltimaAtualizacao) {
		this(codigoUsuario.toString(), estacaoUltimaAtualizacao);
	}

}
