package br.com.fiap.alertaclima.service;

import br.com.fiap.alertaclima.dto.request.UsuarioRequest;
import br.com.fiap.alertaclima.dto.response.UsuarioResponse;
import br.com.fiap.alertaclima.entity.Usuario;
import br.com.fiap.alertaclima.exception.ConflitoException;
import br.com.fiap.alertaclima.exception.RecursoNaoEncontradoException;
import br.com.fiap.alertaclima.repository.MonitoramentoRepository;
import br.com.fiap.alertaclima.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final MonitoramentoRepository monitoramentoRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        request.setEmail(request.getEmail().trim().toLowerCase());
        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        Usuario usuario = modelMapper.map(request, Usuario.class);
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll(Sort.by("nome")).stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return paraResponse(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        request.setEmail(request.getEmail().trim().toLowerCase());
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail(), id)) {
            throw new ConflitoException("E-mail já cadastrado para outro usuário");
        }
        modelMapper.map(request, usuario);
        return paraResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        if (monitoramentoRepository.existsByUsuarioId(id)) {
            throw new ConflitoException("O usuário possui monitoramentos vinculados; exclua-os antes");
        }
        usuarioRepository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private UsuarioResponse paraResponse(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioResponse.class);
    }
}
