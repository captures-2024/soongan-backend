create table comment_like
(
    id           bigint primary key auto_increment,
    member_id    bigint      not null,
    comment_id   bigint      not null,
    contest_type varchar(20) not null,
    created_at   datetime    null,
    updated_at   datetime    null
);

create index comment_like_idx_member_id on comment_like (member_id);
create unique index comment_like_uidx_contest_type_comment_id on comment_like (contest_type, comment_id);
