package br.com.fiap.alertaclima.service;

import br.com.fiap.alertaclima.dto.request.AlertaRequest;
import br.com.fiap.alertaclima.dto.response.AlertaResponse;
import br.com.fiap.alertaclima.entity.Alerta;
import br.com.fiap.alertaclima.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertaclima.exception.RegraNegocioException;
import br.com.fiap.alertaclima.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final CidadeService cidadeService;
    private final ModelMapper modelMapper;

    @Transactional
    public AlertaResponse criar(AlertaRequest request) {
        validarPeriodo(request);
        Alerta alerta = modelMapper.map(request, Alerta.class);
        alerta.setCidade(cidadeService.buscarEntidade(request.getCidadeId()));
        alerta.setAtivo(calcularAtivo(request));
        return paraResponse(alertaRepository.save(alerta));
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listar() {
        return alertaRepository.findAllByOrderByDataInicioDesc().stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public AlertaResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional
    public AlertaResponse atualizar(Long id, AlertaRequest request) {
        Alerta alerta = buscarEntidade(id);
        validarPeriodo(request);
        modelMapper.map(request, alerta);
        alerta.setDescricao(request.getDescricao());
        alerta.setRecomendacao(request.getRecomendacao());
        alerta.setDataFim(request.getDataFim());
        alerta.setCidade(cidadeService.buscarEntidade(request.getCidadeId()));
        alerta.setAtivo(calcularAtivo(request));
        return paraResponse(alertaRepository.save(alerta));
    }

    @Transactional
    public void excluir(Long id) {
        alertaRepository.delete(buscarEntidade(id));
    }

    /** Regra: o fim do alerta não pode ser anterior ao início. */
    private void validarPeriodo(AlertaRequest request) {
        if (request.getDataFim() != null && request.getDataFim().isBefore(request.getDataInicio())) {
            throw new RegraNegocioException("A data de fim do alerta não pode ser anterior à data de início");
        }
    }

    /** Regra: alerta com data de fim já passada é gravado como inativo. */
    private Boolean calcularAtivo(AlertaRequest request) {
        if (request.getDataFim() != null && request.getDataFim().isBefore(LocalDateTime.now())) {
            return false;
        }
        return request.getAtivo() != null ? request.getAtivo() : Boolean.TRUE;
    }

    private Alerta buscarEntidade(Long id) {
        return alertaRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Alerta", id));
    }

    private AlertaResponse paraResponse(Alerta alerta) {
        return modelMapper.map(alerta, AlertaResponse.class);
    }
}
