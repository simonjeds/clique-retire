package com.clique.retire.client.soap;

import com.clique.retire.config.xmlmapper.AbstractSoapClient;
import com.clique.retire.dto.SolicitacaoAutorizacaoConvenioDTO;
import com.clique.retire.dto.ValidarRegrasAutorizacaoNormalDTO;
import com.clique.retire.dto.ValidarRegrasAutorizacaoNormalResponseDTO;
import com.clique.retire.dto.ValidarRegrasAutorizacaoNormalResponseDTO.ConvenioResponse;
import com.clique.retire.enums.ParametroEnum;
import com.clique.retire.model.drogatel.DrogatelParametro;
import com.clique.retire.service.drogatel.ParametroService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ConvenioDrogatelClient extends AbstractSoapClient {

    private static final String NAMESPACE = "http://client.interfacedrogatel.business.convenios.araujo.com/";

    private final String urlWsConvenio;

    public ConvenioDrogatelClient(ParametroService parametroService) {
        super(new RestTemplate(), NAMESPACE);
        DrogatelParametro parametroUrl = parametroService
                .buscarPorChave(ParametroEnum.URL_WS_CONVENIO_DROGATEL.getDescricao());
        this.urlWsConvenio = parametroUrl.getValor();
    }

    public ConvenioResponse validarRegrasAutorizacaoNormal(SolicitacaoAutorizacaoConvenioDTO solicitacao) {
        ValidarRegrasAutorizacaoNormalDTO body = new ValidarRegrasAutorizacaoNormalDTO(solicitacao);
        String responseOperation = "validarRegrasAutorizacaoNormalResponse";
        ResponseEntity<String> response = this.callWs(urlWsConvenio, body);
        ValidarRegrasAutorizacaoNormalResponseDTO responseDTO =
                this.extractResponse(response, responseOperation, ValidarRegrasAutorizacaoNormalResponseDTO.class);
        return responseDTO.getConvenioResponse();
    }

}
