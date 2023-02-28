package com.clique.retire.model.drogatel;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="PEDIDO")
public class PedidoResumido implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="pedi_nr_pedido")
    private Long numeroPedido;
	
	@OneToMany(fetch= FetchType.LAZY, mappedBy= "pedido", cascade = CascadeType.ALL)
	private List<ItemPedidoResumido> itensPedido;
	
	
}
