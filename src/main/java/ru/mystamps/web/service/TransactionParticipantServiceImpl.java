/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import ru.mystamps.web.dao.TransactionParticipantDao;
import ru.mystamps.web.dao.dto.EntityWithIdDto;
import ru.mystamps.web.support.spring.security.HasAuthority;

@RequiredArgsConstructor
public class TransactionParticipantServiceImpl implements TransactionParticipantService {
	
	private final TransactionParticipantDao transactionParticipantDao;
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.ADD_SERIES_SALES)
	public List<EntityWithIdDto> findAllBuyers() {
		return transactionParticipantDao.findAllAsEntityWithIdDto();
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.ADD_SERIES_SALES)
	public List<EntityWithIdDto> findAllSellers() {
		return transactionParticipantDao.findAllAsEntityWithIdDto();
	}
	
}
