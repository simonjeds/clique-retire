package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "DRGTBLIFRITEMFALTARETORNO")
public class ItemFaltaRetorno extends BaseEntityAraujo {

  private static final long serialVersionUID = 4898799229265034974L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "IFRSEQ")
  private Integer id;

  @Column(name = "IFRQTDFALTANTE")
  private Integer quantidade;

  @Column(name = "ITPD_CD_ITEM_PEDIDO")
  private Integer codigoItemPedido;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EXPD_CD_EXPEDICAO_PEDIDO")
  private ExpedicaoPedido expedicaoPedido;

  public ItemFaltaRetorno(String codigoUsuario) {
    super(codigoUsuario);
  }

}
