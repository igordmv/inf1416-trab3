DROP TABLE IF EXISTS Registro;
DROP TABLE IF EXISTS Mensagem;

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




INSERT INTO Mensagem VALUES(1001, 'Sistema iniciado.');
INSERT INTO Mensagem VALUES(1002, 'Sistema encerrado.');