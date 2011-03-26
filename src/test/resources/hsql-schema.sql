----
-- See also: mystamps.sql
----

----
-- users
----
CREATE TABLE "users" (
	"id" INT IDENTITY NOT NULL,
	"login" VARCHAR(15) NOT NULL,
	"name" VARCHAR(100) NOT NULL,
	"email" VARCHAR(255) NOT NULL,
	"registered_at" DATETIME NOT NULL,
	"activated_at" DATETIME NOT NULL,
	"status" TINYINT NOT NULL,
	"hash" CHAR(40) NOT NULL,
	"salt" CHAR(10) NOT NULL,
	UNIQUE ("login")
);

ALTER TABLE "users" ALTER COLUMN "status" SET DEFAULT 1;

----
-- users_activation
----
CREATE TABLE "users_activation" (
	"email" VARCHAR(255) NOT NULL,
	"registered_at" DATETIME NOT NULL,
	"act_key" CHAR(10) NOT NULL,
	UNIQUE ("act_key")
);

----
-- suspicious_activities_types
----
CREATE TABLE "suspicious_activities_types" (
	"id" TINYINT IDENTITY NOT NULL,
	"name" VARCHAR(100) NOT NULL
);

INSERT INTO "suspicious_activities_types"("name")
VALUES ('PageNotFound'), ('AuthenticationFailed');

----
-- suspicious_activities
----
CREATE TABLE "suspicious_activities" (
	"type_id" TINYINT NOT NULL,
	"date" DATETIME NOT NULL,
	"page" VARCHAR(100) NOT NULL,
	"user_id" INT,
	"ip" CHAR(15) NOT NULL,
	"referrer_page" VARCHAR(255) NOT NULL,
	"user_agent" VARCHAR(255) NOT NULL,
	FOREIGN KEY ("type_id") REFERENCES "suspicious_activities_types"("id") ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY ("user_id") REFERENCES "users"("id") ON UPDATE CASCADE ON DELETE CASCADE
);

ALTER TABLE "suspicious_activities" ALTER COLUMN "ip" SET DEFAULT '';
ALTER TABLE "suspicious_activities" ALTER COLUMN "referrer_page" SET DEFAULT '';
ALTER TABLE "suspicious_activities" ALTER COLUMN "user_agent" SET DEFAULT '';

