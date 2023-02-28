package com.clique.retire.util;

public class Constantes {

  private Constantes() {
    throw new IllegalStateException("Utility class");
  }
  
  public static final Integer USUARIO_ADMINISTRADOR = 1;
  
  public static final String TIPO_PEDIDO_DROGATEL = "D";
  
  public static final String MOTIVO_DROGATEL_PRODUTO_EM_FALTA = "PRODUTO EM FALTA";
  public static final Integer CODIGO_PRODUTO_FORMULA_MANIPULADA = 90000;
  public static final Integer TIPO_FRETE_ENTREGA_VIA_MOTO_ID = 1;

  /**
   * FILAS DE PENDENCIA
   */
  public static final String FILA_RECEITA_NAO_RECOLHIDA = "ENTREGA 4.0 - Receita não recolhida";
  public static final String FILA_RECEITA_DIVERGENTE = "ENTREGA 4.0 - Receita divergente";
  public static final String FILA_CONTROLADO_NAO_ENTREGUE_NO_PRAZO = "SAC - Controlado não retirado no prazo";
  
  /**
   * REPOSITÓRIO
   */
  public static final String NUMERO_PEDIDO = "numeroPedido";
  
  /**
   * MENSAGENS INFORMATIVAS
   */
  public static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado.";
  public static final String ITENS_PEDIDO_NAO_ENCONTRADO = "Itens do pedido não foram encontrados.";

}
