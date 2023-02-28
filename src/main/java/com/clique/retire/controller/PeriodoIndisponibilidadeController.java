package com.clique.retire.controller;

import com.clique.retire.dto.BaseResponseDTO;
import com.clique.retire.dto.PeriodoIndisponibilidadeDTO;
import com.clique.retire.model.drogatel.PeriodoIndisponibilidade;
import com.clique.retire.service.drogatel.PeriodoIndisponibilidadeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "rest/periodo-indisponibilidade", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class PeriodoIndisponibilidadeController extends BaseController {

  private final PeriodoIndisponibilidadeService service;

  public PeriodoIndisponibilidadeController(PeriodoIndisponibilidadeService service) {
    this.service = service;
  }

	@GetMapping("listar")
	public ResponseEntity<BaseResponseDTO> buscarTodos() {
		this.validarMatricula();
		List<PeriodoIndisponibilidadeDTO> periodos = this.service.listarTodos().stream()
				.map(PeriodoIndisponibilidadeDTO::new).collect(Collectors.toList());
		Map<String, Object> body = new HashMap<>();
		body.put("periodos", periodos);
		body.put("filtroMeses", service.getFiltroQuantidadeDeMeses());

		return ok(body);
	}

  @PostMapping("criar")
  public ResponseEntity<BaseResponseDTO> salvar(@Valid @RequestBody PeriodoIndisponibilidadeDTO periodoIndisponibilidadeDTO) {
    this.validarMatricula();
    Integer codigoUsuario = this.getCodigoUsuarioLogado();
    PeriodoIndisponibilidade periodo = this.service.salvar(periodoIndisponibilidadeDTO.toEntity(codigoUsuario));
    return ok(new PeriodoIndisponibilidadeDTO(periodo));
  }

  @DeleteMapping("excluir/{id}")
  public ResponseEntity<BaseResponseDTO> excluir(@PathVariable Long id) {
    this.validarMatricula();
    this.service.deletarPorId(id);
    return ok(SUCCESS);
  }

  private void validarMatricula() {
    this.service.validarMatricula(getCodigoUsuarioLogado());
  }

}
