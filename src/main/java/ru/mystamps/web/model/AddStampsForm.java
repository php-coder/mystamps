package ru.mystamps.web.model;

import lombok.Getter;
import lombok.Setter;

public class AddStampsForm {
	@Getter @Setter private String country;
	@Getter @Setter private Integer issueDay;
	@Getter @Setter private Integer issueMonth;
	@Getter @Setter private Integer issueYear;
	@Getter @Setter private String count;
	@Getter @Setter private boolean withoutPerforation;
	@Getter @Setter private String michelNo;
	@Getter @Setter private String scottNo;
	@Getter @Setter private String yvertNo;
	@Getter @Setter private String gibbonsNo;
	@Getter @Setter private String comment;
	@Getter @Setter private String image;
}
