create table member
(
    id                bigint primary key auto_increment,
    email             varchar(255) not null,
    nickname          varchar(40)  null,
    birth_year        int          null,
    self_introduction text         null,
    profile_image_url varchar(255) null,
    provider          varchar(20)  not null,
    provider_id       varchar(255) not null,
    withdrawal_at     datetime     null,
    ban_until         datetime     null,
    created_at        datetime     null,
    updated_at        datetime     null
);

create index idx_member_email on member (email);
create unique index idx_member_provider_provider_id on member (provider, provider_id);
