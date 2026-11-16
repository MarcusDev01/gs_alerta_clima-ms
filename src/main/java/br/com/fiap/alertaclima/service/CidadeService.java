package br.com.fiap.alertaclima.service;

import br.com.fiap.alertaclima.dto.request.CidadeRequest;
import br.com.fiap.alertaclima.dto.response.CidadeResponse;
import br.com.fiap.alertaclima.entity.Cidade;
import br.com.fiap.alertaclima.exception.ConflitoException;
import br.com.fiap.alertaclima.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertaclima.repository.AlertaRepository;
import br.com.fiap.alertaclima.repository.CidadeRepository;
import br.com.fiap.alertaclima.repository.MonitoramentoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CidadeService {

    private final CidadeRepository cidadeRepository;
    private final AlertaRepository alertaRepository;
    private final MonitoramentoRepository monitoramentoRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CidadeResponse criar(CidadeRequest request) {
        normalizar(request);
        if (cidadeRepository.existsByNomeIgnoreCaseAndEstadoIgnoreCase(request.getNome(), request.getEstado())) {
            throw new ConflitoException("Cidade " + request.getNome() + "/" + request.getEstado() + " já cadastrada");
        }
        Cidade cidade = modelMapper.map(request, Cidade.class);
        return paraResponse(cidadeRepository.save(cidade));
    }

    @Transactional(readOnly = true)
    public List<CidadeResponse> listar() {
        return cidadeRepository.findAll(Sort.by("nome")).stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public CidadeResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional
    public CidadeResponse atualizar(Long id, CidadeRequest request) {
        Cidade cidade = buscarEntidade(id);
        normalizar(request);
        if (cidadeRepository.existsByNomeIgnoreCaseAndEstadoIgnoreCaseAndIdNot(request.getNome(), request.getEstado(), id)) {
            throw new ConflitoException("Já existe outra cidade " + request.getNome() + "/" + request.getEstado());
        }
        modelMapper.map(request, cidade);
        cidade.setPopulacao(request.getPopulacao());
        cidade.setAreaDeRisco(request.getAreaDeRisco() != null ? request.getAreaDeRisco() : Boolean.FALSE);
        return paraResponse(cidadeRepository.save(cidade));
    }

    @Transactional
    public void excluir(Long id) {
        Cidade cidade = buscarEntidade(id);
        if (alertaRepository.existsByCidadeId(id) || monitoramentoRepository.existsByCidadeId(id)) {
            throw new ConflitoException("A cidade possui alertas ou monitoramentos vinculados; exclua-os antes");
        }
        cidadeRepository.delete(cidade);
    }

    /** Usado pelos outros services para resolver o relacionamento pelo id. */
    @Transactional(readOnly = true)
    public Cidade buscarEntidade(Long id) {
        return cidadeRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Cidade", id));
    }

    private void normalizar(CidadeRequest request) {
        request.setNome(request.getNome().trim());
        request.setEstado(request.getEstado().trim().toUpperCase());
    }

    private CidadeResponse paraResponse(Cidade cidade) {
        return modelMapper.map(cidade, CidadeResponse.class);
    }
}
