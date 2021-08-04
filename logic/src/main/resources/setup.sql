DROP TABLE IF EXISTS `gift_certificate`;
CREATE TABLE IF NOT EXISTS `gift_certificate` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `duration` INT NOT NULL,
    `create_date` DATE NOT NULL,
    `last_update_date` DATE NOT NULL,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `tag`;
CREATE TABLE IF NOT EXISTS `tag` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL UNIQUE,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `gift_certificate_has_tag`;
CREATE TABLE IF NOT EXISTS `gift_certificate_has_tag` (
`gift_certificate_id` INT NOT NULL,
`tag_id` INT NOT NULL,
PRIMARY KEY (`gift_certificate_id`, `tag_id`));

DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
    id int not null auto_increment,
    `name` varchar(250) null,
    PRIMARY KEY(id)
    );

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS orders (
    id int not null auto_increment,
    user_id int null,
    `order_time` timestamp null,
    price    decimal(10, 2) null,
    certificate_id int null,
    PRIMARY KEY(id),
    constraint user_id foreign key (user_id) references `user` (id),
    constraint certificate_id foreign key (certificate_id) references `gift_certificate`(id)
);

// INITIALIZATION

SET REFERENTIAL_INTEGRITY = FALSE;

INSERT INTO `gift_certificate` (id, name, description, price, duration, create_date, last_update_date)
VALUES (1, 'gift 1', 'description 1', 10.99, 10, NOW(), NOW()),
       (2, 'gift 2', 'description 2', 19.99, 20, NOW(), NOW()),
       (3, 'gift 3', 'description 3', 29.99, 30, NOW(), NOW()),
       (4, 'gift 4', 'description 4', 39.02, 1, NOW(), NOW()),
       (5, 'gift 5', 'description 4', 39.02, 1, NOW(), NOW());


INSERT INTO `tag`
VALUES (1, 'tag1'),
       (2, 'tag2'),
       (3, 'tag3'),
       (4, 'tag4'),
       (5, 'tag5'),
       (6, 'tag6'),
       (7, 'tag7'),
       (8, 'tag8');

INSERT INTO `gift_certificate_has_tag` (gift_certificate_id, tag_id)
VALUES (1, 1),
       (1, 4),
       (1, 6),
       (2, 3),
       (3, 2),
       (3, 7),
       (3, 8),
       (4, 1),
       (4, 5),
       (5, 1);

INSERT INTO `user` (`name`)
VALUES ('user1'),
       ('user2'),
       ('user3'),
       ('user4'),
       ('user5');

INSERT INTO `orders` (user_id, `order_time`, price, certificate_id)
VALUES ( 1, NOW(), 124.12, 2),
       (1, NOW(), 10.99, 3),
       (2, NOW(), 214.24, 2),
       (3, NOW(), 421.25, 1),
       (3, NOW(), 5.99, 3),
       (3, NOW(), 1.99, 5),
       (4, NOW(), 999.99, 4),
       (5, NOW(), 78.88, 5);


