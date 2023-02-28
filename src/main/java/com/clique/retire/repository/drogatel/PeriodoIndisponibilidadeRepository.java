package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.PeriodoIndisponibilidade;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PeriodoIndisponibilidadeRepository extends JpaRepository<PeriodoIndisponibilidade, Long> {

  List<PeriodoIndisponibilidade> findByDataHoraInicioGreaterThan(LocalDateTime dataHoraInicio, Sort sort);

}
