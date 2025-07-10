CREATE TABLE explain
(
    id          bigint primary key auto_increment,
    target_id   bigint      not null,
    target_type varchar(20) not null,
    explain     text        not null,
    created_at  datetime null,
    updated_at  datetime null
);
