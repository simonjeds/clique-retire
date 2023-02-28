package com.clique.retire.repository.drogatel;

import com.clique.retire.model.drogatel.ItemCupomFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemCupomFiscalRepository extends JpaRepository<ItemCupomFiscal, Integer> {

    @Modifying
    @Query("DELETE FROM ItemCupomFiscal icf WHERE icf.itemPedido.codigo IN :idsItensPedido")
    void deleteAllByIdsItensPedido(List<Integer> idsItensPedido);

}
