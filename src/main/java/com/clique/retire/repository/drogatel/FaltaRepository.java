package com.clique.retire.repository.drogatel;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.drogatel.Falta;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Polo;
import com.clique.retire.model.drogatel.Produto;

@Repository
public interface FaltaRepository extends JpaRepository<Falta, Integer> {
	
	@Procedure(procedureName = "sp_CsmInformacaoProdParaTrans", outputParameterName = "numRetorno" )
	Integer spCsmInformacaoProdParaTrans(
		@Param("DATA_PED") Date inicioSeparacao, @Param("PECA_CD_FILIALSOLICIT") Integer filialCodigo,
		@Param("PRME_CD_PRODUTO") Integer produtoCodigo
	);
	
	@Query("FROM Falta f WHERE f.produto = :produto AND f.pedido = :pedido AND f.polo = :polo ORDER BY f.id DESC ")
	Optional<Falta> obterUltimaFalta(
		@Param("produto") Produto produto, @Param("pedido") Pedido pedido, @Param("polo") Polo polo
	);

}
