create sequence hibernate_sequence start with 1 increment by 1
create table exam (id bigint not null, time_for_completion_in_minutes integer not null, exam_name varchar(255), start_date_time varchar(255), end_date_time varchar(255), teacher varchar(255), theme varchar(255), primary key (id))
create table question (id bigint not null, answers varchar(255), correct_answer varchar(255), text varchar(255), theme varchar(255), primary key (id))
create table role (id integer not null, role varchar(255), primary key (id))
create table student_group (id bigint not null, group_name varchar(255), primary key (id))
create table student_session (id bigint not null, code varchar(255), email varchar(255), end_time varchar(255), exam bigint, group_name varchar(255), is_active boolean not null, name varchar(255), result integer not null, result_string clob, time varchar(255), primary key (id))
create table system_users (id bigint not null, active boolean not null, email varchar(255), password varchar(255), username varchar(255), primary key (id))
create table theme (id integer not null, name varchar(255), primary key (id))
create table user_role (user_id bigint not null, role_id integer not null, primary key (user_id, role_id))
alter table user_role add constraint FKa68196081fvovjhkek5m97n3y foreign key (role_id) references role
alter table user_role add constraint FKiuj2y46q7k7ajdghenr25a8jy foreign key (user_id) references system_users



