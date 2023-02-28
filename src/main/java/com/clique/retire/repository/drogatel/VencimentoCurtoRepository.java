package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.VencimentoCurto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VencimentoCurtoRepository extends JpaRepository<VencimentoCurto, Long>{
	
	@Query("SELECT vc FROM VencimentoCurto vc WHERE vc.itemPedido.codigo = :codItemPedido and vc.usuario.codigoUsuario = :codUsuario")
	VencimentoCurto findByItemPedidoCodUsuario(Integer codItemPedido, Integer codUsuario);

	@Modifying
	@Query("DELETE FROM VencimentoCurto vc WHERE vc.itemPedido.codigo IN :idsItensPedido")
	void excluirPorCodigoItemPedido(List<Integer> idsItensPedido);

}
