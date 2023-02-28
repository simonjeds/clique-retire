package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Pedido;

/**
 * @author Framework
 *
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>{
	
	@Query(value = "SELECT p FROM Pedido p WHERE p.numeroPedido = :numeroPedido")
	Pedido findByNumeroPedido(@Param("numeroPedido") Long numeroPedido);
	
	@Procedure(value = "sp_pedido_sequence")
	public int getPedidoSequence();	
	
	/**
	 * Método que consulta um pedido para ser utilizado ao iniciar a separação.
	 * 
	 * @param numeroPedido
	 * @return array com os campos do pedido necessários.
	 */
	@Query(value = "SELECT p.numeroPedido, p.polo.codigo, p.codigoFilialGerencial, p.fasePedido, p.codigoFilialAraujoTem, p.superVendedor FROM Pedido p where p.numeroPedido = :numeroPedido")
	Object obterPedidoParaSeparacao(@Param("numeroPedido") Long numeroPedido);
	
	@Query(nativeQuery = true, value = "SELECT TOP 1 inf.ITNO_CD_ITEM FROM ITEM_NOTA_FISCAL inf WHERE inf.ITPD_CD_ITEM_PEDIDO = :idItemPedido")
	public Long buscarCodigoItemNotaFiscal(Long idItemPedido);	

	/**
	 * Método que consulta um pedido pelo código VTEX.
	 * 
	 * @param codigoEcommerce
	 * @return o pedido conforme código VTEX.
	 */
	public Pedido findByCodigoVTEX(String codigoVTEX);
	
	@Query(nativeQuery = true, value = "SELECT COUNT(*) FROM PENDENCIA_PEDIDO_DROGATEL ppd WHERE pepd_fl_pendencia_resolvida = 'N' AND pedi_nr_pedido = :idPedido")
	public Integer buscarPendencias(@Param("idPedido") Long id);
	
	@Query(nativeQuery = true, 
		   value = "SELECT IIF(p.pedi_tp_pedido = 'A' AND nfd.pedi_nr_pedido IS NULL, 'TRUE', 'FALSE') "
		   		 + "FROM pedido p (nolock) "
		   		 + "LEFT JOIN nota_fiscal_drogatel nfd (nolock) ON p.pedi_nr_pedido = nfd.pedi_nr_pedido "
		   		 + "WHERE p.pedi_nr_pedido = :idPedido")
	public String isPedidoAraujoTemBalcao(@Param("idPedido") Integer id);
	
	@Query(nativeQuery = true, 
			   value = "SELECT IIF(nfd.pedi_nr_pedido IS NULL, 'TRUE', 'FALSE') "
			   		 + "FROM pedido p (nolock) "
			   		 + "LEFT JOIN nota_fiscal_drogatel nfd (nolock) ON p.pedi_nr_pedido = nfd.pedi_nr_pedido "
			   		 + "WHERE p.pedi_nr_pedido = :idPedido")
	public String isPedidoCupomFiscal(@Param("idPedido") Integer id);
	
}