package com.clique.retire.repository.drogatel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.SeparacaoPedido;

@Repository
public interface SeparacaoRepository extends JpaRepository<SeparacaoPedido, Integer>{

	/**
	 * Método para obter a separação em aberto para o usuário.
	 *
	 * @param idUsuario
	 * @return SeparacaoPedido
	 */
	@Query(nativeQuery = true,
			value = "SELECT TOP 1 * FROM SEPARACAO_PEDIDO sp (NOLOCK) "
					+ " WHERE sp.pedi_nr_pedido = :idPedido "
					+ " ORDER BY sp.SPPD_CD_SEPARACAO_PEDIDO desc ")
	Optional<SeparacaoPedido> obterSeparacaoEmAbertoPorIdPedido(@Param("idPedido") Long idPedido);

	 /**
	 * Método para obter a quantidade de pedidos que estão na fase atendido e prontos para iniciar separação no polo.
	 *
	 * @param codigoPolo
	 * @return quantidade pedidos
	 */
	 @Query(value = "select " +
	 		"count(distinct p.PEDI_NR_PEDIDO) " +
	 		"from pedido p (NOLOCK) " +
	 		"left join DRGTBLIEIINTESTOQUEITEM intCD (NOLOCK) on intCD.PEDI_NR_PEDIDO = p.PEDI_NR_PEDIDO and intCD.IEIIDCINTEGRADO='N' " +
	 		"where POLO_CD_POLO= :codigoPolo and PEDI_FL_FASE='03' and PEDI_FL_OPERACAO_LOJA = 'N' " +
	 		"AND intCD.PEDI_NR_PEDIDO IS NULL "
			, nativeQuery = true)
	 Integer obterQuantidadeSepararPoloEcommerce(@Param("codigoPolo") Integer codigoPolo);

	/**
	* Método para obter o proximo para a ser separado na filial.
	*
	* @param codigoLoja
	* @return numeroPedido
	*/
	@Query("FROM SeparacaoPedido sp WHERE sp.numeroPedido = :numeroPedido ORDER BY sp.dataInicio DESC")
	List<SeparacaoPedido> obterSeparacoesEmAberto(@Param("numeroPedido") Integer numeroPedido);

	default SeparacaoPedido obterSeparacaoEmAberto(Integer numeroPedido) {
		return obterSeparacoesEmAberto(numeroPedido).stream().findFirst().orElse(null);
	}

	@Query(
		"FROM SeparacaoPedido sp WHERE sp.id = ( " +
		"  SELECT MAX(sep.id) FROM SeparacaoPedido sep WHERE sep.numeroPedido = :numeroPedido " +
		")"
	)
	SeparacaoPedido obterSeparacaoDoPedido(@Param("numeroPedido") Integer numeroPedido);

	@Query(value = " UPDATE SEPARACAO_PEDIDO " +
				 " SET SPPD_DH_HORARIO_TERMINO = current_timestamp, SPPD_FL_SEPARACAO_FINALIZADA = 'S' " +
				 " WHERE PEDI_NR_PEDIDO = :numeroPedido AND SPPD_DH_HORARIO_TERMINO is null ",
		 nativeQuery = true)
	void finalizarSeparacao(@Param("numeroPedido") Integer numeroPedido);

	Integer deleteByNumeroPedido(Integer numeroPedido);

}
