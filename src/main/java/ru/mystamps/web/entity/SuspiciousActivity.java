package ru.mystamps.web.entity;

import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspiciousActivity {
	@Getter private final Long typeId;
	@Getter private final Date date;
	@Getter private final String page;
	@Getter private final Long userId;
	@Getter private final String ip;
	@Getter private final String refererPage;
	@Getter private final String userAgent;
}
