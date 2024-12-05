create table comment
(
    id                bigint primary key auto_increment,
    member_id         bigint      not null,
    post_id           bigint      not null,
    contest_type      varchar(20) not null,
    comment_text      text        not null,
    parent_comment_id bigint      null,
    comment_status    varchar(20) not null,
    like_count        int default 0,
    created_at        datetime    null,
    updated_at        datetime    null
);

create index comment_idx_member_id_contest_type on comment (member_id, contest_type);
create unique index comment_uidx_contest_type_post_id on comment (post_id, contest_type);
