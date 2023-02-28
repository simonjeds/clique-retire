package com.clique.retire.enums;

public enum CargoEnum {
    SUBGERENTE(3L, "SUB-GERENTE"),
    VENDEDORRESPONSAVEL(27L, "VENDEDOR RESPONSAVEL"),
    GERENTE(2L, "GERENTE");

    private Long codigo;
    private String descricao;

    private CargoEnum(Long codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}