/*
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.beanvalidation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Workaround implementation for Hibernate's {@code @Email} annotation.
 *
 * To require top-level domain I compose {@code @Email} annotation with
 * {@code @Pattern} where latter just add more strict checking.
 *
 * @see <a href="http://stackoverflow.com/q/4459474">Question at StackOverflow</a>
 * @see <a href="https://hibernate.atlassian.net/browse/HVAL-43">Issue in Hibernate's JIRA</a>
 * @author Slava Semushin
 */
@org.hibernate.validator.constraints.Email
@Pattern(regexp = ".+@.+\\..+")
@ReportAsSingleViolation
@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface Email {
	String message() default "{ru.mystamps.web.support.beanvalidation.Email.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
