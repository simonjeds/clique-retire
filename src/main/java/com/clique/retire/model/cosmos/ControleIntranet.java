package com.clique.retire.model.cosmos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="CONTROLE_INTRANET")
public class ControleIntranet {

	@Id
	@Column(name = "CNIT_NM_ESTACAO")
	private String ip;

	@Column(name = "FILI_CD_FILIAL")
	private Integer filial;
}