/*
 * Copyright (C) 2009-2022 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.support.liquibase;

import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.integration.spring.SpringResourceAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Provides ability to run Spring Boot application to only validate Liquibase migrations.
 */
public final class LiquibaseSupport {
	
	private LiquibaseSupport() {
	}
	
	public static SpringApplication createSpringApplication() {
		// Don't run Liquibase by default, we only need to initialize all required beans
		// Note that we can't set "spring.liquibase.enabled: false" because it disables
		// autoconfiguration of Liquibase beans completely.
		// See https://docs.liquibase.com/commands/config-ref/should-run-parameter.html
		System.setProperty("liquibase.shouldRun", "false");
		
		// Explicitly disable JMX. It might be enabled when we run via maven
		System.setProperty("spring.jmx.enabled", "false");
		
		// Override value (WARN) from application*.properties
		System.setProperty("logging.level.liquibase", "INFO");
		
		// LATER: Ideally, we don't need to use a connection pool (HikariCP) in this case.
		// Consider configuring spring.datasource.type property.
		SpringApplication app = new SpringApplication(LiquibaseOnlyStartup.class);
		
		// Act as a console application instead of as a web application.
		// See https://www.baeldung.com/spring-boot-no-web-server
		app.setDefaultProperties(
			Collections.singletonMap("spring.main.web-application-type", "none")
		);
		
		return app;
	}
	
	public static void validate(ApplicationContext context) throws LiquibaseException {
		SpringLiquibase springLiquibase = context.getBean(SpringLiquibase.class);
		performLiquibaseValidate(springLiquibase);
	}
	
	@Import({
		DataSourceAutoConfiguration.class,
		LiquibaseAutoConfiguration.class
	})
	public static class LiquibaseOnlyStartup {
	}
	
	// CheckStyle: ignore LineLength for next 2 lines
	// Partially copy&pasted from:
	// https://github.com/liquibase/liquibase/blob/v4.7.1/liquibase-core/src/main/java/liquibase/integration/spring/SpringLiquibase.java#L263-L276
	// Reason: the original code executes "update" while we need to perform validation
	private static void performLiquibaseValidate(SpringLiquibase springLiquibase)
		throws LiquibaseException {
		// CheckStyle: ignore LineLength for next 1 line
		try (Liquibase liquibase = createLiquibase(springLiquibase.getDataSource().getConnection(), springLiquibase)) {
			validate(liquibase, springLiquibase);
		} catch (SQLException ex) {
			throw new DatabaseException(ex);
		}
	}
	
	// CheckStyle: ignore LineLength for next 2 lines
	// Partially copy&pasted from:
	// https://github.com/liquibase/liquibase/blob/v4.7.1/liquibase-core/src/main/java/liquibase/Liquibase.java#L2279-L2283
	// Reason: the original method doesn't respect spring.liquibase.contexts
	// NOTE: spring.liquibase.labels aren't supported as we don't use them
	private static void validate(Liquibase liquibase, SpringLiquibase springLiquibase)
		throws LiquibaseException {
		DatabaseChangeLog changeLog = liquibase.getDatabaseChangeLog();
		changeLog.validate(liquibase.getDatabase(), springLiquibase.getContexts());
	}
	
	// CheckStyle: ignore LineLength for next 2 lines
	// Partially copy&pasted from:
	// https://github.com/liquibase/liquibase/blob/v4.7.1/liquibase-core/src/main/java/liquibase/integration/spring/SpringLiquibase.java#L320-L334
	// Reason: the original method is protected
	// NOTE: spring.liquibase.parameters.* aren't supported as we don't have access to it
	// (SpringLiquibase doesn't have a getter)
	private static Liquibase createLiquibase(Connection conn, SpringLiquibase springLiquibase)
		throws DatabaseException {
		return new Liquibase(
			springLiquibase.getChangeLog(),
			new SpringResourceAccessor(springLiquibase.getResourceLoader()),
			createDatabase(conn)
		);
	}
	
	// CheckStyle: ignore LineLength for next 2 lines
	// Partially copy&pasted from:
	// https://github.com/liquibase/liquibase/blob/v4.7.1/liquibase-core/src/main/java/liquibase/integration/spring/SpringLiquibase.java#L344-L380
	// Reason: the original method is protected
	// NOTE: the following parameter aren't supported (as we don't use them):
	// - spring.liquibase.default-schema
	// - spring.liquibase.liquibase-schema
	// - spring.liquibase.liquibase-tablespace
	// - spring.liquibase.database-change-log-table
	// - spring.liquibase.database-change-log-lock-table
	private static Database createDatabase(Connection conn) throws DatabaseException {
		return DatabaseFactory.getInstance()
			.findCorrectDatabaseImplementation(new JdbcConnection(conn));
	}
	
}
