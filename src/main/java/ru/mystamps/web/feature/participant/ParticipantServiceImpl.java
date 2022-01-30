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
package ru.mystamps.web.feature.participant;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ru.mystamps.web.common.EntityWithParentDto;
import ru.mystamps.web.support.spring.security.HasAuthority;

import java.util.List;

@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {
	
	private final Logger log;
	private final ParticipantDao participantDao;
	
	@Override
	@Transactional
	@PreAuthorize(HasAuthority.ADD_PARTICIPANT)
	public Integer add(AddParticipantDto dto) {
		Validate.isTrue(dto != null, "DTO must be non null");
		Validate.isTrue(dto.getName() != null, "Name must be non null");
		Validate.isTrue(dto.getBuyer() != null, "Buyer flag must be non null");
		Validate.isTrue(dto.getSeller() != null, "Seller flag must be non null");
		
		AddParticipantDbDto participant = new AddParticipantDbDto();
		participant.setName(dto.getName());
		participant.setUrl(dto.getUrl());
		participant.setGroupId(dto.getGroupId());
		participant.setBuyer(dto.getBuyer());
		participant.setSeller(dto.getSeller());
		
		Integer id = participantDao.add(participant);
		
		log.info("Participant #{} has been created ({})", id, participant);
		
		return id;
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.ADD_SERIES_SALES)
	public List<EntityWithParentDto> findBuyersWithParents() {
		return participantDao.findBuyersWithParents();
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.ADD_SERIES_SALES)
	public List<EntityWithParentDto> findSellersWithParents() {
		return participantDao.findSellersWithParents();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer findSellerId(String name) {
		Validate.isTrue(StringUtils.isNotBlank(name), "Seller name must be non-blank");
		
		return participantDao.findSellerId(name);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer findSellerId(String name, String url) {
		Validate.isTrue(StringUtils.isNotBlank(name), "Seller name must be non-blank");
		Validate.isTrue(StringUtils.isNotBlank(url), "Seller url must be non-blank");
		
		return participantDao.findSellerId(name, url);
	}
	
	@Override
	@Transactional(readOnly = true)
	@PreAuthorize(HasAuthority.ADD_PARTICIPANT)
	public List<EntityWithIdDto> findAllGroups() {
		return participantDao.findAllGroups();
	}
	
	// @todo #857 TransactionParticipantServiceImpl.findGroupIdByName(): move to a separate service
	@Override
	@Transactional(readOnly = true)
	public Integer findGroupIdByName(String name) {
		Validate.isTrue(StringUtils.isNotBlank(name), "Group name must be non-blank");
		
		return participantDao.findGroupIdByName(name);
	}
	
}
