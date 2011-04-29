CREATE DATABASE mystamps;
USE mystamps;

----
-- users
-- (see ru.mystamps.web.entity.User)
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
	
	`hash` CHAR(40) NOT NULL,
	
	-- see ru.mystamps.web.service.UserService.generateSalt()
	`salt` CHAR(10) NOT NULL,
	
	PRIMARY KEY(`id`),
	UNIQUE KEY(`login`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

----
-- users_activation
-- (see ru.mystamps.web.entity.UsersActivation)
----
CREATE TABLE `users_activation` (
	-- see:
	-- ACT_KEY_LENGTH in WEB-INF/web.xml
	-- ru.mystamps.web.service.UserService.generateActivationKey()
	`act_key` CHAR(10) NOT NULL,
	
	-- see EMAIL_MAX_LENGTH in ru.mystamps.web.validation.ValidationRules
	`email` VARCHAR(255) NOT NULL,
	
	`created_at` DATETIME NOT NULL,
	
	PRIMARY KEY(`act_key`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

----
-- suspicious_activities_types
-- (see ru.mystamps.web.entity.SuspiciousActivityType)
----
CREATE TABLE `suspicious_activities_types` (
	`id` INT(5) UNSIGNED AUTO_INCREMENT NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	PRIMARY KEY(`id`),
	UNIQUE KEY(`name`)
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

-- see:
-- ru.mystamps.web.service.SiteService.getAbsentPageType()
-- ru.mystamps.web.service.SiteService.getFailedAuthenticationType()
INSERT INTO `suspicious_activities_types`(`name`)
VALUES ('PageNotFound'), ('AuthenticationFailed');

----
-- suspicious_activities
-- (see ru.mystamps.web.entity.SuspiciousActivity)
----
CREATE TABLE `suspicious_activities` (
	`id` INT(5) UNSIGNED AUTO_INCREMENT NOT NULL,
	`type_id` INT(5) UNSIGNED NOT NULL,
	`occured_at` DATETIME NOT NULL,
	`page` VARCHAR(100) NOT NULL,
	`user_id` INT(5) UNSIGNED,
	`ip` CHAR(15) NOT NULL DEFAULT '',
	`referer_page` VARCHAR(255) NOT NULL DEFAULT '',
	`user_agent` VARCHAR(255) NOT NULL DEFAULT '',
	PRIMARY KEY(`id`),
	FOREIGN KEY(`type_id`) REFERENCES `suspicious_activities_types`(`id`) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8;

