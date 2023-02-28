package com.clique.retire.repository.cosmos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.clique.retire.model.cosmos.Imagem;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long> {
	
	/**
	 * Consulta o código da primeira imagem ativa de um produto.
	 * 
	 * @param codigoItem
	 * @return Integer.
	 */
	@Query(nativeQuery = true, value = " SELECT TOP 1 IMG.DIPCOD FROM CSMECIDBS.DBO.ECITBLIMG IMG (nolock) "
									 + " INNER JOIN CSMECIDBS.DBO.ECITBLDIPDETIMAGEMPROD DET (nolock) ON IMG.DIPCOD = DET.DIPCOD "
									 + " INNER JOIN CSMECIDBS.DBO.ECITBLCIPCABIMAGEMPROD CAB (nolock) ON DET.CIPCOD = CAB.CIPCOD "
									 + " WHERE cab.PRME_CD_PRODUTO = :codigoProduto AND det.DIPSTA = 'A' ORDER BY DET.DIPORDORDEM ")
	public Integer findImagemByCodigoProduto(@Param("codigoProduto") Integer codigoProduto);
	
}
