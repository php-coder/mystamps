/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.feature.participant;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static ru.mystamps.web.feature.participant.ParticipantValidation.NAME_MAX_LENGTH;
import static ru.mystamps.web.feature.participant.ParticipantValidation.NAME_MIN_LENGTH;
import static ru.mystamps.web.feature.participant.ParticipantValidation.URL_MAX_LENGTH;

@Getter
@Setter
public class AddParticipantForm implements AddParticipantDto {
	
	@NotEmpty
	@Size(min = NAME_MIN_LENGTH, message = "{value.too-short}")
	@Size(max = NAME_MAX_LENGTH, message = "{value.too-long}")
	private String name;
	
	@URL
	@Size(max = URL_MAX_LENGTH, message = "{value.too-long}")
	private String url;
	
	// FIXME: must be positive
	// FIXME: must be existing group
	private Integer groupId;
	
	@NotNull
	private Boolean buyer;
	
	@NotNull
	private Boolean seller;
}
