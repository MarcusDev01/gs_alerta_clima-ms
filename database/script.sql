-- Alerta Clima - script de referência do SQL Server.
-- Observação: a aplicação já cria/atualiza as tabelas sozinha (spring.jpa.hibernate.ddl-auto=update).
-- Este script serve para criar o banco e, se quiser, criar as tabelas manualmente.

IF DB_ID('alertaclima') IS NULL CREATE DATABASE alertaclima;
GO
USE alertaclima;
GO

CREATE TABLE cidades (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome           VARCHAR(120)  NOT NULL,
    estado         VARCHAR(2)    NOT NULL,
    latitude       FLOAT         NOT NULL,
    longitude      FLOAT         NOT NULL,
    populacao      INT           NULL,
    area_de_risco  BIT           NOT NULL DEFAULT 0,
    data_cadastro  DATETIME2     NOT NULL
);

CREATE TABLE usuarios (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome           VARCHAR(120)  NOT NULL,
    email          VARCHAR(150)  NOT NULL UNIQUE,
    telefone       VARCHAR(20)   NOT NULL,
    receber_sms    BIT           NOT NULL DEFAULT 1,
    data_cadastro  DATETIME2     NOT NULL
);

CREATE TABLE monitoramentos (
    id                   BIGINT IDENTITY(1,1) PRIMARY KEY,
    apelido              VARCHAR(50)   NOT NULL,
    favorita             BIT           NOT NULL DEFAULT 0,
    notificacoes_ativas  BIT           NOT NULL DEFAULT 1,
    observacao           VARCHAR(255)  NULL,
    data_inicio          DATETIME2     NOT NULL,
    usuario_id           BIGINT        NOT NULL REFERENCES usuarios(id),
    cidade_id            BIGINT        NOT NULL REFERENCES cidades(id),
    CONSTRAINT uk_monitoramento_usuario_cidade UNIQUE (usuario_id, cidade_id)
);

CREATE TABLE alertas (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    tipo          VARCHAR(30)   NOT NULL,
    nivel         VARCHAR(20)   NOT NULL,
    titulo        VARCHAR(120)  NOT NULL,
    descricao     VARCHAR(500)  NULL,
    recomendacao  VARCHAR(500)  NULL,
    data_inicio   DATETIME2     NOT NULL,
    data_fim      DATETIME2     NULL,
    ativo         BIT           NOT NULL DEFAULT 1,
    cidade_id     BIGINT        NOT NULL REFERENCES cidades(id)
);
GO

-- Consulta para comprovar a persistência durante a apresentação:
-- SELECT * FROM cidades; SELECT * FROM usuarios; SELECT * FROM monitoramentos; SELECT * FROM alertas;
