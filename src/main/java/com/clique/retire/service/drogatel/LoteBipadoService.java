package com.clique.retire.service.drogatel;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clique.retire.dto.LoteBipadoDTO;
import com.clique.retire.repository.drogatel.LoteBipadoRepository;
import com.clique.retire.repository.drogatel.LoteBipadoRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class LoteBipadoService {

    private final LoteBipadoRepository repository;
    private final LoteBipadoRepositoryImpl repositoryImpl;

    @Transactional
    public void removerPorItensPedido(List<Integer> idsItensPedido) {
        if (CollectionUtils.isNotEmpty(idsItensPedido)) {
            log.info("Removendo LoteBipado para os itensPedido: {}", idsItensPedido);
            List<Long> idsItensPedidoLong = idsItensPedido.stream().map(Integer::longValue).collect(Collectors.toList());
            repository.deleteByIdsItensPedido(idsItensPedidoLong);
        }
    }
    
    public void limparLoteBipadoPorNumeroPedido(Integer numeroPedido) {
    	repository.limparLoteBipadoPorNumeroPedido(numeroPedido);
    }
    
    public List<LoteBipadoDTO> buscarLotesPorPedido(Long numeroPedido) {
    	return repositoryImpl.buscarLotesPorPedido(numeroPedido);
    }
    
    public boolean existeLoteBipadoEOuReceitaExcedenteOuFaltante(Integer numeroPedido) {
    	atualizarCodigoItemNotaFiscal(numeroPedido.longValue());
    	return repositoryImpl.existeLoteBipadoEOuReceitaExcedenteOuFaltante(numeroPedido);
    }
    
    public void atualizarCodigoItemNotaFiscal(Long numeroPedido) {
    	if (repositoryImpl.existeDiferencaReferenciaItemNotaFiscal(numeroPedido))
    		repositoryImpl.atualizarCodigoItemNotaFiscal(numeroPedido);
    }

}
