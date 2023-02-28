package com.clique.retire.service.drogatel;

import com.clique.retire.client.rest.MsAutorizacaoClient;
import com.clique.retire.dto.AutorizacaoResponseDTO;
import com.clique.retire.enums.CargoEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class AutorizacaoSuperVendedorService {

    private final MsAutorizacaoClient client;

    public boolean validarUsuario(String matricula, String senha) {
        try {
            String dataFormatada = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String credenciaisPuras = dataFormatada + ":" + matricula + ":" + senha;
            String encodeBytes = Base64.getEncoder().encodeToString(credenciaisPuras.getBytes());

            AutorizacaoResponseDTO autorizacaoUsuario = client.gerarAutorizacao(encodeBytes);

            return Objects.nonNull(autorizacaoUsuario)
                    && Objects.nonNull(autorizacaoUsuario.getAutorizacaoClienteResponse())
                    && Arrays.asList(
                            CargoEnum.GERENTE.getCodigo(),
                            CargoEnum.VENDEDORRESPONSAVEL.getCodigo(),
                            CargoEnum.SUBGERENTE.getCodigo()
                    ).contains(autorizacaoUsuario.getAutorizacaoClienteResponse().getCodigoCargo());
        } catch (Exception e) {
            log.error("Não foi possível validar a credencial do usuário.", e);
            return false;
        }
    }

}
