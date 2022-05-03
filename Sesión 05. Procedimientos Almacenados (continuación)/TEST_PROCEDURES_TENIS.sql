create or replace procedure reiniciaTodo is
begin
    /*Reinicializacion de la BD
    */
    execute immediate 'drop sequence seq_pistas';
    execute immediate 'create sequence seq_pistas';
    
    delete from reservas;
    delete from pistas;
    
    insert into pistas values (seq_pistas.nextval);
    insert into reservas values (seq_pistas.currval, '20/03/2018', 14, 'Pepito');
    
    insert into pistas values (seq_pistas.nextval);
    insert into reservas values (seq_pistas.currval, '24/03/2018', 18, 'Pepito');
    
    insert into reservas values (seq_pistas.currval, '21/03/2018', 14, 'Juan');
    insert into pistas values (seq_pistas.nextval);
    
    insert into reservas values (seq_pistas.currval, '22/03/2018', 13, 'Lola');
    insert into reservas values (seq_pistas.currval, '22/03/2018', 12, 'Pepito');
    commit;
end;
/

create or replace procedure TEST_PROCEDURES_TENIS is
 
begin
    reiniciaTodo;    
    
    begin
        dbms_output.put_line('Test1: Socio 1 reserva pista hoy a las 12------------------');
        pReservarPistas( 'Socio 1', CURRENT_DATE, 12 );
        rollback; --truco para detectar que se nos ha olvidado poner un commit
        
        declare --comprobamos se ha hecho la reserva
            cursor curr_reserva is
                select count(*)
                from reservas 
                where trunc(fecha) = trunc(CURRENT_DATE)
                AND hora = 12 AND socio = 'Socio 1';
              
             varCuantos integer;         
            
        begin 
            open curr_reserva;
            fetch curr_reserva into varCuantos;--recuerda que select count siempre devuelve 1a fila pase lo que pase (no necesitas found/notfound)
            close curr_reserva;
            commit; --no dejar abiertas transacciones nunca!!!
            if  varCuantos=0 then   
                 dbms_output.put_line('MAL: No da error pero no hace la reserva correctamente.');
            else 
                dbms_output.put_line('BIEN: Reserva correcta.');
            end if;    
        /*si omitimos el bloque de excepciones sería lo mismo que poner este otro:
        exception
            when others then raise;
        */    
        end;    
    exception
      when others then
        dbms_output.put_line('MAL: Lanza algun tipo de excepción.');
        dbms_output.put_line('Error nro: '||SQLCODE);
        dbms_output.put_line('Mensaje: '||SQLERRM);
    end;
    
    /*lo mismo con la siguiente reserva pero lo simplifcamos
    con un cursor implicito, menos codigo ... */
    begin
        dbms_output.put_line('Test2: Socio 2 reserva pista hoy a las 12------------------');
        pReservarPistas( 'Socio 2', CURRENT_DATE, 12 );
        rollback;
        
        --comprobamos se ha hecho la reserva
        declare
            varCuantos integer;         
            
        begin 
            select count(*) --recuerda que select count siempre devuelve 1 fila pase lo que pase (no necesitas excepcion no_data_found)
            into varCuantos
            from reservas 
             where trunc(fecha) = trunc(CURRENT_DATE)
             AND hora = 12 AND socio = 'Socio 2';
        
            commit; --no dejar abiertas transacciones nunca!!!
            if  varCuantos=0 then   
                 dbms_output.put_line('MAL: No da error pero no hace la reserva correctamente.');
            else 
                dbms_output.put_line('BIEN: Reserva correcta.');
            end if;            
        end;    
    exception
      when others then
        dbms_output.put_line('MAL: Lanza algun tipo de excepción.');
        dbms_output.put_line('Error nro: '||SQLCODE);
        dbms_output.put_line('Mensaje: '||SQLERRM);
    end;
    
    /*Lo mismo con la siguiente reserva, tal cual*/
    begin
        dbms_output.put_line('Test3: Socio 3 reserva pista hoy a las 12------------------');
        pReservarPistas( 'Socio 3', CURRENT_DATE, 12 );
        rollback;
        
        --comprobamos se ha hecho la reserva
        declare
            varCuantos integer;         
            
        begin 
            select count(*) 
            into varCuantos
            from reservas 
             where trunc(fecha) = trunc(CURRENT_DATE)
             AND hora = 12 AND socio = 'Socio 3';
        
            commit;-- no dejar abiertas transacciones nunca!!!
            if  varCuantos=0 then   
                 dbms_output.put_line('MAL: No da error pero no hace la reserva correctamente.');
            else 
                dbms_output.put_line('BIEN: Reserva correcta.');
            end if;            
        end;    
    exception
      when others then
        dbms_output.put_line('MAL: Lanza algun tipo de excepción.');
        dbms_output.put_line('Error nro: '||SQLCODE);
        dbms_output.put_line('Mensaje: '||SQLERRM);
    end;
    
    /*la reserva 4 es diferente, ahora tiene que dar el error de que no hay pistas   */
    begin
        dbms_output.put_line('Test4: No hay pistas disponibles------------------');
        dbms_output.put_line('Test4: Socio 4 reserva pista hoy a las 12------------------');
        pReservarPistas( 'Socio 4', CURRENT_DATE, 12 );
        rollback;    
        dbms_output.put_line('MAL: Reserva pista no disponible sin dar error.');
    exception
        when others then
            if SQLCODE = -20001 then
                dbms_output.put_line('BIEN: Detecta pistas no disponibles.');
                dbms_output.put_line('Error nro '||SQLCODE);
                dbms_output.put_line('Mensaje '||SQLERRM);
        else
            dbms_output.put_line('MAL: Da error pero no detecta pistas no disponibles.');
            dbms_output.put_line('Error nro '||SQLCODE);
            dbms_output.put_line('Mensaje '||SQLERRM);
      end if;
  end;
  
  begin
    dbms_output.put_line('Test5: Anular reserva Socio 1------------------');
    pAnularReservas('Socio 1',current_date, 12, 1);
    
    --comprobamos se ha anulado la reserva
    declare
        varCuantos integer;         
            
    begin 
        select count(*) 
        into varCuantos
        from reservas 
         where trunc(fecha) = trunc(CURRENT_DATE)
         AND hora = 12 AND socio = 'Socio 1';
    
        commit; --no dejar abiertas transacciones nunca!!!
        if  varCuantos=1 then   
             dbms_output.put_line('MAL: No da error pero no anula la reserva correctamente.');
        else 
            dbms_output.put_line('BIEN: Anulacion correcta.');
        end if;            
    end;    
  exception
    when others then
        dbms_output.put_line('MAL: Lanza algun tipo de excepción.');
        dbms_output.put_line('Error nro: '||SQLCODE);
        dbms_output.put_line('Mensaje: '||SQLERRM);
  end;
  
  --en este caso buscamos que de error de que no existe la reserva
  begin
    dbms_output.put_line('Test6: Anular reserva inexistente de 1920------------------');
    pAnularreservas( 'Socio 1', date '1920-1-1', 12, 1);    
    dbms_output.put_line('MAL: No da error al anular reserva inexistente.');
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
end;
/

exec TEST_PROCEDURES_TENIS;

select * from reservas;