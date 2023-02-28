package com.clique.retire.repository.drogatel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.ImpressaoPedido;

@Repository
public interface ImpressaoPedidoRepository extends JpaRepository<ImpressaoPedido, Integer> {

    @Query(nativeQuery = true, value = "SELECT CASE WHEN "
            +  "    COUNT(*) <= cast((SELECT TOP 1 PCRVAL "
            + "             FROM DRGTBLPCRPARAMETRO (nolock) WHERE PCRNOM = "
            + "             'QUANTIDADE_IMPRESSOES_QUE_MOSTRAM_TUTORIAL' "
            + " ) as int) THEN CAST(1 as bit) ELSE CAST(0 as bit) END "
            + " FROM (SELECT COUNT(*) AS QTDE FROM IMPRESSAO_PEDIDO ip (nolock) "
            + "         WHERE ip.USUA_CD_USUARIO = :codigoUsuario "
            + "         GROUP BY PEDI_NR_PEDIDO) as ip ")
    public boolean isImprimirTutorial(@Param("codigoUsuario") Integer codigoUsuario);

    @Query(nativeQuery = true, value = "SELECT CASE WHEN "
            +  "    COUNT(*) <= cast((SELECT TOP 1 PCRVAL "
            + "             FROM DRGTBLPCRPARAMETRO (nolock) WHERE PCRNOM = "
            + "             'QUANTIDADE_IMPRESSOES_QUE_MOSTRAM_TUTORIAL_ARAUJO_TEM' "
            + " ) as int) THEN CAST(1 as bit) ELSE CAST(0 as bit) END "
            + " FROM (SELECT COUNT(*) AS QTDE FROM IMPRESSAO_PEDIDO ip (nolock) "
            + "         JOIN PEDIDO p ON p.pedi_nr_pedido = ip.pedi_nr_pedido "
            + "         WHERE ip.USUA_CD_USUARIO = :codigoUsuario "
            + "         AND p.FILI_CD_FILIAL_ARAUJO_TEM is not null "
            + "         GROUP BY ip.PEDI_NR_PEDIDO) as ip ")
    public boolean isImprimirTutorialAraujoTem(@Param("codigoUsuario") Integer codigoUsuario);
}