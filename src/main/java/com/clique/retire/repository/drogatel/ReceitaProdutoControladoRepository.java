package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.ReceitaProdutoControlado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceitaProdutoControladoRepository extends JpaRepository<ReceitaProdutoControlado, Long> {

	@Query(value =
		"SELECT ISNULL( " +
		"  ( " +
		"    SELECT SUM(itpd_nr_quantidade_pedida) FROM item_pedido " +
		"    WHERE pedi_nr_pedido = :numeroPedido AND itpd_fl_produto_controlado = 'S' " +
		"  ) - ISNULL(" +
		"    (" +
		"      SELECT SUM(rcpc_nr_caixas) FROM receita_produto_controlado " +
		"      WHERE itpd_cd_item_pedido IN (" +
		"        SELECT itpd_cd_item_pedido FROM item_pedido WHERE pedi_nr_pedido IN (:numeroPedido) " +
		"      ) AND DATALENGTH(rcpc_cd_autorizacao) > 0 " +
		"    ), 0 " +
		"  ) " +
		", 0) AS 'aCaptar'",
		nativeQuery = true
	)
	int aCaptar(@Param("numeroPedido") Long numeroPedido);

	@Modifying
	@Query(value =
		"UPDATE receita_produto_controlado SET usua_cd_usuario = :codigoUsuario " +
		"WHERE itpd_cd_item_pedido IN ( " +
		"  SELECT ip.itpd_cd_item_pedido FROM item_pedido ip WHERE ip.pedi_nr_pedido = :numeroPedido " +
		")",
		nativeQuery = true
	)
	void salvarUsuarioConferente(@Param("numeroPedido") Long numeroPedido, @Param("codigoUsuario") Integer codigoUsuario);

	@Modifying
	@Query(value =
		"DELETE FROM receita_lote_produto " +
		"WHERE rcpc_cd_receita_produto_controlado IN (" +
		"  SELECT rcpc_cd_receita_produto_controlado FROM receita_produto_controlado rpc " +
		"  WHERE rpc.itpd_cd_item_pedido IN :idsItensPedido " +
		")",
		nativeQuery = true
	)
	void deleteReceitaLoteProdutoByIdsItensPedido(List<Long> idsItensPedido);
	
	@Modifying
	@Query(value =
		"DELETE FROM documento_esperado " +
		"WHERE rcpc_cd_receita_produto_controlado IN (" +
		"  SELECT rcpc_cd_receita_produto_controlado FROM receita_produto_controlado rpc " +
		"  WHERE rpc.itpd_cd_item_pedido IN :idsItensPedido " +
		")",
		nativeQuery = true
	)
	void deleteDocumentoEsperadoByIdsItensPedido(List<Long> idsItensPedido);
	
	@Modifying
	@Query(value =
		"DELETE FROM posologia " +
		"WHERE rcpc_cd_receita_produto_controlado IN (" +
		"  SELECT rcpc_cd_receita_produto_controlado FROM receita_produto_controlado rpc " +
		"  WHERE rpc.itpd_cd_item_pedido IN :idsItensPedido " +
		")",
		nativeQuery = true
	)
	void deletePosologiaByIdsItensPedido(List<Long> idsItensPedido);

	@Modifying
	@Query("DELETE FROM ReceitaProdutoControlado rpc WHERE rpc.itemPedido IN :idsItensPedido")
	void deleteAllByItemPedidoCodigo(List<Long> idsItensPedido);

}