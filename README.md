# Alerta Clima API ⛈️

> **Antecipe o risco. Proteja quem você ama.** API REST (Java + Spring Boot + SQL Server) do **Alerta Clima**, desenvolvida para a **Global Solution 2026.2 – CitizenTech** (FIAP), disciplina *Microservice and Web Engineering & IT Services* – turma 3SIR.

## 👥 Integrantes

| RM       | Nome                           |
| -------- | ------------------------------ |
| RM554616 | Marcus Vinicius Martinho Lucas |
| RM555421 | Henry Titotto                  |

## 🔗 Links da entrega

| Item                    | Link                                                                                                           |
| ----------------------- | -------------------------------------------------------------------------------------------------------------- |
| GitHub                  | https://github.com/MarcusDev01/gs_alerta_clima-ms                                                              |
| Docker Hub              | https://hub.docker.com/r/marcusvml/alerta-clima-api                                                            |
| API no Azure            | https://alerta-clima-api-554616-gjcghzbgf5dkg6fy.brazilsouth-01.azurewebsites.net                              |
| Swagger (Azure)         | https://alerta-clima-api-554616-gjcghzbgf5dkg6fy.brazilsouth-01.azurewebsites.net/swagger-ui/index.html        |
| OpenAPI JSON (Azure)    | https://alerta-clima-api-554616-gjcghzbgf5dkg6fy.brazilsouth-01.azurewebsites.net/v3/api-docs                  |
| Workflow (GitHub Actions) | https://github.com/MarcusDev01/gs_alerta_clima-ms/actions (*CD - Build e publicação no Docker Hub*)         |

---

## 💡 Objetivo da solução

Enchentes, deslizamentos e ondas de calor estão cada vez mais frequentes no Brasil, e a previsão do tempo comum (só números) não diz **qual é o risco** nem **o que fazer**. O Alerta Clima transforma a previsão em **avisos claros**, com nível de gravidade e orientações de prevenção.

Esta API é o back-end da solução. Ela guarda:

| Entidade          | Tabela           | O que representa                                                                                                |
| ----------------- | ---------------- | --------------------------------------------------------------------------------------------------------------- |
| **Cidade**        | `cidades`        | Cidade acompanhada (nome, UF, coordenadas, população, se tem área de risco)                                     |
| **Usuário**       | `usuarios`       | Pessoa que recebe os alertas (nome, e-mail, telefone, se quer SMS)                                              |
| **Monitoramento** | `monitoramentos` | Cidade que um usuário acompanha: onde mora, trabalha ou onde vivem familiares (apelido, favorita, notificações) |
| **Alerta**        | `alertas`        | Aviso de risco para uma cidade: tipo, nível, título, recomendação e período                                     |

**Relacionamentos:** Usuário 1:N Monitoramento N:1 Cidade (um usuário acompanha várias cidades e uma cidade é acompanhada por vários usuários) e Cidade 1:N Alerta.

**Tipos de alerta:** `CHUVA_FORTE`, `CALOR_EXTREMO`, `BAIXA_UMIDADE`, `VENTO_FORTE`, `RADIACAO_UV`, `FRIO_INTENSO`
**Níveis:** `ATENCAO`, `PERIGO`, `GRANDE_PERIGO`

### Regras de negócio (camada Service)

- Não permite e-mail de usuário repetido nem cidade repetida (mesmo nome + UF) → **409**.
- Um usuário não pode monitorar a mesma cidade duas vezes → **409**.
- A data de fim do alerta não pode ser anterior à data de início → **400**.
- Alerta com data de fim já passada é salvo como inativo.
- Não exclui cidade/usuário que tenha registros vinculados → **409**.

---

## 🏗️ Arquitetura

```
Controller  →  Service  →  Repository  →  SQL Server
    ↑  ↓          ↑
Request/Response DTO  ←ModelMapper→  Entity
```

```
src/main/java/br/com/fiap/alertaclima
├── config/        ModelMapper e OpenAPI
├── controller/    1 controller por entidade (+ redirecionamento de "/" para o Swagger)
├── dto/request/   DTOs de entrada com Bean Validation
├── dto/response/  DTOs de saída + ErroResponse padronizado
├── entity/        Entidades JPA (tabelas no plural)
├── enums/         TipoAlerta e NivelAlerta
├── exception/     Exceções de negócio e GlobalExceptionHandler
├── repository/    Spring Data JPA
└── service/       Regras de negócio
```

- As Controllers **só falam com os Services**, nunca com os Repositories.
- As entidades **nunca** são expostas: entrada por `*Request`, saída por `*Response` (mapeamento com **ModelMapper**).
- Rotas versionadas: `/api/v1/...`.

### Endpoints

| Método | Rota                     | Sucesso                           |
| ------ | ------------------------ | --------------------------------- |
| POST   | `/api/v1/{recurso}`      | 201 Created (+ header `Location`) |
| GET    | `/api/v1/{recurso}`      | 200 OK                            |
| GET    | `/api/v1/{recurso}/{id}` | 200 OK                            |
| PUT    | `/api/v1/{recurso}/{id}` | 200 OK                            |
| DELETE | `/api/v1/{recurso}/{id}` | 204 No Content                    |

`{recurso}` = `cidades`, `usuarios`, `monitoramentos`, `alertas`.

### Respostas de erro (padronizadas, sem stack trace)

| Situação                                                              | Status |
| --------------------------------------------------------------------- | ------ |
| Campos inválidos, JSON mal formado, id não numérico, regra de negócio | 400    |
| Recurso ou rota inexistente                                           | 404    |
| Método HTTP não suportado                                             | 405    |
| Dado duplicado / registro com vínculos                                | 409    |
| Falha inesperada (detalhe só no log do servidor)                      | 500    |

```json
{
  "timestamp": "2026-11-16T20:10:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos na requisição",
  "caminho": "/api/v1/usuarios",
  "campos": [ { "campo": "email", "mensagem": "E-mail inválido" } ]
}
```

### Documentação OpenAPI / Swagger

- Raiz **`/`** redireciona para o Swagger UI (`/swagger-ui/index.html`).
- JSON OpenAPI: `/v3/api-docs`
- Health check: `/actuator/health`

---

## ▶️ Como executar localmente

### Pré-requisitos

- Java 21 e Maven 3.9+ (ou só Docker)
- SQL Server (local, em Docker ou no Azure)

### Opção A – tudo com Docker Compose (mais fácil)

```bash
docker compose up --build
```

Sobe um SQL Server 2022, cria o banco `alertaclima` e a API em `http://localhost:8080` (abre direto no Swagger).

### Opção B – Spring Boot + SQL Server em Docker

1. Subir o SQL Server (senha **apenas para uso local/desenvolvimento**):

   ```bash
   docker run -d --name sqlserver -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=AlertaClima@2026" \
     -p 1433:1433 mcr.microsoft.com/mssql/server:2022-latest
   ```

2. Criar o banco (as tabelas são criadas pela aplicação):

   ```bash
   docker exec -it sqlserver /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "AlertaClima@2026" -C \
     -Q "CREATE DATABASE alertaclima"
   ```

   *(O script completo das tabelas está em `database/script.sql`.)*

3. Rodar a API com as variáveis de ambiente:

   ```bash
   # Linux/macOS
   export DB_URL="jdbc:sqlserver://localhost:1433;databaseName=alertaclima;encrypt=true;trustServerCertificate=true"
   export DB_USERNAME=sa
   export DB_PASSWORD='AlertaClima@2026'
   mvn spring-boot:run
   ```

   ```powershell
   # Windows PowerShell
   $env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=alertaclima;encrypt=true;trustServerCertificate=true"
   $env:DB_USERNAME="sa"
   $env:DB_PASSWORD="AlertaClima@2026"
   mvn spring-boot:run
   ```

4. Abrir `http://localhost:8080`.

### Variáveis de ambiente

| Variável      | Descrição                                 |
| ------------- | ----------------------------------------- |
| `DB_URL`      | URL JDBC do SQL Server                    |
| `DB_USERNAME` | Usuário do banco                          |
| `DB_PASSWORD` | Senha do banco                            |
| `PORT`        | Porta HTTP (padrão 8080)                  |
| `DDL_AUTO`    | Estratégia do Hibernate (padrão `update`) |

> Nenhuma credencial de produção fica no repositório: tudo vem de variáveis de ambiente / secrets. A senha acima é somente do SQL Server local em Docker.

### Testes automatizados

```bash
mvn test
```

Testes de integração (H2 em modo SQL Server) cobrindo o CRUD das 4 entidades, os códigos 201/200/204/400/404/409 e o redirecionamento de `/` para o Swagger.

---

## 🐳 Docker Hub

- **Repositório:** https://hub.docker.com/r/marcusvml/alerta-clima-api
- **Imagem:** `marcusvml/alerta-clima-api`
- **Tag principal:** `latest` (o workflow também publica uma tag com o número da execução)

Obter e executar a imagem:

```bash
docker pull marcusvml/alerta-clima-api:latest

docker run -p 8080:8080 \
  -e DB_URL="jdbc:sqlserver://<host>:1433;databaseName=alertaclima;encrypt=true;trustServerCertificate=true" \
  -e DB_USERNAME=<usuario> -e DB_PASSWORD=<senha> \
  marcusvml/alerta-clima-api:latest
```

Build manual (opcional):

```bash
docker build -t marcusvml/alerta-clima-api:latest .
docker push marcusvml/alerta-clima-api:latest
```

## ⚙️ Continuous Delivery – GitHub Actions

Arquivo: `.github/workflows/docker-publish.yml`
Histórico de execuções: https://github.com/MarcusDev01/gs_alerta_clima-ms/actions

A cada **push na branch `main`**:

1. Faz checkout e configura o Java 21;
2. Compila a aplicação (`mvn clean package`) e roda os testes (`mvn test`);
3. Faz login no Docker Hub com os secrets;
4. Constrói a imagem e publica em `marcusvml/alerta-clima-api` com a tag `latest` (e a tag com o número da execução).

**Secrets necessários** (*Settings → Secrets and variables → Actions*):

| Secret               | Valor                                                                                            |
| -------------------- | ------------------------------------------------------------------------------------------------ |
| `DOCKERHUB_USERNAME` | usuário do Docker Hub                                                                            |
| `DOCKERHUB_TOKEN`    | Access Token do Docker Hub (*Account settings → Personal access tokens*, permissão Read & Write) |

## ☁️ Implantação no Azure

**URL da API:** https://alerta-clima-api-554616-gjcghzbgf5dkg6fy.brazilsouth-01.azurewebsites.net

Foi usado **Azure SQL Database** + **Azure App Service for Containers** (imagem puxada do Docker Hub, região Brazil South).

```bash
az login
RG=rg-alertaclima
LOC=brazilsouth
SQL_SERVER=sql-alertaclima-554616
SQL_ADMIN=alertaadmin
SQL_PASS='<SenhaForte@123>'
APP=alerta-clima-api-554616
IMAGE=marcusvml/alerta-clima-api:latest

# 1) Grupo de recursos
az group create -n $RG -l $LOC

# 2) SQL Server + banco
az sql server create -g $RG -n $SQL_SERVER -l $LOC -u $SQL_ADMIN -p "$SQL_PASS"
az sql db create -g $RG -s $SQL_SERVER -n alertaclima --service-objective Basic
#    libera acesso dos serviços do Azure ao banco
az sql server firewall-rule create -g $RG -s $SQL_SERVER -n AllowAzure \
   --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0

# 3) App Service (Linux) rodando o container do Docker Hub
az appservice plan create -g $RG -n plan-alertaclima --is-linux --sku B1
az webapp create -g $RG -p plan-alertaclima -n $APP --container-image-name $IMAGE

# 4) Variáveis de ambiente da aplicação (credenciais ficam só no Azure)
az webapp config appsettings set -g $RG -n $APP --settings \
  WEBSITES_PORT=8080 \
  DB_URL="jdbc:sqlserver://$SQL_SERVER.database.windows.net:1433;database=alertaclima;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30" \
  DB_USERNAME=$SQL_ADMIN \
  DB_PASSWORD="$SQL_PASS"

az webapp restart -g $RG -n $APP
```

> Para atualizar a versão no Azure depois de um novo push: `az webapp restart -g $RG -n $APP` (o App Service puxa de novo a tag `latest`). Alternativa: **Azure Container Apps** (`az containerapp up -n $APP -g $RG --image $IMAGE --ingress external --target-port 8080 --env-vars DB_URL=... DB_USERNAME=... DB_PASSWORD=secretref:...`).

Logs, se algo não subir: `az webapp log tail -g $RG -n $APP`.

---

## 🧪 Como testar a solução

1. Acesse a URL do Azure (ou `http://localhost:8080`): abre o **Swagger**.
   https://alerta-clima-api-554616-gjcghzbgf5dkg6fy.brazilsouth-01.azurewebsites.net
2. Siga esta ordem (por causa dos relacionamentos):
   1. `POST /api/v1/cidades`
   2. `POST /api/v1/usuarios`
   3. `POST /api/v1/monitoramentos` (usa `usuarioId` e `cidadeId`)
   4. `POST /api/v1/alertas` (usa `cidadeId`)
3. Teste `GET` (lista e por id), `PUT` e `DELETE` em cada recurso.
4. Teste os erros: corpo vazio (400), id inexistente (404), e-mail repetido (409).
5. Comprove a persistência no SQL Server (Query editor do portal Azure):

   ```sql
   SELECT * FROM cidades; SELECT * FROM usuarios; SELECT * FROM monitoramentos; SELECT * FROM alertas;
   ```

O arquivo `testes-api.http` traz todas essas requisições prontas.

Exemplo com curl:

```bash
BASE=https://alerta-clima-api-554616-gjcghzbgf5dkg6fy.brazilsouth-01.azurewebsites.net/api/v1

# criar
curl -X POST $BASE/cidades -H "Content-Type: application/json" \
  -d '{"nome":"Petrópolis","estado":"RJ","latitude":-22.505,"longitude":-43.178,"areaDeRisco":true}'

# listar
curl $BASE/cidades

# consultar inexistente (esperado: 404)
curl -i $BASE/cidades/99999
```

---

## 🛠️ Tecnologias

Java 21 · Spring Boot 3.5 (Web, Data JPA, Validation, Actuator) · Microsoft SQL Server / Azure SQL · ModelMapper · springdoc-openapi (Swagger) · Lombok · JUnit 5 + MockMvc + H2 · Docker · GitHub Actions · Azure App Service

**ODS relacionados:** 3 (Saúde e bem-estar), 11 (Cidades e comunidades sustentáveis) e 13 (Ação contra a mudança global do clima).
