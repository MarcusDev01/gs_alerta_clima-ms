package br.com.fiap.alertaclima.repository;

import br.com.fiap.alertaclima.entity.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    boolean existsByNomeIgnoreCaseAndEstadoIgnoreCase(String nome, String estado);

    boolean existsByNomeIgnoreCaseAndEstadoIgnoreCaseAndIdNot(String nome, String estado, Long id);
}
