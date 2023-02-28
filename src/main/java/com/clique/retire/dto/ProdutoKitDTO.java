package com.clique.retire.dto;

import com.clique.retire.model.drogatel.ItemPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoKitDTO {

    private String kit;
    private Integer sequencialKit;

    public static ProdutoKitDTO fromItemPedido(ItemPedido itemPedido) {
        return ProdutoKitDTO.builder()
                .kit(itemPedido.getKit())
                .sequencialKit(itemPedido.getSequencialKit())
                .build();
    }

    public boolean isPresentItemPedido(ItemPedido itemPedido) {
        return Objects.nonNull(kit) && Objects.nonNull(sequencialKit)
                && kit.equals(itemPedido.getKit()) && sequencialKit.equals(itemPedido.getSequencialKit());
    }

}
