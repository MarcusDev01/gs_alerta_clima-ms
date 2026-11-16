package br.com.fiap.alertaclima.controller;

import br.com.fiap.alertaclima.dto.request.MonitoramentoRequest;
import br.com.fiap.alertaclima.dto.response.MonitoramentoResponse;
import br.com.fiap.alertaclima.dto.response.ErroResponse;
import br.com.fiap.alertaclima.service.MonitoramentoService;
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
@RequestMapping("/api/v1/monitoramentos")
@RequiredArgsConstructor
@Tag(name = "Monitoramentos", description = "CRUD de monitoramentos")
public class MonitoramentoController {

    private final MonitoramentoService monitoramentoService;

    @PostMapping
    @Operation(summary = "Cadastrar")
    @ApiResponse(responseCode = "201", description = "Criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Recurso relacionado não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito com dados existentes", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<MonitoramentoResponse> criar(@Valid @RequestBody MonitoramentoRequest request) {
        MonitoramentoResponse criado = monitoramentoService.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(criado.getId()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    @Operation(summary = "Listar todos")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<MonitoramentoResponse>> listar() {
        return ResponseEntity.ok(monitoramentoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar por id")
    @ApiResponse(responseCode = "200", description = "Encontrado")
    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<MonitoramentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(monitoramentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar")
    @ApiResponse(responseCode = "200", description = "Atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflito com dados existentes", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<MonitoramentoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody MonitoramentoRequest request) {
        return ResponseEntity.ok(monitoramentoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir")
    @ApiResponse(responseCode = "204", description = "Excluído")
    @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "409", description = "Possui registros vinculados", content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        monitoramentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
