package com.clique.retire.model.drogatel;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.clique.retire.enums.SimNaoEnum;
import com.clique.retire.enums.SituacaoAtividadeMotociclistaEnum;
import com.clique.retire.enums.converter.SituacaoAtividadeMotociclistaEnumConverter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Framework
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "MOTOCICLISTA_ATIVIDADE")
public class MotociclistaAtividade extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="MOAT_CD_MOTOCICLIS")
	private Integer codigo;

	@Enumerated(EnumType.STRING)
	@Column(name = "MOAT_FL_LIVRE")
	private SimNaoEnum livre;
	
	@Convert(converter=SituacaoAtividadeMotociclistaEnumConverter.class)
	@Column(name = "MOAT_FL_SITUACAO_ATIVIDADE")
	private SituacaoAtividadeMotociclistaEnum situacaoAtividade;
	
	@ManyToOne
	@JoinColumn(name="MODR_CD_MOTOCICLIS")
	private Motociclista motociclista;
	
	@ManyToOne
	@JoinColumn(name="POLO_CD_POLO")
	private Polo polo;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy= "motociclistaAtividade", cascade=CascadeType.ALL)
	private List<Expedicao> listExpedicao = new ArrayList<>();

}