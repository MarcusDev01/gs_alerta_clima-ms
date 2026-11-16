package br.com.fiap.alertaclima.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI alertaClimaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Alerta Clima API")
                .version("v1")
                .description("""
                        API REST do Alerta Clima (Global Solution 2026.2 - CitizenTech).
                        Gerencia cidades monitoradas, usuários, os monitoramentos (cidades que cada \
                        usuário acompanha) e os alertas de risco climático: chuva forte, calor extremo, \
                        ar seco, vento forte, radiação UV e frio intenso.""")
                .contact(new Contact().name("Equipe Alerta Clima")));
    }
}
