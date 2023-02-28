package com.clique.retire.service.drogatel;

import com.clique.retire.model.drogatel.MovimentoProdutoPedido;
import com.clique.retire.repository.drogatel.MovimentoProdutoPedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentoProdutoPedidoService {

    private final MovimentoProdutoPedidoRepository repository;

    @Transactional
    public void salvarTodos(List<MovimentoProdutoPedido> movimentosProdutosPedido) {
        repository.saveAll(movimentosProdutosPedido);
    }

}
