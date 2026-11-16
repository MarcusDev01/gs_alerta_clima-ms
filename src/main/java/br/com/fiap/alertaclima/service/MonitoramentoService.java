package br.com.fiap.alertaclima.service;

import br.com.fiap.alertaclima.dto.request.MonitoramentoRequest;
import br.com.fiap.alertaclima.dto.response.MonitoramentoResponse;
import br.com.fiap.alertaclima.entity.Monitoramento;
import br.com.fiap.alertaclima.exception.ConflitoException;
import br.com.fiap.alertaclima.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertaclima.repository.MonitoramentoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoramentoService {

    private final MonitoramentoRepository monitoramentoRepository;
    private final UsuarioService usuarioService;
    private final CidadeService cidadeService;
    private final ModelMapper modelMapper;

    @Transactional
    public MonitoramentoResponse criar(MonitoramentoRequest request) {
        if (monitoramentoRepository.existsByUsuarioIdAndCidadeId(request.getUsuarioId(), request.getCidadeId())) {
            throw new ConflitoException("Este usuário já monitora esta cidade");
        }
        Monitoramento monitoramento = modelMapper.map(request, Monitoramento.class);
        monitoramento.setUsuario(usuarioService.buscarEntidade(request.getUsuarioId()));
        monitoramento.setCidade(cidadeService.buscarEntidade(request.getCidadeId()));
        return paraResponse(monitoramentoRepository.save(monitoramento));
    }

    @Transactional(readOnly = true)
    public List<MonitoramentoResponse> listar() {
        return monitoramentoRepository.findAllByOrderByIdAsc().stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public MonitoramentoResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional
    public MonitoramentoResponse atualizar(Long id, MonitoramentoRequest request) {
        Monitoramento monitoramento = buscarEntidade(id);
        if (monitoramentoRepository.existsByUsuarioIdAndCidadeIdAndIdNot(request.getUsuarioId(), request.getCidadeId(), id)) {
            throw new ConflitoException("Este usuário já monitora esta cidade em outro registro");
        }
        modelMapper.map(request, monitoramento);
        monitoramento.setObservacao(request.getObservacao());
        monitoramento.setUsuario(usuarioService.buscarEntidade(request.getUsuarioId()));
        monitoramento.setCidade(cidadeService.buscarEntidade(request.getCidadeId()));
        return paraResponse(monitoramentoRepository.save(monitoramento));
    }

    @Transactional
    public void excluir(Long id) {
        monitoramentoRepository.delete(buscarEntidade(id));
    }

    private Monitoramento buscarEntidade(Long id) {
        return monitoramentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Monitoramento", id));
    }

    private MonitoramentoResponse paraResponse(Monitoramento monitoramento) {
        return modelMapper.map(monitoramento, MonitoramentoResponse.class);
    }
}
