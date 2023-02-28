package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.CupomFiscal;

@Repository
public interface CupomFiscalRepository extends JpaRepository<CupomFiscal, Integer>{

    CupomFiscal findCupomFiscalByCodigoRegistro(Integer codigoRegistro);
    
    @Modifying
	@Query(nativeQuery = true, value = "DELETE FROM ITEM_CUPOM_FISCAL WHERE CUFI_CD_CUPOM IN "
			+ " (select CUFI_CD_CUPOM FROM CUPOM_FISCAL WHERE REPE_CD_REGISTRO_PEDIDO IN "
			+ " (select REPE_CD_REGISTRO_PEDIDO from REGISTRO_PEDIDO WHERE PEDI_NR_PEDIDO = :numeroPedido)) ")
	public Integer deleteItemByNumeropedido(@Param("numeroPedido") Long numeroPedido);	
	
	@Modifying
	@Query(nativeQuery = true, value = "DELETE FROM CUPOM_FISCAL WHERE REPE_CD_REGISTRO_PEDIDO IN "
			+ " (select REPE_CD_REGISTRO_PEDIDO FROM REGISTRO_PEDIDO WHERE PEDI_NR_PEDIDO = :numeroPedido) ")
	public Integer deleteCupomByNumeropedido(@Param("numeroPedido") Long numeroPedido);
    
}
