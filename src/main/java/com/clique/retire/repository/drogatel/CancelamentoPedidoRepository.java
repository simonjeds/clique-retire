package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.CancelamentoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CancelamentoPedidoRepository extends JpaRepository<CancelamentoPedido, Integer>,
  CancelamentoPedidoRepositoryCustom {

  @Query(
    "FROM CancelamentoPedido cp " +
    "WHERE cp.fim IS NULL AND cp.usuario.codigoUsuario = :codigoUsuario AND cp.pedido.polo.codigo= :filial"
  )
  Optional<CancelamentoPedido> findByUsuario(Integer codigoUsuario, Integer filial);

}
