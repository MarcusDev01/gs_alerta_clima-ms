package br.com.fiap.alertaclima;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AlertaClimaApiTests {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private long criar(String rota, String corpo) throws Exception {
        String resp = mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(resp);
        return node.get("id").asLong();
    }

    @Test
    void fluxoCompletoCrud() throws Exception {
        long cidade = criar("/api/v1/cidades",
                "{\"nome\":\"Petrópolis\",\"estado\":\"rj\",\"latitude\":-22.505,\"longitude\":-43.178,\"populacao\":278881,\"areaDeRisco\":true}");
        long usuario = criar("/api/v1/usuarios",
                "{\"nome\":\"Maria Oliveira\",\"email\":\"Maria@Email.com\",\"telefone\":\"21987654321\"}");
        long monit = criar("/api/v1/monitoramentos",
                "{\"usuarioId\":" + usuario + ",\"cidadeId\":" + cidade + ",\"apelido\":\"Casa\",\"favorita\":true}");
        long alerta = criar("/api/v1/alertas",
                "{\"cidadeId\":" + cidade + ",\"tipo\":\"CHUVA_FORTE\",\"nivel\":\"GRANDE_PERIGO\",\"titulo\":\"Temporal\",\"dataInicio\":\"2030-01-10T12:00:00\",\"dataFim\":\"2030-01-11T06:00:00\"}");

        mvc.perform(get("/api/v1/cidades/" + cidade)).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RJ"))
                .andExpect(jsonPath("$.dataCadastro").exists());
        mvc.perform(get("/api/v1/usuarios/" + usuario)).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.receberSms").value(true));
        mvc.perform(get("/api/v1/monitoramentos/" + monit)).andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioNome").value("Maria Oliveira"))
                .andExpect(jsonPath("$.cidadeNome").value("Petrópolis"))
                .andExpect(jsonPath("$.notificacoesAtivas").value(true));
        mvc.perform(get("/api/v1/alertas")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cidadeNome").value("Petrópolis"))
                .andExpect(jsonPath("$[0].ativo").value(true));

        // PUT
        mvc.perform(put("/api/v1/alertas/" + alerta).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cidadeId\":" + cidade + ",\"tipo\":\"CHUVA_FORTE\",\"nivel\":\"PERIGO\",\"titulo\":\"Temporal (rebaixado)\",\"dataInicio\":\"2030-01-10T12:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivel").value("PERIGO"))
                .andExpect(jsonPath("$.dataFim").doesNotExist());
        mvc.perform(put("/api/v1/monitoramentos/" + monit).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":" + usuario + ",\"cidadeId\":" + cidade + ",\"apelido\":\"Trabalho\",\"favorita\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apelido").value("Trabalho"))
                .andExpect(jsonPath("$.usuarioId").value(usuario));

        // cidade com vínculos não pode ser excluída
        mvc.perform(delete("/api/v1/cidades/" + cidade)).andExpect(status().isConflict());

        // DELETE
        mvc.perform(delete("/api/v1/alertas/" + alerta)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/monitoramentos/" + monit)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/usuarios/" + usuario)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/cidades/" + cidade)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/cidades/" + cidade)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem", containsString("não encontrad")));
    }

    @Test
    void errosPadronizados() throws Exception {
        mvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.trace").doesNotExist());
        mvc.perform(post("/api/v1/alertas").contentType(MediaType.APPLICATION_JSON).content("{\"tipo\":\"TORNADO\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/alertas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cidadeId\":999,\"tipo\":\"VENTO_FORTE\",\"nivel\":\"ATENCAO\",\"titulo\":\"x\",\"dataInicio\":\"2030-01-10T12:00:00\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/v1/usuarios/abc")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/usuarios/999").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Fulano\",\"email\":\"f@f.com\",\"telefone\":\"11999999999\"}"))
                .andExpect(status().isNotFound());

        long c = criar("/api/v1/cidades", "{\"nome\":\"Recife\",\"estado\":\"PE\",\"latitude\":-8.05,\"longitude\":-34.9}");
        mvc.perform(post("/api/v1/alertas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cidadeId\":" + c + ",\"tipo\":\"CALOR_EXTREMO\",\"nivel\":\"ATENCAO\",\"titulo\":\"x\",\"dataInicio\":\"2030-01-10T12:00:00\",\"dataFim\":\"2030-01-09T12:00:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem", containsString("data de fim")));
        mvc.perform(post("/api/v1/cidades").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"recife\",\"estado\":\"pe\",\"latitude\":-8.05,\"longitude\":-34.9}"))
                .andExpect(status().isConflict());
    }

    @Test
    void raizRedirecionaParaSwagger() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/alertas/{id}']").exists());
    }
}
