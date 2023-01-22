/*
 * Copyright (C) 2009-2023 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.account;

import lombok.Getter;
import lombok.Setter;
import ru.mystamps.web.support.beanvalidation.Group;

import javax.validation.GroupSequence;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import static ru.mystamps.web.feature.account.AccountValidation.EMAIL_2ND_LEVEL_DOMAIN_REGEXP;
import static ru.mystamps.web.feature.account.AccountValidation.EMAIL_MAX_LENGTH;

@Getter
@Setter
@GroupSequence({
	RegisterAccountForm.class,
	Group.Level1.class,
	Group.Level2.class,
	Group.Level3.class
})
public class RegisterAccountForm implements RegisterAccountDto {
	
	@NotEmpty(groups = Group.Level1.class)
	@Size(max = EMAIL_MAX_LENGTH, message = "{value.too-long}", groups = Group.Level2.class)
	@Email(regexp = EMAIL_2ND_LEVEL_DOMAIN_REGEXP, groups = Group.Level3.class)
	private String email;
	
}
