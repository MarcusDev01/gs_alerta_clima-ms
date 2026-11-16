package br.com.fiap.alertaclima.repository;

import br.com.fiap.alertaclima.entity.Monitoramento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoramentoRepository extends JpaRepository<Monitoramento, Long> {

    @EntityGraph(attributePaths = {"usuario", "cidade"})
    List<Monitoramento> findAllByOrderByIdAsc();

    boolean existsByUsuarioIdAndCidadeId(Long usuarioId, Long cidadeId);

    boolean existsByUsuarioIdAndCidadeIdAndIdNot(Long usuarioId, Long cidadeId, Long id);

    boolean existsByUsuarioId(Long usuarioId);

    boolean existsByCidadeId(Long cidadeId);
}
