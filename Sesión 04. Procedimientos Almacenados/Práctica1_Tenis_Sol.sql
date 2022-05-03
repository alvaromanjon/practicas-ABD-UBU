drop table reservas;
drop table pistas;
drop sequence seq_pistas;

create table pistas (
	nro integer primary key
	);
	
create table reservas (
	pista integer references pistas(nro),
	fecha date,
	hora integer check (hora >= 0 and hora <= 23),
	socio varchar(20),
	primary key (pista, fecha, hora)
	);
	
create sequence seq_pistas;

insert into pistas values (seq_pistas.nextval);
insert into reservas 
	values (seq_pistas.currval, '20/03/2018', 14, 'Pepito');
insert into pistas values (seq_pistas.nextval);
insert into reservas 
	values (seq_pistas.currval, '24/03/2018', 18, 'Pepito');
insert into reservas 
	values (seq_pistas.currval, '21/03/2018', 14, 'Juan');
insert into pistas values (seq_pistas.nextval);
insert into reservas 
	values (seq_pistas.currval, '22/03/2018', 13, 'Lola');
insert into reservas 
	values (seq_pistas.currval, '22/03/2018', 12, 'Pepito');

commit;

create or replace function anularReserva( 
	p_socio varchar,
	p_fecha date,
	p_hora integer, 
	p_pista integer ) 
return integer is

begin
	DELETE FROM reservas 
        WHERE
            fecha = p_fecha AND
            pista = p_pista AND
            hora = p_hora AND
            socio = p_socio;

	if sql%rowcount = 1 then
		commit;
		return 1;
	else
		rollback;
		return 0;
	end if;
end;
/

create or replace FUNCTION reservarPista(
        p_socio VARCHAR,
        p_fecha DATE,
        p_hora INTEGER
    ) 
RETURN INTEGER IS

    CURSOR vPistasLibres IS
        SELECT nro
        FROM pistas 
        WHERE nro NOT IN (
            SELECT pista
            FROM reservas
            WHERE 
                fecha = p_fecha AND
                hora = p_hora);
            
    vPista INTEGER;

BEGIN
    OPEN vPistasLibres;
    FETCH vPistasLibres INTO vPista;

    IF vPistasLibres%NOTFOUND
    THEN
        CLOSE vPistasLibres;
        RETURN 0;
    END IF;

    INSERT INTO reservas VALUES (vPista, p_fecha, p_hora, p_socio);
    CLOSE vPistasLibres;
    COMMIT;
    RETURN 1;
END;
/

--Bloque anónimo a) Paso 2
SET SERVEROUTPUT ON
declare
    resultado integer;
begin
    resultado:=reservarPista('Socio 1', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 1: OK');
    else
        dbms_output.put_line('Reserva 1: MAL');
    end if;
    
    resultado:=reservarPista('Socio 2', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 2: OK');
    else
        dbms_output.put_line('Reserva 2: MAL');
    end if;
    
    resultado:=reservarPista('Socio 3', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 3: OK');
    else
        dbms_output.put_line('Reserva 3: MAL');
    end if;
        
    resultado:=reservarPista('Socio 4', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 4: OK');
    else
        dbms_output.put_line('Reserva 4: MAL');
    end if;

--Bloque anónimo b) Paso 2
    resultado:=anularReserva('Socio 1', CURRENT_DATE, 12, 1);
    if resultado=1 then
        dbms_output.put_line('Reserva 1 anulada: OK');
    else
        dbms_output.put_line('Reserva 1 anulada: MAL');
    end if;
    
    resultado:=anularReserva('Socio 2', date '1920-1-1', 12, 1);
    if resultado=1 then
        dbms_output.put_line('Reserva 2 anulada: OK');
    else
        dbms_output.put_line('Reserva 2 anulada: MAL');
    end if;

end;
/

SELECT * FROM reservas;

--Para convertir en procedimiento
SET SERVEROUTPUT ON
--declare
create or replace procedure TEST_FUNCIONES_TENIS is
    resultado integer;
begin
    resultado:=reservarPista('Socio 1', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 1: OK');
    else
        dbms_output.put_line('Reserva 1: MAL');
    end if;
    
    resultado:=reservarPista('Socio 2', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 2: OK');
    else
        dbms_output.put_line('Reserva 2: MAL');
    end if;
    
    resultado:=reservarPista('Socio 3', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 3: OK');
    else
        dbms_output.put_line('Reserva 3: MAL');
    end if;
        
    resultado:=reservarPista('Socio 4', CURRENT_DATE, 12);
    if resultado=1 then
        dbms_output.put_line('Reserva 4: OK');
    else
        dbms_output.put_line('Reserva 4: MAL');
    end if;

--Bloque anónimo b) Paso 2
    resultado:=anularReserva('Socio 1', CURRENT_DATE, 12, 1);
    if resultado=1 then
        dbms_output.put_line('Reserva 1 anulada: OK');
    else
        dbms_output.put_line('Reserva 1 anulada: MAL');
    end if;
    
    resultado:=anularReserva('Socio 2', date '1920-1-1', 12, 1);
    if resultado=1 then
        dbms_output.put_line('Reserva 2 anulada: OK');
    else
        dbms_output.put_line('Reserva 2 anulada: MAL');
    end if;
end;
/
 --Forma 1 de ejecutar
begin
    TEST_FUNCIONES_TENIS;
end;
/

--Forma 2 de ejecutar
exec TEST_FUNCIONES_TENIS;