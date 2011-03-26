package ru.mystamps.web.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspiciousActivityType {
	@Getter private final Long id;
	@Getter private final String name;
}
