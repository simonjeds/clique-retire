package com.clique.retire.repository.drogatel;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.LoteBipado;

@Repository
public interface LoteBipadoRepository extends JpaRepository<LoteBipado, Long> {

	@Transactional
	@Modifying
	@Query(
		value =
			"DELETE FROM lote_bipado WHERE itpd_cd_item_pedido IN ( " +
				"SELECT itpd_cd_item_pedido FROM item_pedido " +
				"WHERE pedi_nr_pedido = :numeroPedido AND itpd_fl_produto_controlado = 'S'" +
			") ",
		nativeQuery = true
	)
	void limparLoteBipadoPorNumeroPedido(@Param("numeroPedido") Integer numeroPedido);

	@Modifying
	@Query("DELETE FROM LoteBipado lb WHERE lb.itemPedido.codigo IN :idsItensPedido")
    void deleteByIdsItensPedido(List<Long> idsItensPedido);

}