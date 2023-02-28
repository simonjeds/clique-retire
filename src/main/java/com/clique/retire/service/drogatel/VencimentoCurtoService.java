package com.clique.retire.service.drogatel;

import com.clique.retire.repository.drogatel.VencimentoCurtoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VencimentoCurtoService {

    private final VencimentoCurtoRepository repository;

    @Transactional
    public void removerVencimentoCurtoPorItensPedidos(List<Integer> idsItensPedido) {
        if (CollectionUtils.isNotEmpty(idsItensPedido)) {
            repository.excluirPorCodigoItemPedido(idsItensPedido);
        }
    }

}
