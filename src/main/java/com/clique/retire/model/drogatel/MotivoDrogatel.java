package com.clique.retire.model.drogatel;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MOTIVO_DROGATEL")
@Getter
@Setter
public class MotivoDrogatel {

  @Id
  @Column(name = "MTDR_CD_MOTIVO_DROGATEL")
  private Long id;

  @Column(name = "MTDR_DS_DESCRICAO")
  private String descricao;

  @Column(name = "MTDR_FL_TIPO_MOTIVO")
  private String tipo;

}
