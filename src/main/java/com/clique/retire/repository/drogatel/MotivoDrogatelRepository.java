package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.MotivoDrogatel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotivoDrogatelRepository extends JpaRepository<MotivoDrogatel, Long> {

  List<MotivoDrogatel> findByTipo(String tipo);
  MotivoDrogatel findTopByDescricao(String descricao);
  MotivoDrogatel findTopByTipoAndDescricao(String tipoMotivo, String descricao);

}
