package com.clique.retire.client.rest.impl;

import com.clique.retire.client.rest.RappiClient;
import com.clique.retire.dto.MarketplaceHandshakeResponseDTO;
import com.clique.retire.dto.PinMarketplaceDTO;
import feign.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class RappiClientImpl extends AbstractRestClient implements RappiClient {

  private static final String API_KEY = "psw#Framework_Araujo@2020";

  public RappiClientImpl(String baseUrl) {
    super(baseUrl);
  }

  @Override
  public MarketplaceHandshakeResponseDTO handShake(PinMarketplaceDTO pinMarketplaceDTO) {
    String url = "/api/v1/cliqueretire/handshake";

    final HttpHeaders headers = this.getHttpHeaders();
    headers.set("api_key", API_KEY);
    HttpEntity<PinMarketplaceDTO> httpEntity = new HttpEntity<>(pinMarketplaceDTO, headers);

    return this.getRestTemplate().postForObject(url, httpEntity, MarketplaceHandshakeResponseDTO.class);
  }

  @Override
  public void iniciarRetirada(Integer codigoPedidoDrogatel) {
    String url = "/api/v1/cliqueretire/iniciarRetirada/{codigoPedidoDrogatel}";

    HttpEntity<String> httpEntity = getHttpEntity();

    this.getRestTemplate().postForObject(url, httpEntity, Void.class, codigoPedidoDrogatel);
  }

  @Override
  public Response finalizarRetirada(PinMarketplaceDTO pinMarketPlaceDTO) {
    //String url = "/api/v1/cliqueretire/finalizarRetirada"

    //HttpEntity<String> httpEntity = this.getHttpEntity()

	//return this.getRestTemplate().postForObject(url, httpEntity, Void.class)
    return null;
  }
  
  private HttpEntity<String> getHttpEntity() {
	  final HttpHeaders headers = this.getHttpHeaders();
	  headers.set("api_key", API_KEY);
	  
	  return new HttpEntity<>(headers);
  }

}
