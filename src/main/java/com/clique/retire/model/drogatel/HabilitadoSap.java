package com.clique.retire.model.drogatel;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="ITDSAPDRGDBS.dbo.SAPDRGTBLPLSPARLIBFLUXO")
public class HabilitadoSap implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="HDRDTHHOR")
	private LocalDate dataHoraRegistro;
	
	@Column(name="HDRCODUSU")
	private String codUsuario;
	
	@Column(name="HDRCODLCK")
	private Integer codLock;
	
	@Column(name="HDRDTHINS")
	private LocalDate dataHoraThins;
	
	@Column(name="HDRCODETC")
	private String codEtc;
	
	@Column(name="HDRCODPRG")
	private Integer codPrg;
	
	@Id
	@Column(name="PLSSEQ")
	private Integer plsSeq;
	
	@Column(name="PLSTXTCHAVE")
	private String plsTxtChave;
	
	@Column(name="PLSNOMSISTEMA")
	private String plsNomSistema;
	
	@Column(name="PLSDESFUNCIONALIDADE")
	private String plsDesFuncionalidade;
	
	@Column(name="PLSIDCATIVAFLUXOSAP")
	private String plsIdCativaFluxoSap;
	
}
