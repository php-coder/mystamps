package ru.mystamps.web.tests.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import lombok.Getter;

import ru.mystamps.web.tests.WebElementUtils;
import ru.mystamps.web.tests.page.element.Form;
import ru.mystamps.web.tests.page.element.Form.CheckboxField;
import ru.mystamps.web.tests.page.element.Form.Field;
import ru.mystamps.web.tests.page.element.Form.InputField;
import ru.mystamps.web.tests.page.element.Form.PasswordField;
import ru.mystamps.web.tests.page.element.Form.SelectField;
import ru.mystamps.web.tests.page.element.Form.SubmitButton;
import ru.mystamps.web.tests.page.element.Form.TextareaField;
import ru.mystamps.web.tests.page.element.Form.UploadFileField;

public abstract class AbstractPageWithForm extends AbstractPage {
	
	private static final String              FORM_LOCATOR = "//form";
	private static final String       INPUT_FIELD_LOCATOR = "//input[@name=\"%s\"]";
	private static final String    PASSWORD_FIELD_LOCATOR = "//input[@name=\"%s\"][@type=\"password\"]";
	private static final String    CHECKBOX_FIELD_LOCATOR = "//input[@name=\"%s\"][@type=\"checkbox\"]";
	private static final String      UPLOAD_FIELD_LOCATOR = "//input[@name=\"%s\"][@type=\"file\"]";
	private static final String      SELECT_FIELD_LOCATOR = "//select[@name=\"%s\"]";
	private static final String    TEXTAREA_FIELD_LOCATOR = "//textarea[@name=\"%s\"]";
	private static final String     SUBMIT_BUTTON_LOCATOR = "//input[@type=\"submit\"]";
	private static final String SUBMIT_WITH_VALUE_LOCATOR = "//input[@type=\"submit\"][@value=\"%s\"]";
	
	private static final String             LABEL_LOCATOR = "//label[@for=\"%s\"]";
	private static final String       FIELD_ERROR_LOCATOR = "//span[@id=\"%s.errors\"]";
	private static final String    FIELD_REQUIRED_LOCATOR = "//span[@id=\"%s.required\"]";
	private static final String        FORM_ERROR_LOCATOR = "//div[@id=\"form.errors\"]";
	
	/// @see isFieldExists()
	private static final Map<String, String> fieldLocators = new HashMap<String, String>();
	
	@Getter private Form form;
	
	static {
		fieldLocators.put(InputField.class.getSimpleName(), INPUT_FIELD_LOCATOR);
		fieldLocators.put(CheckboxField.class.getSimpleName(), CHECKBOX_FIELD_LOCATOR);
		fieldLocators.put(UploadFileField.class.getSimpleName(), UPLOAD_FIELD_LOCATOR);
		fieldLocators.put(PasswordField.class.getSimpleName(), PASSWORD_FIELD_LOCATOR);
		fieldLocators.put(SelectField.class.getSimpleName(), SELECT_FIELD_LOCATOR);
		fieldLocators.put(TextareaField.class.getSimpleName(), TEXTAREA_FIELD_LOCATOR);
	}
	
	public AbstractPageWithForm(final WebDriver driver, final String pageUrl) {
		super(driver, pageUrl);
	}
	
	protected void hasForm(final Form form) {
		this.form = form;
	}
	
	public boolean isFieldExists(final Field field) {
		final String fieldType = field.getClass().getSimpleName();
		if (!fieldLocators.containsKey(fieldType)) {
			throw new IllegalArgumentException("Internal error: unknown field type");
		}
		
		final String fieldXpath =
			String.format(fieldLocators.get(fieldType), field.getName());
		
		return elementWithXPathExists(fieldXpath);
	}
	
	public boolean isFieldHasError(final String id) {
		return elementWithXPathExists(String.format(FIELD_ERROR_LOCATOR, id));
	}
	
	public boolean isSubmitButtonExists(final SubmitButton button) {
		return elementWithXPathExists(String.format(SUBMIT_WITH_VALUE_LOCATOR, button.getValue()));
	}
	
	public void submit() {
		getElementByXPath(SUBMIT_BUTTON_LOCATOR).submit();
	}
	
	public boolean formExists() {
		return elementWithXPathExists(FORM_LOCATOR);
	}
	
	public String getInputLabelValue(final String id) {
		return getTextOfElementByXPath(String.format(LABEL_LOCATOR, id));
	}
	
	public boolean inputHasAsterisk(final String id) {
		return "*".equals(getTextOfElementByXPath(String.format(FIELD_REQUIRED_LOCATOR, id)));
	}
	
	public String getFieldValue(final String name) {
		return getElementByXPath(String.format(INPUT_FIELD_LOCATOR, name)).getValue();
	}
	
	public String getFieldError(final String id) {
		return getTextOfElementByXPath(String.format(FIELD_ERROR_LOCATOR, id));
	}
	
	public String getFormError() {
		return getTextOfElementByXPath(FORM_ERROR_LOCATOR);
	}
	
	public List<String> getFormHints() {
		return WebElementUtils.convertToListWithText(
			getElementsByClassName("hint_item")
		);
	}
	
	public List<String> getSelectOptions(final String id) {
		return WebElementUtils.convertToListWithText(
			new Select(getElementById(id)).getOptions()
		);
	}
	
	public void fillField(final String name, final String value) {
		getElementByName(name).sendKeys(value);
	}
	
}
