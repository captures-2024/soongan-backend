create table weekly_contest_final
(
    id                  bigint primary key auto_increment,
    weekly_contest_id   bigint        not null,
    weekly_contest_post bigint        not null,
    ranking             int           null,
    score               decimal(4, 2) null,
    created_at          datetime      null,
    updated_at          datetime      null
);

create index weekly_contest_final_idx_weekly_contest_id_ranking on weekly_contest_final (weekly_contest_id, ranking);
