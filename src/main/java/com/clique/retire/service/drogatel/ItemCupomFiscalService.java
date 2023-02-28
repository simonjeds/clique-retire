package com.clique.retire.service.drogatel;

import com.clique.retire.repository.drogatel.ItemCupomFiscalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ItemCupomFiscalService {

    private final ItemCupomFiscalRepository repository;

    @Transactional
    public void removerPorItensPedido(List<Integer> idsItensPedido) {
        if (CollectionUtils.isNotEmpty(idsItensPedido)) {
            log.info("Removendo itemCupomFiscal para os itensPedido: {}", idsItensPedido);
            repository.deleteAllByIdsItensPedido(idsItensPedido);
        }
    }

}
