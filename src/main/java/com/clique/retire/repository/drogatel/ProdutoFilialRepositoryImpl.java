package com.clique.retire.repository.drogatel;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.math.NumberUtils;

import com.clique.retire.model.drogatel.Deposito;
import com.clique.retire.model.drogatel.ProdutoFilial;

public class ProdutoFilialRepositoryImpl implements ProdutoFilialRepositoryCustom {

	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public Optional<ProdutoFilial> obterProdutoFilialParaRegistroFalta(Integer codigoProduto, Integer codigoPolo,
			Long numeroPedido) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT ");
		sql.append("       pd.depo_cd_deposito as codigoDeposito, ");
		sql.append("       prfi_vl_prcprom as precoPromocional, ");
		sql.append("       prfi_vl_precovenda as precoVenda, ");
		sql.append("       ISNULL(prfi_qt_estoqatual,0) -  isnull(PFNReser.quantidadeReservada,0) as estoqueAtual,  ");
		sql.append("       ISNULL(PFNReser.quantidadeReservada,0) as quantidadeReservada ");
		sql.append("FROM produto_filial pf (nolock)  ");
		sql.append("INNER JOIN produto_deposito pd (nolock) ON pf.depo_cd_deposito = pd.depo_cd_deposito AND pf.prme_cd_produto = pd.prme_cd_produto ");
		sql.append("LEFT JOIN (" + montaClausulaEstoqueReservado(numeroPedido)
				+ ") AS PFNReser ON  PFNReser.produtoReservado = pf.prme_cd_produto AND PFNReser.poloEstReservado = pf.fili_cd_filial ");
		sql.append("WHERE pf.prme_cd_produto = :codigoProduto ");
		sql.append("      AND pf.fili_cd_filial = :codigoPolo ");

		Query query = em.createNativeQuery(sql.toString())
						.setParameter("codigoProduto", codigoProduto)
						.setParameter("codigoPolo", codigoPolo);

		return query.getResultList().stream().map(this::montarProdutoFilia).findFirst();
	}

	public String montaClausulaEstoqueReservado(Long numeroPedido) {
		StringBuilder select = new StringBuilder();
		select.append(" SELECT pr.polo_cd_polo as poloEstReservado, ");
		select.append("        ir.prme_cd_produto as produtoReservado, ");
		select.append("        SUM(ir.iprqtdreservada) as quantidadeReservada ");
		select.append(" FROM drgtblprepedidoreservado pr (nolock) ");
		select.append(" INNER JOIN drgtblipritempedreser ir (nolock) on ir.pedi_nr_pedido = pr.pedi_nr_pedido ");
		if (numeroPedido != null) {
			select.append(" WHERE pr.pedi_nr_pedido < " + numeroPedido);
		}
		select.append(" GROUP BY ir.prme_cd_produto, pr.polo_cd_polo ");

		return select.toString();
	}

	private ProdutoFilial montarProdutoFilia(Object o) {
		ProdutoFilial pf = new ProdutoFilial();
		Deposito d = new Deposito();
		Object[] valores = (Object[]) o;
		d.setCodigo(NumberUtils.toInt(valores[0].toString()));
		pf.setDeposito(d);
		pf.setPrecoPromocional(NumberUtils.toDouble(valores[1].toString()));
		pf.setPrecoVenda(NumberUtils.toDouble(valores[2].toString()));
		pf.setEstoqueAtual(NumberUtils.toInt(valores[3].toString()));
		pf.setQuantidadeReservada(NumberUtils.toInt(valores[4].toString()));

		return pf;
	}

}
