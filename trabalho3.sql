DROP TABLE IF EXISTS Registro;
DROP TABLE IF EXISTS Mensagem;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Grupo;

CREATE TABLE Grupo (
    gid INTEGER PRIVATE KEY,
    name TEXT NOT NULL
);

INSERT INTO Grupo VALUES(1,"Administrador");
INSERT INTO Grupo VALUES(2,"Usuario");

CREATE TABLE User (
  name TEXT NOT NULL,
  email TEXT PRIVATE KEY NOT NULL ,
  grupoId INTEGER NOT NULL,
  salt TEXT NOT NULL,
  passwordDigest TEXT NOT NULL,
  numberWrongAccessPassword INTEGER DEFAULT 0,
  numberWrongAccessPrivateKey INTEGER DEFAULT 0,
  lastTryWrongAcess DATETIME,
  countAccess INTEGER DEFAULT 0,
  countConsult INTEGER DEFAULT 0,
  certificate TEXT NOT NULL,
  FOREIGN KEY(grupoId) REFERENCES Grupo(gid)
);

INSERT INTO "User" VALUES('Administrador','admin@inf1416.puc-rio.br',1,'A0ptRq7LmQ','6cefc90af4c2c175f15e4237acb01ce052baca54',0,0,NULL,0,0,'-----BEGIN CERTIFICATE-----
MIID9jCCAt6gAwIBAgIBATANBgkqhkiG9w0BAQsFADCBhDELMAkGA1UEBhMCQlIx
CzAJBgNVBAgMAlJKMQwwCgYDVQQHDANSaW8xDDAKBgNVBAoMA1BVQzEQMA4GA1UE
CwwHSU5GMTQxNjETMBEGA1UEAwwKQUMgSU5GMTQxNjElMCMGCSqGSIb3DQEJARYW
Y2FAZ3JhZC5pbmYucHVjLXJpby5icjAeFw0xOTA1MDMxNzE5MjhaFw0yMjA1MDIx
NzE5MjhaMHsxCzAJBgNVBAYTAkJSMQswCQYDVQQIDAJSSjEMMAoGA1UECgwDUFVD
MRAwDgYDVQQLDAdJTkYxNDE2MRYwFAYDVQQDDA1BZG1pbmlzdHJhdG9yMScwJQYJ
KoZIhvcNAQkBFhhhZG1pbkBpbmYxNDE2LnB1Yy1yaW8uYnIwggEiMA0GCSqGSIb3
DQEBAQUAA4IBDwAwggEKAoIBAQDDnq2WpTioReNQ3EapxCdmUt9khsS2BHf/YB7t
jGILCzQegnV1swvcH+xfd9FUjR7pORFSNvrfWKt93t3l2Dc0kCvVffh5BSnXIwwb
W94O+E1Yp6pvpyflj8YI+VLy0dNCiszHAF5ux6lRZYcrM4KiJndqeFRnqRP8zWI5
O1kJJMXzCqIXwmXtfqVjWiwXTnjU97xfQqKkmAt8Z+uxJaQxdZJBczmo/jQAIz1g
x+SXA4TshU5Ra4sQYLo5+FgAfA2vswHGXA6ba3N52wydZ2IYUJL2/YmTyfxzRnsy
uqbL+hcOw6bm+g0OEIIC7JduKpinz3BieiO15vameAJlqpedAgMBAAGjezB5MAkG
A1UdEwQCMAAwLAYJYIZIAYb4QgENBB8WHU9wZW5TU0wgR2VuZXJhdGVkIENlcnRp
ZmljYXRlMB0GA1UdDgQWBBSeUNmquC0OBxDLGpUaDNxe1t2EADAfBgNVHSMEGDAW
gBQjgTvDGSuVmdnK6jtr/hwkc8KCjjANBgkqhkiG9w0BAQsFAAOCAQEAYjji1ws7
7cw8uVhlUTkzVxyAaUKOgJx2zuvhR79MItH7L+7ocDrMB/tGCgoAhAM1gVeuyP2t
0j9mmRuuFDEFvsFqmOoSDbLFkxr1G8StujUQDrLe+691qU5RNubP3XacRyPVTA1F
/pSr/XUm4fymqDZyVcxqYPFewhQlL3VaD2bKeNWEAczgkOHkC3dDb9bCL4oDr1Ss
URKDWWg2XbZpuTO7IhxTYKwddKvsJTjizHIz6mi6JavHM7+xtB/ZvQaW04O9y5QI
9EQPJsF3nybVNKWIR9UA4tWSfHmQ5J9cGk/bZBCqzvgmV8Wv7cMUB7q6mzGUP1a+
HtNmSvQW9Uow3g==
-----END CERTIFICATE-----');

CREATE TABLE Mensagem (
    id INTEGER PRIMARY KEY,
    texto TEXT NOT NULL
  );

CREATE TABLE Registro (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    messageId INTEGER NOT NULL,
    email TEXT,
    filename TEXT,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(messageId) REFERENCES Mensagem(id)
);

INSERT INTO Mensagem VALUES(1001,"Sistema iniciado.");
INSERT INTO Mensagem VALUES(1002,"Sistema encerrado.");
INSERT INTO Mensagem VALUES(2001,"Autenticação etapa 1 iniciada.");
INSERT INTO Mensagem VALUES(2002,"Autenticação etapa 1 encerrada.");
INSERT INTO Mensagem VALUES(2003,"Login name <login_name> identificado com acesso liberado.");
INSERT INTO Mensagem VALUES(2004,"Login name <login_name> identificado com acesso bloqueado.");
INSERT INTO Mensagem VALUES(2005,"Login name <login_name> não identificado.");
INSERT INTO Mensagem VALUES(3001,"Autenticação etapa 2 iniciada para <login_name>.");
INSERT INTO Mensagem VALUES(3002,"Autenticação etapa 2 encerrada para <login_name>.");
INSERT INTO Mensagem VALUES(3003,"Senha pessoal verificada positivamente para <login_name>.");
INSERT INTO Mensagem VALUES(3004,"Primeiro erro da senha pessoal contabilizado para <login_name>.");
INSERT INTO Mensagem VALUES(3005,"Segundo erro da senha pessoal contabilizado para <login_name>.");
INSERT INTO Mensagem VALUES(3006,"Terceiro erro da senha pessoal contabilizado para <login_name>.");
INSERT INTO Mensagem VALUES(3007,"Acesso do usuario <login_name> bloqueado pela autenticação etapa 2.");
INSERT INTO Mensagem VALUES(4001,"Autenticação etapa 3 iniciada para <login_name>.");
INSERT INTO Mensagem VALUES(4002,"Autenticação etapa 3 encerrada para <login_name>.");
INSERT INTO Mensagem VALUES(4003,"Chave privada verificada positivamente para <login_name>.");
INSERT INTO Mensagem VALUES(4004,"Chave privada verificada negativamente para <login_name> (caminho inválido).");
INSERT INTO Mensagem VALUES(4005,"Chave privada verificada negativamente para <login_name> (frase secreta inválida).");
INSERT INTO Mensagem VALUES(4006,"Chave privada verificada negativamente para <login_name> (assinatura digital inválida).");
INSERT INTO Mensagem VALUES(4007,"Acesso do usuario <login_name> bloqueado pela autenticação etapa 3.");
INSERT INTO Mensagem VALUES(5001,"Tela principal apresentada para <login_name>.");
INSERT INTO Mensagem VALUES(5002,"Opção 1 do menu principal selecionada por <login_name>.");
INSERT INTO Mensagem VALUES(5003,"Opção 2 do menu principal selecionada por <login_name>.");
INSERT INTO Mensagem VALUES(5004,"Opção 3 do menu principal selecionada por <login_name>.");
INSERT INTO Mensagem VALUES(5005,"Opção 4 do menu principal selecionada por <login_name>.");
INSERT INTO Mensagem VALUES(6001,"Tela de cadastro apresentada para <login_name>.");
INSERT INTO Mensagem VALUES(6002,"Botão cadastrar pressionado por <login_name>.");
INSERT INTO Mensagem VALUES(6003,"Senha pessoal inválida fornecida por <login_name>.");
INSERT INTO Mensagem VALUES(6004,"Caminho do certificado digital inválido fornecido por <login_name>.");
INSERT INTO Mensagem VALUES(6005,"Confirmação de dados aceita por <login_name>.");
INSERT INTO Mensagem VALUES(6006,"Confirmação de dados rejeitada por <login_name>.");
INSERT INTO Mensagem VALUES(6007,"Botão voltar de cadastro para o menu principal pressionado por <login_name>.");
INSERT INTO Mensagem VALUES(7001,"Tela de alteração da senha pessoal e certificado apresentada para <login_name>.");
INSERT INTO Mensagem VALUES(7002,"Senha pessoal inválida fornecida por <login_name>.");
INSERT INTO Mensagem VALUES(7003,"Caminho do certificado digital inválido fornecido por <login_name>.");
INSERT INTO Mensagem VALUES(7004,"Confirmação de dados aceita por <login_name>.");
INSERT INTO Mensagem VALUES(7005,"Confirmação de dados rejeitada por <login_name>.");
INSERT INTO Mensagem VALUES(7006,"Botão voltar de carregamento para o menu principal pressionado por <login_name>.");
INSERT INTO Mensagem VALUES(8001,"Tela de consulta de arquivos secretos apresentada para <login_name>.");
INSERT INTO Mensagem VALUES(8002,"Botão voltar de consulta para o menu principal pressionado por <login_name>.");
INSERT INTO Mensagem VALUES(8003,"Botão Listar de consulta pressionado por <login_name>.");
INSERT INTO Mensagem VALUES(8004,"Caminho de pasta inválido fornecido por <login_name>.");
INSERT INTO Mensagem VALUES(8005,"Arquivo de índice decriptado com sucesso para <login_name>.");
INSERT INTO Mensagem VALUES(8006,"Arquivo de índice verificado (integridade e autenticidade) com sucesso para <login_name>.");
INSERT INTO Mensagem VALUES(8007,"Falha na decriptação do arquivo de índice para <login_name>.");
INSERT INTO Mensagem VALUES(8008,"Falha na verificação (integridade e autenticidade) do arquivo de índice para <login_name>.");
INSERT INTO Mensagem VALUES(8009,"Lista de arquivos presentes no índice apresentada para <login_name>.");
INSERT INTO Mensagem VALUES(8010,"Arquivo <arq_name> selecionado por <login_name> para decriptação.");
INSERT INTO Mensagem VALUES(8011,"Acesso permitido ao arquivo <arq_name> para <login_name>.");
INSERT INTO Mensagem VALUES(8012,"Acesso negado ao arquivo <arq_name> para <login_name>.");
INSERT INTO Mensagem VALUES(8013,"Arquivo <arq_name> decriptado com sucesso para <login_name>.");
INSERT INTO Mensagem VALUES(8014,"Arquivo <arq_name> verificado (integridade e autenticidade) com sucesso para <login_name>.");
INSERT INTO Mensagem VALUES(8015,"Falha na decriptação do arquivo <arq_name> para <login_name>.");
INSERT INTO Mensagem VALUES(8016,"Falha na verificação (integridade e autenticidade) do arquivo <arq_name> para <login_name>.");
INSERT INTO Mensagem VALUES(9001,"Tela de saída apresentada para <login_name>.");
INSERT INTO Mensagem VALUES(9002,"Saída não liberada por falta de one-time password para <login_name>.");
INSERT INTO Mensagem VALUES(9003,"Botão sair pressionado por <login_name>.");
INSERT INTO Mensagem VALUES(9004,"Botão voltar de sair para o menu principal pressionado por <login_name>");