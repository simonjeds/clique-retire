package com.clique.retire.service.drogatel;

import com.clique.retire.model.drogatel.MotivoDrogatel;
import com.clique.retire.repository.drogatel.MotivoDrogatelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MotivoDrogatelService {

    private final MotivoDrogatelRepository repository;
    private final Map<String, MotivoDrogatel> cacheMotivosPorDescricao = new HashMap<>();

    public List<MotivoDrogatel> buscarPorTipo(String tipoMotivo) {
        return repository.findByTipo(tipoMotivo);
    }

    public MotivoDrogatel buscarPorTipoEDescricao(String tipoMotivo, String descricao) {
        return repository.findTopByTipoAndDescricao(tipoMotivo, descricao);
    }

    public MotivoDrogatel buscarPorDescricao(String descricao) {
        return repository.findTopByDescricao(descricao);
    }

    public MotivoDrogatel getMotivoByDescricaoCache(String descricao) {
        MotivoDrogatel motivo = cacheMotivosPorDescricao.get(descricao);

        if (Objects.isNull(motivo)) {
            MotivoDrogatel motivoDrogatel = buscarPorDescricao(descricao);
            cacheMotivosPorDescricao.put(descricao, motivoDrogatel);
        }

        return cacheMotivosPorDescricao.get(descricao);
    }

    public MotivoDrogatel buscarMotivoParaCancelamentoDePedidoNoDrogatel() {
        String tipoMotivo = "C";
        String descricao = "FALTA DE MERCADORIA";
        return buscarPorTipoEDescricao(tipoMotivo, descricao);
    }

}
