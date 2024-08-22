CREATE TABLE fcm_token
(
    id              bigint primary key auto_increment,
    member_id       bigint          null,
    token           varchar(255)    not null,
    device_type     varchar(20)     not null,
    device_id       varchar(255)    not null,
    created_at      datetime        null,
    updated_at      datetime        null
);

create index fcm_token_idx_member_id on fcm_token (member_id);
create index fcm_token_idx_device_type on fcm_token (device_type);
create index fcm_token_idx_token on fcm_token (token);