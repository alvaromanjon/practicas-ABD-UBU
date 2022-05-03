--Autores: Álvaro Manjón y María Alonso

drop table precio_combustible cascade constraints;
drop table modelos            cascade constraints;
drop table vehiculos 	      cascade constraints;
drop table clientes 	      cascade constraints;
drop table facturas	      cascade constraints;
drop table lineas_factura     cascade constraints;
drop table reservas			 cascade constraints;

drop sequence seq_modelos;
drop sequence seq_num_fact;
drop sequence seq_reservas;

create table clientes(
	NIF	varchar(9) primary key,
	nombre	varchar(20) not null,
	ape1	varchar(20) not null,
	ape2	varchar(20) not null,
	direccion varchar(40) 
);

create table precio_combustible(
	tipo_combustible	varchar(10) primary key,
	precio_por_litro	numeric(4,2) not null
);

create sequence seq_modelos;

create table modelos(
	id_modelo 		integer primary key,
	nombre			varchar(30) not null,
	precio_cada_dia 	numeric(6,2) not null check (precio_cada_dia>=0),
	capacidad_deposito	integer not null check (capacidad_deposito>0),
	tipo_combustible	varchar(10) not null references precio_combustible);


create table vehiculos(
	matricula	varchar(8)  primary key,
	id_modelo	integer  not null references modelos,
	color		varchar(10)
);

create sequence seq_reservas;
create table reservas(
	idReserva	integer primary key,
	cliente  	varchar(9) references clientes,
	matricula	varchar(8) references vehiculos,
	fecha_ini	date not null,
	fecha_fin	date,
	check (fecha_fin >= fecha_ini)
);

create sequence seq_num_fact;
create table facturas(
	nroFactura	integer primary key,
	importe		numeric( 8, 2),
	cliente		varchar(9) not null references clientes
);

create table lineas_factura(
	nroFactura	integer references facturas,
	concepto	char(40),
	importe		numeric( 7, 2),
	primary key ( nroFactura, concepto)
);
	

create or replace procedure alquilar(arg_NIF_cliente varchar,
  arg_matricula varchar, arg_fecha_ini date, arg_fecha_fin date) is
    
    fechas_comprobacion exception;
    pragma exception_init(fechas_comprobacion,-20001);
    vehiculo_inexistente exception;
    pragma exception_init(vehiculo_inexistente, -20002);
    vehiculo_no_disponible exception;
    pragma exception_init(vehiculo_no_disponible, -20003);
    cliente_inexistente exception;
    pragma exception_init(cliente_inexistente, -20004);
    fk_not_found exception;
    pragma exception_init(fk_not_found, -02291);
  
    type datosCoche is record (
        modelo_id modelos.id_modelo%type,
        precio_cada_dia modelos.precio_cada_dia%type,
        capacidad_deposito modelos.capacidad_deposito%type,
        tipo_combustible modelos.tipo_combustible%type,
        precio_por_litro precio_combustible.precio_por_litro%type
    );
    
    type datosReserva is record (
        idReserva reservas.idReserva%type,
        cliente reservas.cliente%type,
        fecha_ini reservas.fecha_ini%type,
        fecha_fin reservas.fecha_fin%type
    );
    
    infoCoche datosCoche;
    infoReserva datosReserva;
    n_dias integer;
    suma_importes integer;
    precio_alquiler_vehiculo integer;
    precio_coste_combustible integer;
  
    cursor vDatosVehiculo is
    select modelos.id_modelo, modelos.precio_cada_dia, modelos.capacidad_deposito, modelos.tipo_combustible, precio_combustible.precio_por_litro
    from modelos join vehiculos on (vehiculos.id_modelo = modelos.id_modelo) join precio_combustible on (modelos.tipo_combustible = precio_combustible.tipo_combustible)
    where vehiculos.matricula=arg_matricula
    for update of modelos.id_modelo;
    
    cursor vDisponibilidadVehiculo is
    select idReserva, cliente, fecha_ini, fecha_fin
    from reservas
    where (reservas.matricula=arg_matricula) and (fecha_ini between arg_fecha_ini and arg_fecha_fin) or (fecha_fin between arg_fecha_ini and arg_fecha_fin);
    
begin
    --Apartado 1. Comprobación de fechas
    if arg_fecha_ini > arg_fecha_fin then
        raise_application_error(-20001, 'El numero de dias sera mayor que cero.');
    end if;
    
    --Apartado 2. Comprobación de existencia de vehículo
    open vDatosVehiculo;
    fetch vDatosVehiculo into infoCoche;
    
    if vDatosVehiculo%rowcount=0 then
        raise_application_error(-20002, 'Vehiculo inexistente.');
        close vDatosVehiculo;
    end if;
    close vDatosVehiculo;
    
    --Apartado 3. Comprobación de disponibilidad del vehículo
    open vDisponibilidadVehiculo;
    fetch vDisponibilidadVehiculo into infoReserva;
    
    if vDisponibilidadVehiculo%rowcount!=0 then
        raise_application_error(-20003, 'El vehiculo no esta disponible.');
        close vDisponibilidadVehiculo;
    end if;
    close vDisponibilidadVehiculo;
    
    --Apartado 4. Cliente inexistente
    insert into reservas values (seq_reservas.nextval, arg_NIF_cliente, arg_matricula, arg_fecha_ini, arg_fecha_fin);
    /*
    - P4a. El resultado de la SELECT del paso anterior, ¿sigue siendo fiable en este paso?
    
    No va a ser fiable, ya que en este apartado acabamos de hacer un insert en la tabla
    de reservas, y en el paso anterior hemos hecho una SELECT de esa misma tabla, por
    lo que va a faltar la fila que acabamos de introducir, en caso de cumplir el criterio
    del where.
    
    - P4b. En este paso, la ejecución concurrente del mismo procedimiento ALQUILA 
    con, quizás otros o los mimos argumentos, ¿podría habernos añadido una reserva 
    no recogida en esa SELECT que fuese incompatible con nuestra reserva?, ¿por qué?
    
    Si son serializables, la última reserva que se añada detectaría que ya está 
    ocupado el vehículo y daría error. En caso de no serlo, se añadirían las dos
    reservas.
    
    - P4c. En este paso otra transacción concurrente cualquiera ¿podría hacer 
    INSERT o UPDATE sobre reservas y habernos añadido una reserva no recogida 
    en esa SELECT que fuese incompatible con nuestra reserva?, ¿por qué?
    
    No, porque la tabla está bloqueada debido al uso de cursores, por lo tanto,
    hasta que no hagamos commit, no se podrá hacer insert o update de forma
    concurrente.
    
    */
        
    --Apartado 5. Creación de factura y líneas de factura
    /*
    - P5. Piensa por qué en este paso ninguna transacción concurrente podría 
    habernos borrado el cliente o haber cambiado su NIF.
    
    No podría haberlo hecho ya que hemos bloqueado la escritura con los cursores,
    así que hasta que no se levante el bloqueo de escritura sólo se pueden leer
    sus datos.
    
    */
    if arg_fecha_fin is null then
        n_dias := 3;
    else
        n_dias := arg_fecha_fin - arg_fecha_ini;
    end if;
        
    precio_alquiler_vehiculo := infoCoche.precio_cada_dia * n_dias; 
    precio_coste_combustible := infoCoche.capacidad_deposito * infoCoche.precio_por_litro;
    suma_importes := precio_alquiler_vehiculo + precio_coste_combustible;
    
    insert into facturas values (seq_num_fact.nextval, suma_importes, arg_NIF_cliente);
    insert into lineas_factura values (seq_num_fact.currval,'Deposito lleno de ' || infoCoche.capacidad_deposito || ' litros de ' || infoCoche.tipo_combustible, precio_coste_combustible);
    insert into lineas_factura values (seq_num_fact.currval, n_dias || ' dias de alquiler, vehiculo modelo ' || infoCoche.modelo_id, precio_alquiler_vehiculo);
    
    commit;  
    
    exception
        when fk_not_found then
            rollback;
            raise_application_error(-20004, 'Cliente inexistente.');
            --Excepción del apartado 4
                
        when others then
            rollback;
            raise;
end;
/

create or replace
procedure reset_seq( p_seq_name varchar )
--From https://stackoverflow.com/questions/51470/how-do-i-reset-a-sequence-in-oracle
is
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

create or replace procedure inicializa_test is
begin
  reset_seq( 'seq_modelos' );
  reset_seq( 'seq_num_fact' );
  reset_seq( 'seq_reservas' );
        
  
    delete from lineas_factura;
    delete from facturas;
    delete from reservas;
    delete from vehiculos;
    delete from modelos;
    delete from precio_combustible;
    delete from clientes;
   
		
    insert into clientes values ('12345678A', 'Pepe', 'Perez', 'Porras', 'C/Perezoso n1');
    insert into clientes values ('11111111B', 'Beatriz', 'Barbosa', 'Bernardez', 'C/Barriocanal n1');
    
    insert into precio_combustible values ('Gasolina', 1.8);
    insert into precio_combustible values ('Gasoil',   1.7);
    
    insert into modelos values ( seq_modelos.nextval, 'Renault Clio Gasolina', 15, 50, 'Gasolina');
    insert into vehiculos values ( '1234-ABC', seq_modelos.currval, 'VERDE');

    insert into modelos values ( seq_modelos.nextval, 'Renault Clio Gasoil', 16,   50, 'Gasoil');
    insert into vehiculos values ( '1111-ABC', seq_modelos.currval, 'VERDE');
    insert into vehiculos values ( '2222-ABC', seq_modelos.currval, 'GRIS');
	
    commit;
end;
/
exec inicializa_test;

create or replace procedure test_alquila_coches is
begin
	 
  --caso 1 nro dias negativo
  begin
    inicializa_test;
    alquilar('12345678A', '1234-ABC', current_date, current_date-1);
    dbms_output.put_line('MAL: Caso nro dias negativo no levanta excepcion');
  exception
    when others then
      if sqlcode=-20001 then
        dbms_output.put_line('OK: Caso nro dias negativo correcto');
      else
        dbms_output.put_line('MAL: Caso nro dias negativo levanta excepcion '||sqlcode||' '||sqlerrm);
      end if;
  end;
  
  --caso 2 vehiculo inexistente
  begin
    inicializa_test;
    alquilar('87654321Z', '9999-ZZZ', date '2013-3-20', date '2013-3-22');
    dbms_output.put_line('MAL: Caso vehiculo inexistente no levanta excepcion');
  exception
    when others then
      if sqlcode=-20002 then
        dbms_output.put_line('OK: Caso vehiculo inexistente correcto');
      else
        dbms_output.put_line('MAL: Caso vehiculo inexistente levanta excepcion '||sqlcode||' '||sqlerrm);
      end if;
  end;
  
  --caso 3 cliente inexistente
  begin
    inicializa_test;
    alquilar('87654321Z', '1234-ABC', date '2013-3-20', date '2013-3-22');
    dbms_output.put_line('MAL: Caso cliente inexistente no levanta excepcion');
  exception
    when others then
      if sqlcode=-20004 then
        dbms_output.put_line('OK: Caso cliente inexistente correcto');
      else
        dbms_output.put_line('MAL: Caso cliente inexistente levanta excepcion '||sqlcode||' '||sqlerrm);
      end if;
  end;
  
  --caso 4 Todo correcto pero NO especifico la fecha final 
  declare
                
    resultadoPrevisto varchar(200) := 
      '1234-ABC11/03/1313512345678A3 dias de alquiler, vehiculo modelo 1   45#'||
      '1234-ABC11/03/1313512345678ADeposito lleno de 50 litros de Gasolina 90';
                
    resultadoReal varchar(200)  := '';
    fila varchar(200);
  begin  
    inicializa_test;
    alquilar('12345678A', '1234-ABC', date '2013-3-11', null);
    
    SELECT listAgg(matricula||fecha_ini||fecha_fin||facturas.importe||cliente
								||concepto||lineas_factura.importe, '#')
            within group (order by nroFactura, concepto)
    into resultadoReal
    FROM facturas join lineas_factura using(NroFactura)
                  join reservas using(cliente);
								
    dbms_output.put_line('Caso Todo correcto pero NO especifico la fecha final:');
   if resultadoReal=resultadoPrevisto then
      dbms_output.put_line('--OK SI Coinciden la reserva, la factura y las linea de factura');
    else
      dbms_output.put_line('--MAL NO Coinciden la reserva, la factura o las linea de factura');
      dbms_output.put_line('resultadoPrevisto='||resultadoPrevisto);
      dbms_output.put_line('resultadoReal    ='||resultadoReal);
    end if;
    
  exception   
    when others then
       dbms_output.put_line('--MAL: Caso Todo correcto pero NO especifico la fecha final devuelve '||sqlerrm);
  end;
  
  --caso 5 Intentar alquilar un coche ya alquilado
  
  --5.1 la fecha ini del alquiler esta dentro de una reserva
  begin
    inicializa_test;    
	--Reservo del 2013-3-10 al 12
	insert into reservas values
	 (seq_reservas.NEXTVAL, '11111111B', '1234-ABC', date '2013-3-11'-1, date '2013-3-11'+1);
    --Fecha ini de la reserva el 11 
	alquilar('12345678A', '1234-ABC', date '2013-3-11', date '2013-3-13');
	
    dbms_output.put_line('MAL: Caso vehiculo ocupado solape de fecha_ini no levanta excepcion');
	
  exception
    when others then
      if sqlcode=-20003 then
        dbms_output.put_line('OK: Caso vehiculo ocupado solape de fecha_ini correcto');
      else
        dbms_output.put_line('MAL: Caso vehiculo ocupado solape de fecha_ini levanta excepcion '||sqlcode||' '||sqlerrm);
      end if;
  end; 
  
   --5.2 la fecha fin del alquiler esta dentro de una reserva
  begin
    inicializa_test;    
	--Reservo del 2013-3-10 al 12
	insert into reservas values
	 (seq_reservas.NEXTVAL, '11111111B', '1234-ABC', date '2013-3-11'-1, date '2013-3-11'+1);
    --Fecha fin de la reserva el 11 
	alquilar('12345678A', '1234-ABC', date '2013-3-7', date '2013-3-11');
	
    dbms_output.put_line('MAL: Caso vehiculo ocupado solape de fecha_fin no levanta excepcion');
	
  exception
    when others then
      if sqlcode=-20003 then
        dbms_output.put_line('OK: Caso vehiculo ocupado solape de fecha_fin correcto');
      else
        dbms_output.put_line('MAL: Caso vehiculo ocupado solape de fecha_fin levanta excepcion '||sqlcode||' '||sqlerrm);
      end if;
  end; 
  
  --5.3 la el intervalo del alquiler esta dentro de una reserva
  begin
    inicializa_test;    
	--Reservo del 2013-3-9 al 13
	insert into reservas values
	 (seq_reservas.NEXTVAL, '11111111B', '1234-ABC', date '2013-3-11'-2, date '2013-3-11'+2);
    -- reserva del 4 al 19
	alquilar('12345678A', '1234-ABC', date '2013-3-11'-7, date '2013-3-12'+7);
	
    dbms_output.put_line('MAL: Caso vehiculo ocupado intervalo del alquiler esta dentro de una reserva no levanta excepcion');
	
  exception
    when others then
      if sqlcode=-20003 then
        dbms_output.put_line('OK: Caso vehiculo ocupado intervalo del alquiler esta dentro de una reserva correcto');
      else
        dbms_output.put_line('MAL: Caso vehiculo ocupado intervalo del alquiler esta dentro de una reserva levanta excepcion '
        ||sqlcode||' '||sqlerrm);
      end if;
  end; 
  
   --caso 6 Todo correcto pero SI especifico la fecha final 
  declare
                                      
    resultadoPrevisto varchar(400) := '12222-ABC11/03/1313/03/1311712345678A2 dias de alquiler, vehiculo modelo 2   32#'||
                                    '12222-ABC11/03/1313/03/1311712345678ADeposito lleno de 50 litros de Gasoil   85';
                                      
    resultadoReal varchar(400)  := '';    
    fila varchar(200);
  begin
    inicializa_test;
    alquilar('12345678A', '2222-ABC', date '2013-3-11', date '2013-3-13');
    
    SELECT listAgg(nroFactura||matricula||fecha_ini||fecha_fin||facturas.importe||cliente
								||concepto||lineas_factura.importe, '#')
            within group (order by nroFactura, concepto)
    into resultadoReal
    FROM facturas join lineas_factura using(NroFactura)
                  join reservas using(cliente);
    
    
    dbms_output.put_line('Caso Todo correcto pero SI especifico la fecha final');
    
    if resultadoReal=resultadoPrevisto then
      dbms_output.put_line('--OK SI Coinciden la reserva, la factura y las linea de factura');
    else
      dbms_output.put_line('--MAL NO Coinciden la reserva, la factura o las linea de factura');
      dbms_output.put_line('resultadoPrevisto='||resultadoPrevisto);
      dbms_output.put_line('resultadoReal    ='||resultadoReal);
    end if;
    
  exception   
    when others then
       dbms_output.put_line('--MAL: Caso Todo correcto pero SI especifico la fecha final devuelve '||sqlerrm);
  end;
 
end;
/

set serveroutput on
exec test_alquila_coches;
