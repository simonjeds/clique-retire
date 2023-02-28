package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name="FERIADO")
public class Feriado extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="FERI_DT_FERIADO")
	private Date dataFeriado;
	
	@Column(name="FERI_DS_FERIADO")
	private String descricao;
	
	
	public Feriado(String codigoUsuario) {
		super(codigoUsuario);
	}
	
	public Feriado() {	
	}
}
