package com.clique.retire.model.drogatel;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.clique.retire.model.enums.AtivoInativoEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name="DRGTBLPPSPREPEDSIAC")
@NoArgsConstructor
public class PrePedidoSiac extends BaseEntityAraujo{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="PPSSEQ")
	private Integer codigo;
	
	@Column(name="pedi_nr_pedido")
	private Integer numeroPedido;
	
	@Column(name="UPPCODPREPEDIDO")
	private Integer numeroPrePedido;
	
	@Column(name="FILI_CD_FILIAL")
	private Integer codigoFilial;
	
	@Column(name="PPSIDCATIVO")
	@Enumerated(EnumType.STRING)
	private AtivoInativoEnum ativoInativo;
	
	@OneToMany(fetch= FetchType.LAZY, mappedBy= "prePedido", cascade = CascadeType.ALL)
	private List<ItemPrePedidoSiac> itens;
	
	public PrePedidoSiac(String codigoUsuario) {
		super(codigoUsuario);
	}
}
