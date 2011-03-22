package ru.mystamps.web.tests.cases;

import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;

import org.junit.Test;

import ru.mystamps.web.tests.page.AddStampsPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

public class WhenUserAddStamps extends WhenUserAtAnyPageWithForm<AddStampsPage> {
	
	private static final int DAYS_IN_MONTH  = 31;
	private static final int MONTHS_IN_YEAR = 12;
	private static final int SINCE_YEAR     = 1840;
	private static final int CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final List<String> expectedDays =
		new ArrayList<String>(DAYS_IN_MONTH + 1);
	
	private static final List<String> expectedMonths =
		new ArrayList<String>(MONTHS_IN_YEAR + 1);
	
	private static final List<String> expectedYears =
		new ArrayList<String>(CURRENT_YEAR - SINCE_YEAR + 1);
	
	static {
		expectedDays.add("");
		for (int i = 1; i <= DAYS_IN_MONTH; i++) {
			expectedDays.add(String.valueOf(i));
		}
		
		expectedMonths.add("");
		for (int i = 1; i <= MONTHS_IN_YEAR; i++) {
			expectedMonths.add(String.valueOf(i));
		}
		
		expectedYears.add("");
		// years in reverse order
		for (int i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			expectedYears.add(String.valueOf(i));
		}
	}
	
	public WhenUserAddStamps() {
		super(AddStampsPage.class);
		hasTitle(tr("t_add_series"));
		hasHeader(tr("t_add_series_ucfirst"));
	}
	
	@Test
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	@Test
	public void issueDayFieldShouldHaveOptionsFor31Days() {
		assertThat(page.getDayFieldValues(), is(expectedDays));
	}
	
	@Test
	public void issueMonthFieldShouldHaveOptionsFor12Months() {
		assertThat(page.getMonthFieldValues(), is(expectedMonths));
	}
	
	@Test
	public void issueYearFieldShouldHaveOptionsForRangeFrom1840ToCurrentYear() {
		assertThat(page.getYearFieldValues(), is(expectedYears));
	}
	
}
