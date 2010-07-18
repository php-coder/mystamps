package ru.mystamps.site.beans;

import java.util.Calendar;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

@ManagedBean(name="form")
@ApplicationScoped
public class FormBean {
	private static final int DAYS_IN_MONTHS = 31;
	private static final int MONTHS_IN_YEAR = 12;
	private static final int SINCE_YEAR     = 1840;
	private static final int CURRENT_YEAR   = Calendar.getInstance().get(Calendar.YEAR);
	private static final int YEARS_COUNT    = CURRENT_YEAR - SINCE_YEAR + 1; // TODO: why +1?
	
	private static final SelectItem[] days   = new SelectItem[DAYS_IN_MONTHS + 1];
	private static final SelectItem[] months = new SelectItem[MONTHS_IN_YEAR + 1];
	private static final SelectItem[] years  = new SelectItem[YEARS_COUNT + 1];
	
	static {
		int i;
		final SelectItem empty = new SelectItem("");
		
		// init days
		days[0]   = empty;
		for (i = 1; i <= DAYS_IN_MONTHS; ++i) {
			final String day = new Integer(i).toString();
			days[i] = new SelectItem(day);
		}
		
		// init months
		months[0] = empty;
		for (i = 1; i <= MONTHS_IN_YEAR; ++i) {
			final String month = new Integer(i).toString();
			months[i] = new SelectItem(month);
		}
		
		// init years
		years[0]  = empty;
		for (i = 0; i < YEARS_COUNT; ++i) {
			final String year = new Integer(CURRENT_YEAR - i).toString();
			years[i + 1] = new SelectItem(year);
		}
		
	}
	
	public SelectItem[] getDays() {
		return days;
	}
	
	public SelectItem[] getMonths() {
		return months;
	}
	
	public SelectItem[] getYears() {
		return years;
	}
	
}
