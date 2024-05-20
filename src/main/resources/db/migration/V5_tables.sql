CREATE TABLE `tables` (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_number` int NOT NULL,
  `seats_number` int NOT NULL,
  `state` varchar(512) NOT NULL,
  `table_comment` text,
  `restaurnt_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `restaurnt_id_idx` (`restaurnt_id`),
  CONSTRAINT `restaurnt_id` FOREIGN KEY (`restaurnt_id`) REFERENCES `restaurants` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


INSERT INTO tables (id, table_number, seats_number, state, table_comment, restaurnt_id)
VALUES (1, 4, 0, 'state 1', 'comment for table 1', 1),
       (2, 6, 1, 'state 2', 'comment for table 2', 1),
       (3, 2, 0, 'state 3', 'comment for table 3', 1),
       (4,8, 0, 'state 4', 'comment for table 4', 1),
       (5, 4, 1, 'state 5', 'comment for table 5', 1);


INSERT INTO tables (id, table_number, seats_number, state, table_comment, restaurnt_id)
VALUES (6, 4, 0, 'state 1', 'comment for table 1', 2),
       (7, 6, 1, 'state 2', 'comment for table 2', 2),
       (8, 2, 0, 'state 3', 'comment for table 3', 2),
       (9,8, 0, 'state 4', 'comment for table 4', 2),
       (10, 4, 1, 'state 5', 'comment for table 5', 2);