drop table modelos cascade constraints;
drop table trenes cascade constraints;
drop table recorridos cascade constraints;
drop table viajes cascade constraints;
drop table tickets cascade constraints;
drop table revisionesModelo cascade constraints;
drop table revisionesTren cascade constraints;
drop table conductores cascade constraints;

create table modelos(
 idModelo integer primary key,
 nplazas integer
);

create table revisionesModelo(
 idRevisionM	integer primary key,
 km		        integer not null,
 descripcion  varchar(20),
 idModelo	    integer not null references modelos
 );

create table conductores(
 idConductor integer primary key,
 nombre      	char(20));

create table trenes(
  idTren   integer primary key,
  modelo      integer references modelos,
  kms         integer not null,
  idProximaRevision  	integer references revisionesModelo
);

create table recorridos(
   idRecorrido      integer primary key,
   estacionOrigen   varchar(15) not null,
   estacionDestino  varchar(15) not null,
   kms              numeric(6,2) not null,
   horaSalida       timestamp,          --Oracle no tiene time 
   horaLlegada      timestamp not null, --Oracle no tiene time   
   precio           numeric(5,2) not null,
   unique ( estacionOrigen, estacionDestino, horaSalida )   
);

create table viajes(
 idViaje     	integer primary key,
 idTren   	integer references trenes  not null,
 idRecorrido 	integer references recorridos not null,
 fecha 		    date not null,
 nPlazasLibres	integer not null,
 --realizado      boolean default false not null,
 realizado      smallint default 0 not null check(realizado in (0,1)),
 idConductor    integer not null references conductores,
 unique (idRecorrido, fecha) 
);

create table tickets(
 idTicket 	integer primary key,
 idViaje  	integer references viajes not null,
 fechaCompra    date not null,
 cantidad       integer not null,
 precio		numeric(5,2) not null
);
drop sequence seq_tickets;
create sequence seq_tickets;

create table revisionesTren(
 idRevisionA    integer  primary key,
 idRevisionM    integer not null references revisionesModelo,
 idTren	    integer not null references trenes,
 fecha		      date    not null,
 kms		        integer not null,
 UNIQUE ( idRevisionM, idTren)
); 

------------------------------------------------
--                                            nPlazas	idModelo 
insert into modelos (idModelo, nPlazas) values ( 1,     40 );  
insert into modelos (idModelo, nPlazas) values ( 2,     15 );  
insert into modelos (idModelo, nPlazas) values ( 3,     35 );  

insert into revisionesModelo ( idRevisionM, km,    descripcion,     idModelo ) 
		     values                 ( 1,        5000,  'Frenos',  	           1);
insert into revisionesModelo ( idRevisionM, km,    descripcion,     idModelo ) 
		     values                 ( 2,        10000, 'Frenos y filtros',     1);	
insert into revisionesModelo ( idRevisionM, km,    descripcion,     idModelo )                 
		     values                 ( 3,        5000,  'Frenos y filtros',     2);	
insert into revisionesModelo ( idRevisionM, km,    descripcion,     idModelo )                 
		     values                 ( 4,        5000,  'Frenos y filtros',     3);	

insert into trenes( idTren, modelo, 	kms,	idProximaRevision) values
                          (	1,          1,	  1000, 		1); 		
insert into trenes ( idTren, modelo, 	kms,	idProximaRevision) values
                          (	2,          1,	7500,		    2);
insert into trenes ( idTren, modelo, 	kms,	idProximaRevision) values          
                          (	3,          2,	2000,		    2);
insert into trenes( idTren, modelo, 	kms,	idProximaRevision) values          
                          ( 4,          2,  1000,       2);
insert into trenes ( idTren, modelo, 	kms,	idProximaRevision) values          
                          ( 5,          3,  1000,       4);     

insert into revisionesTren ( idRevisionA, idRevisionM, idTren, fecha,	            kms ) 
                        values ( 1,             1,		      2,      CURRENT_DATE-365, 	5500);   

                                                   --
insert into conductores( idConductor, nombre ) values (1, 'Pepe');
insert into conductores( idConductor, nombre ) values (2, 'Juan');
insert into conductores( idConductor, nombre ) values (3, 'Ana');

insert into recorridos 
(idRecorrido, estacionOrigen,	estacionDestino,kms, 	horaSalida, 	horaLlegada, precio) 
values 
( 1, 'Burgos',	   'Madrid',	      201,	'1/1/1 8:30',	'1/1/1 10:30',	10 );
insert into recorridos 
(idRecorrido, estacionOrigen,	estacionDestino,kms, 	horaSalida, 	horaLlegada, precio) 
values 
( 2, 'Burgos',	    'Madrid',	      200,	'1/1/1 16:30', '1/1/1 18:30',	12 );

insert into recorridos 
(idRecorrido, estacionOrigen,	estacionDestino,kms, 	horaSalida, 	horaLlegada, precio) 
values 
( 3,  'Madrid',	  'Burgos',	        200,	'1/1/1 13:30',	'1/1/1 15:30',	10 );

insert into recorridos 
(idRecorrido, estacionOrigen,	estacionDestino,kms, 	horaSalida, 	horaLlegada, precio) 
values 
( 4, 'Leon',       'Zamora',       150,    '1/1/1 8:00',  '1/1/1 9:30',    6  );  

insert into viajes
(idViaje, idTren,	idRecorrido,	fecha,		nPlazasLibres, realizado, idConductor)
values
(	1,        1,		      1,	    DATE '2022-4-20', 30,           1,        1);

insert into viajes
(idViaje, idTren,	idRecorrido,	fecha,		nPlazasLibres, realizado, idConductor)
values
(	2,        1,		      1,	    CURRENT_DATE+1,   38,           0,        2);

insert into viajes
(idViaje, idTren,	idRecorrido,	fecha,		nPlazasLibres, realizado, idConductor)
values
( 3,      1,              1,    CURRENT_DATE+7,   10,         0,        3);

insert into viajes
(idViaje, idTren,	idRecorrido,	fecha,		nPlazasLibres, realizado, idConductor)
values
( 4,      2,              4,	  CURRENT_DATE+7,   40,         0,       1);
	        
		
insert into tickets (	idTicket, idViaje, fechaCompra,	cantidad, precio)  
                  values(	seq_tickets.nextval,        1,	CURRENT_DATE,	  1,	      10);
insert into tickets (	idTicket, idViaje, fechaCompra,	cantidad, precio)                    
                  values( seq_tickets.nextval,        2,  CURRENT_DATE-1, 2,        10);	    
                  
commit;
exit;

		       
