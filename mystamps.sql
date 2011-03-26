CREATE DATABASE mystamps;
USE mystamps;

----
-- users
----
CREATE TABLE `users` (
	`id` INT(5) UNSIGNED AUTO_INCREMENT NOT NULL,
	
	-- see LOGIN_MAX_LENGTH in ru.mystamps.web.validation.ValidationRules
	`login` VARCHAR(15) NOT NULL,
	
	-- see NAME_MAX_LENGTH in ru.mystamps.web.validation.ValidationRules
	`name` VARCHAR(100) NOT NULL,
	
	-- see email field at users_activation
	`email` VARCHAR(255) NOT NULL,
	
	`registered_at` DATETIME NOT NULL,
	`activated_at` DATETIME NOT NULL,
	
	-- -1 (banned), 0 (lost password), 1 (ok)
	`status` TINYINT(1) NOT NULL DEFAULT 1,
	
	`hash` CHAR(40) NOT NULL,
	
	-- see ru.mystamps.db.Users.generateSalt()
	`salt` CHAR(10) NOT NULL,
	
	PRIMARY KEY(`id`),
	UNIQUE KEY(`login`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

----
-- users_activation
----
CREATE TABLE `users_activation` (
	-- see EMAIL_MAX_LENGTH in ru.mystamps.web.validation.ValidationRules
	`email` VARCHAR(255) NOT NULL,
	
	`registered_at` DATETIME NOT NULL,
	
	-- see:
	-- ACT_KEY_LENGTH in WEB-INF/web.xml
	-- ru.mystamps.db.UsersActivation.generateActivationKey()
	`act_key` CHAR(10) NOT NULL,
	
	UNIQUE KEY(`act_key`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

----
-- suspicious_activities_types
----
CREATE TABLE `suspicious_activities_types` (
	`id` TINYINT(2) UNSIGNED AUTO_INCREMENT NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

INSERT INTO `suspicious_activities_types`(`name`)
VALUES ('PageNotFound'), ('AuthenticationFailed');

----
-- suspicious_activities
----
CREATE TABLE `suspicious_activities` (
	`type_id` TINYINT(2) UNSIGNED NOT NULL,
	`date` DATETIME NOT NULL,
	`page` VARCHAR(100) NOT NULL,
	`user_id` INT(5) UNSIGNED,
	`ip` CHAR(15) NOT NULL DEFAULT '',
	`referrer_page` VARCHAR(255) NOT NULL DEFAULT '',
	`user_agent` VARCHAR(255) NOT NULL DEFAULT '',
	FOREIGN KEY (`type_id`) REFERENCES `suspicious_activities_types`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

