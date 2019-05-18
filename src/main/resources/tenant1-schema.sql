create table tenant (
  id integer primary key,
  cep varchar(100) not null,
  cidade varchar(500),
  pdv double
);
