CREATE TABLE people_group (
    id_ serial,
    name_ varchar(255),
    primary key (id_)
);

CREATE TABLE person (
    id_ serial,
    first_name_ varchar(255),
    last_name_ varchar(255),
    group_id_ integer not null,
    primary key (id_),
    foreign key (group_id_) references people_group(id_)
);

CREATE TABLE contact_info (
    id_ serial,
    owner_id_ integer not null,
    street_address_ varchar(255),
    phone_number_ varchar(20),
    primary key (id_),
    foreign key (owner_id_) references person(id_)
);
