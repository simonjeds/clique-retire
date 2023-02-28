package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name="FILIAL")
public class Filial extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="fili_cd_filial")
    private Integer id;
	
	@Column(name="fili_nm_fantasia")
	private String nomeFantasia;
	
	@Column(name="fili_hr_aberdom")
	private Date horarioAberturaDomingo;
	
	@Column(name="FILI_HR_ABERSEG")
	private Date horarioAberturaSegunda;
	
	@Column(name="FILI_HR_ABERTER")
	private Date horarioAberturaTerca;
	
	@Column(name="FILI_HR_ABERQUA")
	private Date horarioAberturaQuarta;
	
	@Column(name="FILI_HR_ABERQUI")
	private Date horarioAberturaQuinta;
	
	@Column(name="FILI_HR_ABERSEX")
	private Date horarioAberturaSexta;
	
	@Column(name="FILI_HR_ABERSAB")
	private Date horarioAberturaSabado;
	
	@Column(name="FILI_HR_ABERFER")
	private Date horarioAberturaFeriado;
	
	@Column(name="fili_hr_fechdom")
	private Date horarioFechamentoDomingo;
	
	@Column(name="FILI_HR_fechSEG")
	private Date horarioFechamentoSegunda;
	
	@Column(name="FILI_HR_fechTER")
	private Date horarioFechamentoTerca;
	
	@Column(name="FILI_HR_fechQUA")
	private Date horarioFechamentoQuarta;
	
	@Column(name="FILI_HR_fechQUI")
	private Date horarioFechamentoQuinta;
	
	@Column(name="FILI_HR_fechSEX")
	private Date horarioFechamentoSexta;
	
	@Column(name="FILI_HR_fechSAB")
	private Date horarioFechamentoSabado;
	
	@Column(name="FILI_HR_fechFER")
	private Date horarioFechamentoFeriado;
	
	public Filial(String codigoUsuario) {
		super(codigoUsuario);
	}
	
	public Filial() {	
	}
}
