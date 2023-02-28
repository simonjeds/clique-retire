package com.clique.retire.model.drogatel;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.clique.retire.enums.TipoProblemaEnum;
import com.clique.retire.enums.converter.TipoProblemaEnumConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Builder
@EqualsAndHashCode(callSuper=false, onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "DRGTBLPSPPROBLEMSEPARAPED")
public class ProblemaSeparacaoPedido extends BaseEntityAraujo {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PSPSEQ")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "pedi_nr_pedido")
	private Pedido pedido;

	@ManyToOne
	@JoinColumn(name = "prme_cd_produto")
	private Produto produto;

	@Column(name = "PSPIDCPROBLEMA")
	@Convert(converter = TipoProblemaEnumConverter.class)
	private TipoProblemaEnum tipoProblema;

	public ProblemaSeparacaoPedido(String codigoUsuario) {
		super(codigoUsuario);
	}

}