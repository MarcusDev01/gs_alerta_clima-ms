package br.com.fiap.alertaclima.controller;

import br.com.fiap.alertaclima.dto.request.AlertaRequest;
import br.com.fiap.alertaclima.dto.response.AlertaResponse;
import br.com.fiap.alertaclima.dto.response.ErroResponse;
import br.com.fiap.alertaclima.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
@Tag(name = "Alertas", description = "CRUD de alertas")
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping
    @Operation(summary = "Cadastrar")
    @ApiResponse(responseCode = "201", description = "Criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Recurso relacionado não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito com dados existentes", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<AlertaResponse> criar(@Valid @RequestBody AlertaRequest request) {
        AlertaResponse criado = alertaService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar todos")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<AlertaResponse>> listar() {
        return ResponseEntity.ok(alertaService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<AlertaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito com dados existentes", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<AlertaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AlertaRequest request) {
        return ResponseEntity.ok(alertaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir")
    @ApiResponse(responseCode = "204", description = "Excluído")
    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Possui registros vinculados", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alertaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
