package com.clique.retire.infra.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Data
@Builder
public class ProblemDetails {

    private int status;
    private String type = "error";
    private String title;
    private String message;
    private String detail;
    private List<Error> errors;

    public OffsetDateTime getTimestamp() {
        return OffsetDateTime.now();
    }

    public ProblemDetails addError(Error error) {
        if (Objects.isNull(this.errors)) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        return this;
    }

    @Data
    @Builder
    public static class Error {
        private String name;
        private String message;
    }

}
