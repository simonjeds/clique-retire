package com.clique.retire.repository.drogatel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.model.drogatel.PedidoFracionado;

public interface PedidoFracionadoRepository extends JpaRepository<PedidoFracionado, Integer> {

	Optional<PedidoFracionado> findByIdAndFracionado(Integer id, SimNaoEnum fracionado);

}
