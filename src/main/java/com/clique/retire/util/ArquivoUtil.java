package com.clique.retire.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArquivoUtil {

    public static String lerArquivo(String arquivo) {
        try {
            Resource resource = new ClassPathResource(arquivo);
            return IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            log.info("Arquivo {} não encontrado.", arquivo);
            throw new UncheckedIOException(e);
        }
    }

}
