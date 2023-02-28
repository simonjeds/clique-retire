package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
	
	ItemPedido findByCodigo(Integer codigo);
	
	void deleteAllByCodigoIn(List<Integer> idsItensPedido);

	@Modifying
	@Query(
		value = "DELETE FROM drgtblipsitemprepedsiac WHERE itpd_cd_item_pedido IN :idsItensPedido", nativeQuery = true
	)
    void deleteItemPrePedidoSiacByItensPedido(List<Integer> idsItensPedido);

	@Modifying
	@Query(
		value = "DELETE FROM item_autorizacao_convenio WHERE itpd_cd_item_pedido IN :idsItensPedido", nativeQuery = true
	)
    void deleteItemAutorizacaoConvenioByItensPedido(List<Integer> idsItensPedido);

}
