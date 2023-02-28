package com.clique.retire.service.drogatel;

import com.clique.retire.dto.ModoParcelamentoDTO;
import com.clique.retire.dto.ParcelamentoDTO;
import com.clique.retire.repository.drogatel.ParcelamentoPedidoRepositoryImpl;
import com.clique.retire.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParcelamentoPedidoService {

    private final ParcelamentoPedidoRepositoryImpl repository;

    public List<ModoParcelamentoDTO> buscarParcelamentosDisponiveisProPedido(Long numeroPedido, double valorPedido) {
        List<ModoParcelamentoDTO> modosDePacelamento =
                repository.buscarParcelamentosDisponiveisProCartaoDoPedido(numeroPedido);

        modosDePacelamento.forEach(modoDeParcelamento -> {
            List<ParcelamentoDTO> parcelamentos = gerarParcelamentos(modoDeParcelamento, valorPedido);
            modoDeParcelamento.setParcelamentos(parcelamentos);
        });

        return modosDePacelamento.stream()
                .filter(modoDePacelamento -> CollectionUtils.isNotEmpty(modoDePacelamento.getParcelamentos()))
                .collect(Collectors.toList());
    }

    private List<ParcelamentoDTO> gerarParcelamentos(ModoParcelamentoDTO modoDeParcelamento, double valorTotalPedido) {
    	List<ParcelamentoDTO> parcelamentos = new ArrayList<>();
        if (valorTotalPedido < modoDeParcelamento.getValorMinimoParcelamento()) {
        	parcelamentos.add(definirParcela(1, valorTotalPedido, false));
        	return parcelamentos;
        }
        
    	parcelamentos.add(definirParcela(1, valorTotalPedido, false));
        for (int numeroParcelas = 2; numeroParcelas <= modoDeParcelamento.getNumeroMaximoParcelas(); numeroParcelas++) {
            double valorParcela = valorTotalPedido / numeroParcelas;
            boolean possuiJuros = false;

            if (numeroParcelas > modoDeParcelamento.getNumeroMaximoParcelasSemJuros()) {
                valorParcela *= 1 + (modoDeParcelamento.getValorPercentualJuros() / 100);
                possuiJuros = true;
            }

            if (valorParcela >= modoDeParcelamento.getValorMinimoPorParcela()) {
                parcelamentos.add(definirParcela(numeroParcelas, valorParcela, possuiJuros));
            }
        }

        return parcelamentos;
    }
    
    private ParcelamentoDTO definirParcela(Integer parcela, double valor, boolean possuiJuros) {
    	ParcelamentoDTO parcelamento = new ParcelamentoDTO();
        parcelamento.setNumeroParcelas(parcela);
        parcelamento.setValorParcela(NumberUtil.round(valor, 3));
        parcelamento.setPossuiJuros(possuiJuros);
        return parcelamento;
    }

}
