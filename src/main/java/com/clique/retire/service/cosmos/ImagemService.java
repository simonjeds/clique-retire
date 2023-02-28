package com.clique.retire.service.cosmos;

import com.clique.retire.dto.UrlImagemDTO;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.repository.cosmos.ImagemRepository;
import com.clique.retire.service.drogatel.ParametroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Service
public class ImagemService {

  private final ImagemRepository imagemRepository;
  private final ParametroService parametroService;

  public String montarURLImagens(Integer codigoProduto, String urlBase) {
    try {
      Integer codigoImagem = imagemRepository.findImagemByCodigoProduto(codigoProduto);
      return Objects.nonNull(codigoImagem) ? urlBase.concat(codigoImagem.toString()) : null;
    } catch (Exception e) {
      log.error("Ocorreu um erro ao tentar buscar a imagem do produto '{}'", codigoProduto);
      return null;
    }
  }

  public void montarUrlImagensProdutos(List<? extends UrlImagemDTO> itensPedido) {
    String urlBase = parametroService.buscarPorChave(ParametroEnum.URL_SERVICO_IMAGENS.getDescricao()).getValor();
    itensPedido.forEach(produtoDTO -> {
      try {
        if (Objects.isNull(produtoDTO.getCodigoImagemProduto())) {
          Integer codigoImagem = imagemRepository.findImagemByCodigoProduto(produtoDTO.getCodigoProduto());
          produtoDTO.setCodigoImagemProduto(codigoImagem);
        }

        if (Objects.nonNull(produtoDTO.getCodigoImagemProduto())) {
          String urlImagem = urlBase.concat(produtoDTO.getCodigoImagemProduto().toString());
          produtoDTO.setUrlImagem(urlImagem);
        }
      } catch (Exception ex) {
        log.error("Não foi possível buscar a imagem do produto {}", produtoDTO.getCodigoProduto());
      }
    });
  }

}
