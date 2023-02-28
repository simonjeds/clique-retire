package com.clique.retire.model.drogatel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "DRGTBLCPCCANCPEDCONTROL")
public class CancelamentoPedido extends BaseEntityAraujo {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CPCSEQ")
  private Integer id;

  @Column(name = "CPCDTHINICIO")
  private Date inicio;

  @Column(name = "CPCDTHFIM")
  private Date fim;

  @OneToOne
  @JoinColumn(name = "PEDI_NR_PEDIDO")
  private Pedido pedido;

  @OneToOne
  @JoinColumn(name = "USUA_CD_USUARIO")
  private Usuario usuario;

  public CancelamentoPedido(Usuario usuario, Pedido pedido) {
    super(usuario.getCodigoUsuario().toString());
    this.usuario = usuario;
    this.inicio = new Date();
    this.pedido = pedido;
  }

}