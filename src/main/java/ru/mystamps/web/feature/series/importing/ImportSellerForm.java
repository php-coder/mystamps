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
package ru.mystamps.web.feature.series.importing;

import lombok.Getter;
import lombok.Setter;
import ru.mystamps.web.feature.participant.AddParticipantDto;

@Getter
@Setter
public class ImportSellerForm implements AddParticipantDto {
	
	// @todo #695 /series/import/request/{id}(seller.name): trim empty values to null
	// @todo #695 /series/import/request/{id}(seller.name): add validation against short values
	// @todo #695 /series/import/request/{id}(seller.name): add validation against long values
	private String name;
	
	// @todo #695 /series/import/request/{id}(seller.url): trim empty values to null
	// @todo #695 /series/import/request/{id}(seller.url): add validation for valid url
	// @todo #695 /series/import/request/{id}(seller.url): add validation against long values
	private String url;
	
	// @todo #857 /series/import/request/{id}(seller.group): add validation against negative values
	// @todo #857 /series/import/request/{id}(seller.group): add validation for existing group
	private Integer groupId;
	
	@Override
	public Boolean getBuyer() {
		return Boolean.FALSE;
	}
	
	@Override
	public Boolean getSeller() {
		return Boolean.TRUE;
	}
	
}
