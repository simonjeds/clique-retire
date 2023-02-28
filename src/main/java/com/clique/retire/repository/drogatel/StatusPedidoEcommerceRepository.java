package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.StatusPedidoEcommerce;

@Repository
public interface StatusPedidoEcommerceRepository extends JpaRepository<StatusPedidoEcommerce, Integer> {

}