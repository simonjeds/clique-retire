package com.clique.retire.dto;

import com.clique.retire.config.gson.GsonIgnore;
import com.clique.retire.model.drogatel.ItemPedido;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ItemFaltaDTO {

    @EqualsAndHashCode.Include
    private Integer codigoItem;
    private Integer quantidadeFalta;
    private Integer codigoProduto;
    private Integer quantidadePedido;
    @GsonIgnore
    private ItemPedido itemPedidoRelacionado;
    private boolean isPbm;
    private Integer quantidadeFaltaTotal;

    public ItemFaltaDTO deepClone() {
        ItemFaltaDTO clone = new ItemFaltaDTO();
        clone.codigoItem = this.codigoItem;
        clone.quantidadeFalta = this.quantidadeFalta;
        clone.codigoProduto = this.codigoProduto;
        clone.quantidadePedido = this.quantidadePedido;
        clone.itemPedidoRelacionado = this.itemPedidoRelacionado;
        clone.isPbm = this.isPbm;
        return clone;
    }

}
