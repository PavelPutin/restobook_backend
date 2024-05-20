CREATE TABLE `employees` (
  `id` int NOT NULL AUTO_INCREMENT,
  `login` varchar(512) NOT NULL,
  `surname` varchar(512) NOT NULL,
  `name` varchar(512) NOT NULL,
  `patronymic` varchar(512) DEFAULT NULL,
  `employee_comment` text,
  `change_Pass` tinyint(1) DEFAULT NULL,
  `restaurant_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `restaurnt_id_idx` (`restaurant_id`),
  CONSTRAINT `restaurant_id` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


-- Добавление сотрудников для первого ресторана
INSERT INTO employees (login, surname, name, patronymic, employee_comment, change_Pass, restaurnt_id)
VALUES ('john_doe', 'Doe', 'John', 'Smith', 'Test comment for John Doe', 0, 1),
       ('jane_smith', 'Smith', 'Jane', 'Doe', 'Test comment for Jane Smith', 1, 1),
       ('mike_jones', 'Jones', 'Mike', NULL, 'Test comment for Mike Jones', 0, 1),
       ('sarah_brown', 'Brown', 'Sarah', NULL, NULL, 1, 1),
       ('alex_white', 'White', 'Alex', NULL, NULL, 0, 1);

-- Добавление сотрудников для второго ресторана
INSERT INTO employees (login, surname, name, patronymic, employee_comment, change_Pass, restaurnt_id)
VALUES ('peter_smith', 'Smith', 'Peter', NULL, NULL, 0, 2),
       ('linda_green', 'Green', 'Linda', NULL, NULL, 1, 2),
       ('sam_wilson', 'Wilson', 'Samuel', NULL, NULL, 0, 2),
       ('emily_baker', 'Baker', 'Emily', NULL, NULL, 1, 2),
       ('david_clarkson','Clarkson','David','Michael','Test comment for David Clarkson.',0 ,2);