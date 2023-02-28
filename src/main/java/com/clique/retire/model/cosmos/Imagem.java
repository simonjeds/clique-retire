package com.clique.retire.model.cosmos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "ECITBLIMG", schema = "CSMECIDBS.DBO")
public class Imagem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "dipcod")
	private Integer codigoImagem;

}
