create table if not exists reserva.tb_ambiente
(
    id              bigint auto_increment
    primary key,
    aprovacao       enum ('AUTOMATICA', 'MANUAL')                     not null,
    descricao       varchar(500)                                      null,
    disponibilidade enum ('DISPONIVEL', 'INDISPONIVEL', 'MANUTENCAO') not null,
    nome            varchar(50)                                       not null,
    constraint UKfskqvaoav7xvmonvwpx59x8h8
    unique (nome)
    );

