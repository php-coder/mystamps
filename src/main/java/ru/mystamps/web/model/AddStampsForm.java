package ru.mystamps.web.model;

import lombok.Getter;
import lombok.Setter;

public class AddStampsForm {
	@Getter @Setter String country;
	@Getter @Setter Integer issueDay;
	@Getter @Setter Integer issueMonth;
	@Getter @Setter Integer issueYear;
	@Getter @Setter String count;
	@Getter @Setter boolean withoutPerforation;
	@Getter @Setter String michelNo;
	@Getter @Setter String scottNo;
	@Getter @Setter String yvertNo;
	@Getter @Setter String gibbonsNo;
	@Getter @Setter String comment;
	@Getter @Setter String image;
}
