DROP TABLE matriculas CASCADE CONSTRAINTS;
DROP TABLE asignaturas CASCADE CONSTRAINTS;
DROP TABLE grupos CASCADE CONSTRAINTS;

CREATE TABLE asignaturas(
	nombre	  VARCHAR(10) PRIMARY KEY,
	nCreditos INTEGER
);

INSERT INTO asignaturas VALUES( 'FPROG', 9);
INSERT INTO asignaturas VALUES( 'OFIM', 6);

CREATE TABLE grupos(
	idGrupo 	INTEGER,
	asignatura	VARCHAR(10) REFERENCES asignaturas,
	plazasLibres	INTEGER,
	PRIMARY KEY( idGrupo, asignatura)
);

--Hacemos grupos de 4 alumnos para no tener que insertar muchas filas en
--la tabla matricula
INSERT INTO grupos VALUES( 1, 'FPROG', 4);
INSERT INTO grupos VALUES( 1, 'OFIM',  4);
INSERT INTO grupos VALUES( 2, 'FPROG', 4);
INSERT INTO grupos VALUES( 2, 'OFIM',  4);
INSERT INTO grupos VALUES( 10, 'OFIM',  4);

CREATE TABLE matriculas(
	idMatricula integer   PRIMARY KEY,
	alumno      VARCHAR(20) NOT NULL,
  asignatura  VARCHAR(10) NOT NULL,
  grupo	    INTEGER  NOT NULL,
  FOREIGN KEY( grupo, asignatura) REFERENCES grupos
);

drop sequence seq_matricula;
create sequence seq_matricula;

--Llenamos el grupo 1 de ofimatica
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'PEPE', 'OFIM', 1);
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'ANA', 'OFIM', 1);
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'JUAN', 'OFIM', 1);
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'LUIS', 'OFIM', 1);

UPDATE grupos SET plazasLibres=plazasLibres-4
WHERE asignatura='OFIM' AND idgrupo=1;

--Dejamos 1 plaza libre en el grupo 2 de ofimatica
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'ANTONIO', 'OFIM', 2);
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'MERCEDES', 'OFIM',  2);
INSERT INTO matriculas VALUES ( seq_matricula.nextval, 'JESUS', 'OFIM', 2);

UPDATE grupos SET plazasLibres=plazasLibres-3
WHERE asignatura='OFIM' AND idgrupo=2;

--Los grupos de FPROG quedan vacios (con sitio de sobra)

commit;
exit;
