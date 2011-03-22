package ru.mystamps.web.tests.page;

import java.util.List;

import org.openqa.selenium.WebDriver;

import static ru.mystamps.web.SiteMap.ADD_STAMPS_PAGE_URL;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.checkboxField;
import static ru.mystamps.web.tests.page.element.Form.selectField;
import static ru.mystamps.web.tests.page.element.Form.textareaField;
import static ru.mystamps.web.tests.page.element.Form.uploadFileField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AddStampsPage extends AbstractPageWithForm {
	
	public AddStampsPage(final WebDriver driver) {
		super(driver, ADD_STAMPS_PAGE_URL);
		
		hasForm(
			with(
				inputField("country").withLabel(tr("t_country")),
				selectField("issueDay"),
				selectField("issueMonth"),
				selectField("issueYear").withLabel(tr("t_issue_date")),
				inputField("count").withLabel(tr("t_count")),
				checkboxField("withoutPerforation").withLabel(tr("t_without_perforation")),
				inputField("michelNo").withLabel(tr("t_michel_no")),
				inputField("scottNo").withLabel(tr("t_scott_no")),
				inputField("yvertNo").withLabel(tr("t_yvert_no")),
				inputField("gibbonsNo").withLabel(tr("t_sg_no")),
				textareaField("comment").withLabel(tr("t_comment")),
				uploadFileField("image").withLabel(tr("t_image"))
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
	public List<String> getDayFieldValues() {
		return getSelectOptions("issueDay");
	}
	
	public List<String> getMonthFieldValues() {
		return getSelectOptions("issueMonth");
	}
	
	public List<String> getYearFieldValues() {
		return getSelectOptions("issueYear");
	}
	
}
