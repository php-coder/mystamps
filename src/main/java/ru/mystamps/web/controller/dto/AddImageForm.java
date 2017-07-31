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
package ru.mystamps.web.controller.dto;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

import ru.mystamps.web.service.dto.AddImageDto;
import ru.mystamps.web.support.beanvalidation.ImageFile;
import ru.mystamps.web.support.beanvalidation.MaxFileSize;
import ru.mystamps.web.support.beanvalidation.MaxFileSize.Unit;
import ru.mystamps.web.support.beanvalidation.NotEmptyFile;
import ru.mystamps.web.support.beanvalidation.NotEmptyFilename;

import static ru.mystamps.web.validation.ValidationRules.MAX_IMAGE_SIZE;

@Getter
@Setter
@GroupSequence({
	AddImageForm.class,
	Group.Level1.class,
	Group.Level2.class,
	Group.Level3.class,
	Group.Level4.class,
	Group.Level5.class
})
public class AddImageForm implements AddImageDto {
	
	@NotNull(groups = Group.Level1.class)
	@NotEmptyFilename(groups = Group.Level2.class)
	@NotEmptyFile(groups = Group.Level3.class)
	@MaxFileSize(value = MAX_IMAGE_SIZE, unit = Unit.Kbytes, groups = Group.Level4.class)
	@ImageFile(groups = Group.Level5.class)
	private MultipartFile image;
	
}
