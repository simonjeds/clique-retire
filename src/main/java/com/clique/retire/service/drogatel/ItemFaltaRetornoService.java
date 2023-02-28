package com.clique.retire.service.drogatel;

import com.clique.retire.model.drogatel.ItemFaltaRetorno;
import com.clique.retire.repository.drogatel.ItemFaltaRetornoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemFaltaRetornoService {

  private final ItemFaltaRetornoRepository repository;

  public void saveAll(Collection<ItemFaltaRetorno> itens) {
    this.repository.saveAll(itens);
  }

}
