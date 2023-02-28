package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.ParametroDrogatel;

@Repository
public interface ParametroDrogatelRepository extends JpaRepository<ParametroDrogatel, Integer> {

  @Query("SELECT pa FROM ParametroDrogatel pa INNER JOIN pa.depositoPadrao d WHERE pa.codigo = :codigo")
  ParametroDrogatel obterParametroDrogatelComDepositoPadrao(@Param("codigo") Integer codigo);

}
