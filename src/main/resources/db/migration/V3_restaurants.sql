CREATE TABLE `restaurants` (
  `id` int NOT NULL,
  `rest_name` varchar(255) NOT NULL,
  `legal_entity_name` varchar(255) NOT NULL,
  `inn` varchar(12) NOT NULL,
  `rest_comment` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `legal_entity_name_UNIQUE` (`legal_entity_name`),
  UNIQUE KEY `inn_UNIQUE` (`inn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


INSERT INTO restobook_backend.restaurant (id, rest_name, legal_entity_name, inn, rest_comment)
VALUES (1, 'Restaurant 1', 'Legal Entity 1', '1234567890', 'Comment for Restaurant 1');

INSERT INTO restaurant (id, rest_name, legal_entity_name, inn, rest_comment)
VALUES (2, 'Restaurant 2', 'Legal Entity 2', '0987654321', 'Comment for Restaurant 2');