package com.clique.retire.service.drogatel;

import static com.clique.retire.util.Constantes.PEDIDO_NAO_ENCONTRADO;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.text.MaskFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clique.retire.dto.EtiquetaDTO;
import com.clique.retire.dto.FilialDTO;
import com.clique.retire.dto.ProdutoEntradaDTO;
import com.clique.retire.dto.ReceitaSkuDTO;
import com.clique.retire.enums.SexoEnum;
import com.clique.retire.infra.exception.EntidadeNaoEncontradaException;
import com.clique.retire.model.drogatel.Pedido;
import com.clique.retire.model.drogatel.Usuario;
import com.clique.retire.util.ArquivoUtil;
import com.clique.retire.util.DateUtils;
import com.clique.retire.util.StringUtil;
import com.clique.retire.wrapper.IntWrapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class EmitirEtiquetaService {

  @Autowired
  private CaptacaoPedidoService captacaoPedidoService;
  
  @Autowired
  private PedidoService pedidoService;

  public String gerarEtiqueta(
    EtiquetaDTO etiquetaDTO, ReceitaSkuDTO receitaSkuDTO, FilialDTO filialDTO, List<ProdutoEntradaDTO> produtosEntrada
  ) {
    final LocalDateTime dataCaptacao = LocalDateTime.ofInstant(
      receitaSkuDTO.getDataCaptacao().toInstant(), ZoneId.of("America/Sao_Paulo")
    );
    final String templateEtiqueta = ArquivoUtil.lerArquivo("etiquetas/etiqueta.txt");

    String data = DateUtils.getDataFormatada(dataCaptacao);
    String hora = DateUtils.getHoraFormatada(dataCaptacao);
    String numeroDeAutorizacao = String.valueOf(etiquetaDTO.getNumeroAutorizacao());

    String telefoneLoja = "(" + filialDTO.getDdd().trim() + ") " + filialDTO.getTelefone().trim();
    String enderecoLoja = filialDTO.getEndereco().trim() + "-" + filialDTO.getBairro();
    String cidadeLoja = filialDTO.getCidade() + " - " + filialDTO.getSiglaEstado() + ", " + filialDTO.getCep();

    String nomeCliente = receitaSkuDTO.getNomeCliente().length() > 28
      ? receitaSkuDTO.getNomeCliente().substring(0, 28)
      : receitaSkuDTO.getNomeCliente();
    String enderecoCliente = receitaSkuDTO.getLogradouro() + "," + receitaSkuDTO.getNumero();
    String bairroCliente = StringUtils.defaultString(receitaSkuDTO.getComplemento()) + "-" + receitaSkuDTO.getBairro();
    String cidadeCliente =  receitaSkuDTO.getCidade() + "-"+ receitaSkuDTO.getUf() ;

    String paciente = receitaSkuDTO.isAntibiotico() && !receitaSkuDTO.getNumeroRegistro().startsWith("V")
      ? preencherPaciente(receitaSkuDTO)
      : "";

    Usuario vendedor = etiquetaDTO.getVendedor();
    Usuario conferente = etiquetaDTO.getVendedorConferente();

    String nomeVendedor = String.format("%s - %s", vendedor.getMatricula(), vendedor.getNome().split(" ")[0]);
    String nomeConferente = Objects.nonNull(conferente)
      ? String.format("%s - %s", conferente.getMatricula(), conferente.getNome().split(" ")[0])
      : "";

    String etiquetaFormatada = templateEtiqueta
      .replace("$Data", data)
      .replace("$Hora", hora)
      .replace("$N_Autorizacao", "null".equals(numeroDeAutorizacao) ? "" : numeroDeAutorizacao)
      .replace("$Pedidos", preencherProdutos(produtosEntrada))
      .replace("$Vendedor", nomeVendedor)
      .replace("$Farmaceutico", nomeConferente)
      .replace("$IdLoja", String.valueOf(filialDTO.getId()))
      .replace("$NomeLoja", filialDTO.getNome())
      .replace("$TelefoneLoja", telefoneLoja)
      .replace("$EnderecoLoja", enderecoLoja)
      .replace("$CidadeLoja", cidadeLoja)
      .replace("$CNPJ", filialDTO.getCnpj())
      .replace("$NomeCliente", nomeCliente)
      .replace("$IDCliente", receitaSkuDTO.getDocumentoPaciente())
      .replace("$EnderecoCliente", enderecoCliente)
      .replace("$BairroCliente", bairroCliente)
      .replace("$CidadeCliente", cidadeCliente)
      .replace("$TelefoneCliente", receitaSkuDTO.getCelular())
      .replace("$Paciente", paciente);

    try {
      MaskFormatter maskCPF = new MaskFormatter("###.###.###-##");
      maskCPF.setValueContainsLiteralCharacters(false);
      String cpf = maskCPF.valueToString(receitaSkuDTO.getCpfCnpjCliente());
      etiquetaFormatada = etiquetaFormatada.replace("$Cpf/Cnpj", cpf);
    } catch (ParseException ignored) { 
    	log.warn("Não foi possível definir a máscara para o CPF: {}", receitaSkuDTO.getCpfCnpjCliente());
    }

    return StringUtil.removerAcentos(etiquetaFormatada).toUpperCase();
  }

  /**
   * altura 95 no layout zpl aumentando de 20 em 20 para cada linha nova de pedido na receita
   *
   * @param produtosEntrada Lista de itens da receita
   * @return listagem de produtos
   */
  protected String preencherProdutos(List<ProdutoEntradaDTO> produtosEntrada) {
    final IntWrapper altura = new IntWrapper(95);
    final String templateProdutos = ArquivoUtil.lerArquivo("etiquetas/pedido.txt");

    return produtosEntrada.stream().map(produto -> {
      int alturaValue = altura.getValue();
      altura.setValue(alturaValue + 20);

      return templateProdutos
        .replace("$Altura", String.valueOf(alturaValue))
        .replace("$QTDE", String.valueOf(produto.getQuantidade()))
        .replace("$Produto", produto.getDescricaoResumida())
        .replace("$Lote", StringUtils.defaultString(produto.getNumeroLote()));
    }).collect(Collectors.joining());
  }

  protected String preencherPaciente(ReceitaSkuDTO receitaSkuDTO) {
    final String templatePaciente = ArquivoUtil.lerArquivo("etiquetas/paciente.txt");
    final String nomeCompletoPaciente = StringUtils.defaultString(receitaSkuDTO.getNomePaciente());
    final int limiteCaracteresNome = 28;

    StringBuilder nomePacienteBuilder = new StringBuilder();
    StringBuilder nomeExcedenteBuilder = new StringBuilder();

    if (nomeCompletoPaciente.length() <= limiteCaracteresNome) {
      nomePacienteBuilder.append(nomeCompletoPaciente);
    } else {
      for (String nome : nomeCompletoPaciente.split(" ")) {
        int soma = nomePacienteBuilder.length() + nome.length();
        if (soma > limiteCaracteresNome) {
          nomeExcedenteBuilder.append(nome).append(" ");
        } else {
          nomePacienteBuilder.append(nome).append(" ");
        }
      }
    }

    return templatePaciente
      .replace("$Sexo", SexoEnum.getValorPorSigla(receitaSkuDTO.getSexo()).getDescricao())
      .replace("$Idade", String.valueOf(receitaSkuDTO.getIdadePaciente()))
      .replace("$NomePaciente", nomePacienteBuilder.toString())
      .replace("$NomeExcedente", nomeExcedenteBuilder.toString());
  }

  public String imprimirEtiqueta(Long numeroPedido) {
	
	Pedido pedido = pedidoService.buscarPorId(numeroPedido);
	if(Objects.isNull(pedido))
		throw new EntidadeNaoEncontradaException(PEDIDO_NAO_ENCONTRADO);
	  
    captacaoPedidoService.gerarCaptacaoReceitaEEmitirEtiqueta(numeroPedido, true);
    return "Etiqueta impressa.";
  }

}
