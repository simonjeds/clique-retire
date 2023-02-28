package com.clique.retire.repository.drogatel;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Feriado;

/**
 * @author Framework
 *
 */
@Repository
public interface FeriadoRepository extends JpaRepository<Feriado, Date>{
	
	/**
	 * Busca os feriados dentro de um período de datas.
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * 
	 * @return Lista de datas referentes a feriados
	 */
	@Query(value = "SELECT FERI_DT_FERIADO FROM FERIADO (nolock) "
			+ " WHERE FERI_DT_FERIADO BETWEEN :dataInicio AND :dataFim ", nativeQuery = true)
	public List<Date> buscarFeriadosPorPeriodo(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
	
}
