create table contribution
(
    id uuid primary key
);

create table post
(
    id              uuid primary key,
    contribution_id uuid references contribution (id) not null,
    ordering        int                               not null,
    author          varchar                           not null,
    time            timestamp                         not null,
    contents        varchar                           not null
);
