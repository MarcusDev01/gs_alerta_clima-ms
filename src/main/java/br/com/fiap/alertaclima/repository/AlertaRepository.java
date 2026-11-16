package br.com.fiap.alertaclima.repository;

import br.com.fiap.alertaclima.entity.Alerta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    @EntityGraph(attributePaths = "cidade")
    List<Alerta> findAllByOrderByDataInicioDesc();

    boolean existsByCidadeId(Long cidadeId);
}
