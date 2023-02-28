package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.PendenciaPedidoDrogatel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendenciaPedidoRepository extends JpaRepository<PendenciaPedidoDrogatel, Long>,
  PendenciaPedidoRepositoryCustom {

}
