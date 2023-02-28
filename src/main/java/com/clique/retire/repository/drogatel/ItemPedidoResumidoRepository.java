package com.clique.retire.repository.drogatel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.ItemPedidoResumido;
import com.clique.retire.model.drogatel.PedidoResumido;

@Repository
public interface ItemPedidoResumidoRepository extends JpaRepository<ItemPedidoResumido, Long> {


	public List<ItemPedidoResumido> findByPedido(PedidoResumido pedido);

}
