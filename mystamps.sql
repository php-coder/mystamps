CREATE DATABASE mystamps;
USE mystamps;

----
-- users
----
CREATE TABLE `users` (
	`id` INT(5) UNSIGNED AUTO_INCREMENT NOT NULL,
	`login` VARCHAR(20) NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	`email` VARCHAR(255) NOT NULL,
	`registered_at` DATETIME NOT NULL,
	`status` TINYINT(1), -- -1 (banned), 0 (just registered), 1 (ok), 3 (lost password)
	`hash` CHAR(40) NOT NULL,
	`salt` CHAR(10) NOT NULL,
	PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

----
-- suspicious_activities_types
----
CREATE TABLE `suspicious_activities_types` (
	`id` TINYINT(2) UNSIGNED AUTO_INCREMENT NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

INSERT INTO suspicious_activities_types(name) VALUES("PageNotFound");

----
-- suspicious_activities
----
CREATE TABLE `suspicious_activities` (
	`type_id` TINYINT(2) UNSIGNED NOT NULL,
	`date` DATETIME NOT NULL,
	`page` VARCHAR(100) NOT NULL,
	`user_id` INT(5) UNSIGNED,
	`ip` CHAR(15) NOT NULL NOT NULL DEFAULT '',
	`referrer_page` VARCHAR(255) NOT NULL DEFAULT '',
	`user_agent` VARCHAR(255) NOT NULL DEFAULT '',
	FOREIGN KEY (`type_id`) REFERENCES `suspicious_activities_types`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

