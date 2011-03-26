package ru.mystamps.db;

import org.springframework.stereotype.Repository;

import ru.mystamps.web.entity.SuspiciousActivityType;

@Repository
public class SuspiciousActivitiesTypes {
	
	public SuspiciousActivityType findByName(final String name) {
		// TODO
		return new SuspiciousActivityType(10L, "XXX");
	}
	
}
