package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "CLIENTE")
public class Cliente extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "clnt_cd_cliente")
	private Integer id;

	@Column(name = "clnt_ds_nome")
	private String nome;
	
	@Column(name = "clnt_ds_email")
	private String email;

	@Column(name = "clnt_tn_cpf_cnpj")
	private String documento;
	
	@Column(name = "CLNT_TN_CELULAR")
	private String celular;
	
	@Column(name = "CLNT_DS_SEXO")
	private String sexo;	
	

}