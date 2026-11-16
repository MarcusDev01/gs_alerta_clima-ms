package br.com.fiap.alertaclima.config;

import br.com.fiap.alertaclima.dto.request.AlertaRequest;
import br.com.fiap.alertaclima.dto.request.MonitoramentoRequest;
import br.com.fiap.alertaclima.entity.Alerta;
import br.com.fiap.alertaclima.entity.Monitoramento;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        // Os relacionamentos são resolvidos na camada Service (busca no banco pelos ids),
        // por isso o mapeamento Request -> Entity ignora id e objetos relacionados.
        // emptyTypeMap + implicitMappings garante que os skips sejam aplicados antes do mapeamento implícito.
        mapper.emptyTypeMap(MonitoramentoRequest.class, Monitoramento.class)
                .addMappings(m -> {
                    m.skip(Monitoramento::setId);
                    m.skip(Monitoramento::setUsuario);
                    m.skip(Monitoramento::setCidade);
                })
                .implicitMappings();
        mapper.emptyTypeMap(AlertaRequest.class, Alerta.class)
                .addMappings(m -> {
                    m.skip(Alerta::setId);
                    m.skip(Alerta::setCidade);
                })
                .implicitMappings();
        return mapper;
    }
}
