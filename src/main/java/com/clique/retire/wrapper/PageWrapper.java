package com.clique.retire.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageWrapper<T> {

  private List<T> conteudo;
  private int pagina;
  private int totalPaginas;

  public PageWrapper(List<T> conteudo, int pagina, int quantidadePorPagina, int totalRegistros) {
    this.conteudo = conteudo;
    this.pagina = pagina;
    this.totalPaginas = (int) Math.ceil((double) totalRegistros / quantidadePorPagina);
  }

}
