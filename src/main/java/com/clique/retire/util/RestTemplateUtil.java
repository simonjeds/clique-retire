package com.clique.retire.util;

import com.clique.retire.client.rest.*;
import com.clique.retire.client.rest.impl.*;

public class RestTemplateUtil {

  public static BackofficeClient getBackofficeClient(String url) {
    return new BackofficeClientImpl(url);
  }

  public static ScheduleClient getScheduleClient(String url) {
    return new ScheduleClientImpl(url);
  }

  public static PedidoClient getPedidoClient(String url) {
    return new PedidoClientImpl(url);
  }

  public static PedidoDiretoClient getPedidoDiretoClient(String url) {
    return new PedidoDiretoClientImpl(url);
  }

  public static EmitirEtiquetaClient getGerarEtiquetaClient(String url) {
    return new EmitirEtiquetaClientImpl(url);
  }

  public static PrescritorClient getPrescritorClient(String url) {
    return new PrescritorClientImpl(url);
  }

  public static CaptacaoClient getCaptacaoClient(String url) {
    return new CaptacaoClientImpl(url);
  }

  public static CaptacaoDrogatelClient getCaptacaoDrogatelClient(String url) {
    return new CaptacaoDrogatelClientImpl(url);
  }

  public static LojasPreProducaoClient getLojasPreProducaoClient(String url) {
    return new LojasPreProducaoClientImpl(url);
  }

  public static RappiClient getRappiClient(String url) {
    return new RappiClientImpl(url);
  }

}
