package com.clique.retire.repository.drogatel;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SeparacaoRepositoryImpl implements SeparacaoRepositoryCustom {

	@PersistenceContext(unitName = "drogatelEntityManager")
	private EntityManager em;
	
	@Transactional
	public void iniciarSeparacao(Integer codigoLoja, Integer codigoUsuario) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("   DECLARE @numero_pedido INTEGER; ")
		   
		   .append("   SELECT top 1 @numero_pedido = numero_pedido FROM (  ")
		   .append("        SELECT   ")
		   .append("            p.pedi_nr_pedido as numero_pedido,    ")
		   .append("            p.pedi_dh_termino_atendimento as integracao,  ")
		   .append("            case when DATEDIFF(MINUTE,p.pedi_dh_termino_atendimento,CURRENT_TIMESTAMP) > 4 then   ")
		   .append("                (case when pedi_fl_marketplace = 'S' then 1 else 2 end)  ")
		   .append("            else   ")
		   .append("                (case when pedi_fl_marketplace = 'S' then 3 else 4 end )  ")
		   .append("            end as prioridade  ")
		   .append("        FROM pedido p (nolock) ")
		   .append("        LEFT JOIN drgtblpdcpedidocompl compl (NOLOCK) ON compl.pdcnrpedido = p.pedi_nr_pedido ")
		   .append("        WHERE p.pedi_fl_fase = '03'  ")
		   .append("              AND pedi_fl_operacao_loja = 'S'  ")
		   .append("              AND p.polo_cd_polo = :polo ")
		   .append("              AND COALESCE(p.pedi_fl_formula, 'N') = 'N'  ")
		   .append("              AND COALESCE(compl.pdcidcpapafila, 'N') = 'N'  ")
		   .append("    ) tb   ")
		   .append("   ORDER BY prioridade, integracao; ")
		   
		   .append("   IF @numero_pedido is not null ")
		   .append("      BEGIN")
		   .append("         UPDATE pedido  ")
		   .append("         SET pedi_fl_fase = '06', xxxx_dh_alt = current_timestamp, xxxx_cd_usualt = :idUsuario  ")
		   .append("         WHERE pedi_nr_pedido = @numero_pedido; ")
		   
		   .append("         INSERT INTO separacao_pedido   ")
		   .append("         VALUES (current_timestamp,current_timestamp,null,@numero_pedido,:idUsuario,'N',:idUsuario,1); ")
		   .append("      END");
		
		em.createNativeQuery(sql.toString())
		  .setParameter("polo", codigoLoja)
		  .setParameter("idUsuario", codigoUsuario)
		  .executeUpdate();
	}
	
	public void finalizarSeparacao(Long numeroPedido) {
		
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE SEPARACAO_PEDIDO ")
		   .append(" SET SPPD_DH_HORARIO_TERMINO = current_timestamp, SPPD_FL_SEPARACAO_FINALIZADA = 'S' ") 
		   .append(" WHERE PEDI_NR_PEDIDO = :numeroPedido AND SPPD_DH_HORARIO_TERMINO is null ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("numeroPedido", numeroPedido);
		
		query.executeUpdate();
	}
}