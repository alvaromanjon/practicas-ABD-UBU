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


SET SERVEROUTPUT ON
/*Procedure que resetea secuencias
 El argumento es un cadena con el nombre de la secuencia
*/
create or replace procedure reset_seq( p_seq_name varchar ) is
--From https://stackoverflow.com/questions/51470/how-do-i-reset-a-sequence-in-oracle
    l_val number;
begin
    --Averiguo cual es el siguiente valor y lo guardo en l_val
    execute immediate
    'select ' || p_seq_name || '.nextval from dual' INTO l_val;

    --Utilizo ese valor en negativo para poner la secuencia cero, pimero cambiando el incremento de la secuencia
    execute immediate
    'alter sequence ' || p_seq_name || ' increment by -' || l_val ||
                                                          ' minvalue 0';
   --segundo pidiendo el siguiente valor
    execute immediate
    'select ' || p_seq_name || '.nextval from dual' INTO l_val;

    --restauro el incremento de la secuencia a 1
    execute immediate
    'alter sequence ' || p_seq_name || ' increment by 1 minvalue 0';

end;
/

create or replace procedure reiniciaTodo is
begin
    /*
    --Esto a la larga puede dar errores no documentados por Oracle, mejor resetear la secuencia con el procedimiento anterior    
    execute immediate 'drop sequence seq_pistas';
    execute immediate 'create sequence seq_pistas';
    */
    reset_seq('seq_pistas');
    
    delete from reservas;
    delete from pistas;
    
    insert into pistas values (seq_pistas.nextval);
    insert into reservas values (seq_pistas.currval, '22/03/2018', 12, 'Pepito');
    
    insert into pistas values (seq_pistas.nextval);
    insert into reservas values (seq_pistas.currval, '20/03/2018', 12, 'Pepito');    
    insert into reservas values (seq_pistas.currval, '22/03/2018', 12, 'Juan');
    
    insert into pistas values (seq_pistas.nextval);    
    insert into reservas values (seq_pistas.currval, '22/03/2018', 13, 'Lola');
    insert into reservas values (seq_pistas.currval, '22/03/2018', 12, 'Pepito');
    commit;
    
end;
/

create or replace procedure test_anular_reserva is
begin  
  
  begin
    reiniciaTodo;
    dbms_output.put_line('');
    dbms_output.put_line('Test1: Anular reserva inexistente------------------');
    pAnularReservas('Nadie','20/03/2019', 0, 666);
    commit;
    dbms_output.put_line('MAL: Anula reserva inexistente.');
  exception
    when others then
      if SQLCODE = -20000 then
        dbms_output.put_line('BIEN: Detecta reserva inexistente.');
        dbms_output.put_line('Error nro '||SQLCODE);
        dbms_output.put_line('Mensaje '||SQLERRM);
      else
        dbms_output.put_line('MAL: Da error pero no detecta reserva inexistente.');
        dbms_output.put_line('Error nro '||SQLCODE);
        dbms_output.put_line('Mensaje '||SQLERRM);
      end if;
  end;
  
  declare
    filas integer;
  begin
    reiniciaTodo;
    dbms_output.put_line('');
    dbms_output.put_line('Test2: Anular reserva existente------------------');
    pAnularReservas('Pepito', '22/03/2018', 12, 3);
    rollback;
    
    select count(*) 
    into filas
    from reservas;
    commit;
    
    if filas = 4 then
      dbms_output.put_line('BIEN: Anula reserva existente.');
    else
      dbms_output.put_line('MAL: No anula reserva existente.');
    end if;
  exception
    when others then
      dbms_output.put_line('MAL: Da error');
      dbms_output.put_line('Error nro '||SQLCODE);
      dbms_output.put_line('Mensaje '||SQLERRM);
  end;
end;
/

/*
    Completa el test según las indicaciones
*/
create or replace procedure test_reservar_pista is
begin
  
  begin
    /*
    Este test intentaría la reserva
    pReservarpistas('Nadie','22/03/2018', 12);
    y tiene que comporbar que devuelve la excepcion -200001    
    */
    null;--quita esta linea cuando lo implementes (es solo para que de momento compile)
  end;
  
  declare
    filas integer;
  begin
    /*
    Este test hará satisfactiriamente la reserva
    pReservarpistas('Pepito', '20/03/2018', 16);
    que no ha de devolver ninguna excepcion
    
    Ademas comprobará mediante un cursor que la reserva se ha registrado en la BD
    */
    null;--quita esta linea cuando lo implementes (es solo para que de momento compile)
    
  end;
end;
/

begin
  test_anular_reserva;
  test_reservar_pista;
end;
/

select * from reservas;

