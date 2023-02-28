package com.clique.retire.repository.cosmos;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.clique.retire.dto.ItemNotaFiscalDTO;
import com.clique.retire.model.drogatel.Pedido;

@Repository
public class NotaFiscalRepositoryImpl implements NotaFiscalRepositoryCustom {
	
	@PersistenceContext(unitName = "cosmosEntityManager")
	private EntityManager em;
	
	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager emDrogatel;
	
	public boolean existeNotaFiscal(Long numeroPedido) {
		StringBuilder sbQuery = new StringBuilder();

        sbQuery.append(" select top 1 nf.PEDI_NR_PEDIDO ")
               .append(" from NOTA_FISCAL nf (nolock) ")
               .append(" where nf.PEDI_NR_PEDIDO = :numeroPedido ")
               .append("       and nf.NOFI_CD_SITUACAO = '0'  ")
               .append("       and NOFI_TP_NF <> 'B' ")
               .append("       and nf.NOFI_TX_CHAVE_NFE  is not null ")
               .append(" order by NOFI_DH_EMISSAO desc ");

		Query query = em.createNativeQuery(sbQuery.toString());
		query.setParameter("numeroPedido", numeroPedido);

		List<Object[]> listResult = query.getResultList();
		
		return listResult != null && !listResult.isEmpty();
	}

	@Override
	public void removerItensNotaPeloItemPedido(List<Integer> idsItensPedido) {
		emDrogatel.createNativeQuery("DELETE FROM item_nota_fiscal WHERE itpd_cd_item_pedido IN :idsItensPedido")
				.setParameter("idsItensPedido", idsItensPedido)
				.executeUpdate();
	}

	@Override
	public void atualizarValorNotaEItens(List<ItemNotaFiscalDTO> itens, Pedido pedido) {
		StringBuilder updateNota = new StringBuilder()
				.append("UPDATE nota_fiscal_drogatel SET nofi_vl_total_pedido = :valorTotalPedido ")
				.append("WHERE pedi_nr_pedido = :numeroPedido");

		emDrogatel.createNativeQuery(updateNota.toString())
				.setParameter("valorTotalPedido", pedido.getValorTotalItens())
				.setParameter("numeroPedido", pedido.getNumeroPedido())
				.executeUpdate();

		StringBuilder updateItens = new StringBuilder()
				.append("UPDATE item_nota_fiscal SET itno_qt_produto = :quantidade, itno_vl_unitario = :preco ")
				.append("WHERE itpd_cd_item_pedido = :codigoItem");

		itens.forEach(
			item -> emDrogatel.createNativeQuery(updateItens.toString())
				.setParameter("quantidade", item.getQuantidade())
				.setParameter("preco", item.getPreco())
				.setParameter("codigoItem", item.getCodigoItemPedido())
				.executeUpdate()
		);
	}

}
