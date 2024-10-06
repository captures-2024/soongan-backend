create table post_like
(
    id           bigint primary key auto_increment,
    member_id    bigint      not null,
    post_id      bigint      not null,
    contest_type varchar(20) not null,
    created_at   datetime    null,
    updated_at   datetime    null
);

create index post_like_idx_member_id on post_like (member_id);
create unique index post_like_uidx_contest_type_post_id on post_like (contest_type, post_id);
