package com.clique.retire.repository.drogatel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;

import org.springframework.stereotype.Repository;

import com.clique.retire.dto.ItemPedidoSIACDTO;
import com.clique.retire.model.drogatel.PrePedidoSiac;

@Repository
public class SiacRepositoryImpl implements SiacRepositoryCustom {
	
	@PersistenceContext
    private EntityManager em;
	
	/**
	 * Método para atualizar o número do prePedido.
	 * @param PrePedidoSiac
	 */
	@Override
    public void atualizarNumeroPrePeddio(PrePedidoSiac prePedido) {
		em.createNativeQuery("UPDATE drgtblppsprepedsiac SET uppcodprepedido = :numeroPrePedido WHERE ppsseq = :codigo ")
		  .setParameter("numeroPrePedido", prePedido.getNumeroPrePedido())
		  .setParameter("codigo", prePedido.getCodigo())
		  .executeUpdate();
    }
	
	/**
	 * Método para obter os itens de um pedido que devem sofrer alteração de preço para se adequar ao preço da loja
	 * @param numero do pedido
	 */
    @SuppressWarnings("unchecked")
    public List<ItemPedidoSIACDTO> obterItensValorMaiorQueSIAC(Integer numeroPedido) {
    	StringBuilder sql = 
			new StringBuilder("SELECT ")
			          .append("    ip.itpd_cd_item_pedido as codigoItemPedido, ")
			          .append("    ip.pedi_nr_pedido as pedido, ")
			          .append("    ip.prme_cd_produto as codigoProduto, ")
			          .append("    ip.itpd_nr_quantidade_pedida as quantidade, ")
			          .append("    ip.itpd_vl_preco_unitario as precoAnterior, ")
			          .append("    CASE WHEN prfi_vl_prcprom > 0 THEN prfi_vl_prcprom ELSE prfi_vl_precovenda END as novoPreco, ")
			          .append("    p.polo_cd_polo as filial ")
			          .append("FROM item_pedido ip (nolock) ")
			          .append("INNER JOIN pedido p (nolock) ON p.pedi_nr_pedido = ip.pedi_nr_pedido  ")
			          .append("INNER JOIN produto_filial pf (nolock) ON pf.fili_cd_filial = p.polo_cd_polo AND pf.prme_cd_produto = ip.prme_cd_produto ")
			          .append("                                         AND itpd_vl_preco_unitario > (CASE WHEN prfi_vl_prcprom > 0 THEN prfi_vl_prcprom ELSE prfi_vl_precovenda END ) ")
			          .append("LEFT JOIN drgtblippitempedidopbm ipbm (nolock) ON ipbm.itpd_cd_item_pedido =  ip.itpd_cd_item_pedido ")
			          .append("WHERE p.pedi_nr_pedido = :numeroPedido AND ipbm.itpd_cd_item_pedido is null ");

  		List<Tuple> resultado = 
  				em.createNativeQuery(sql.toString(), Tuple.class)
				  .setParameter("numeroPedido", numeroPedido)
				  .getResultList();
		
  		return resultado.stream()
  						.map(tupla -> 
  								ItemPedidoSIACDTO
  									.builder()
  							 		.codigoItemPedido(tupla.get("codigoItemPedido", Integer.class))
  							 		.numeroPedido(tupla.get("pedido", Integer.class))
  							 		.codigoProduto(tupla.get("codigoProduto", Integer.class))
  							 		.quantidadeProduto(tupla.get("quantidade", Integer.class))
  							 		.precoAnterior(tupla.get("precoAnterior", BigDecimal.class).doubleValue())
  							 		.novoPreco(tupla.get("novoPreco", BigDecimal.class).doubleValue())
  							 		.codigoFilial(tupla.get("filial", Integer.class))
  							 		.build())
  						.collect(Collectors.toList());
	}
}