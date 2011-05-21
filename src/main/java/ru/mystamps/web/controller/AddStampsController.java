package ru.mystamps.web.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ru.mystamps.web.model.AddStampsForm;

import static ru.mystamps.web.SiteMap.ADD_STAMPS_PAGE_URL;

@Controller
@RequestMapping(ADD_STAMPS_PAGE_URL)
public class AddStampsController {
	
	private static final Integer DAYS_IN_MONTH  = 31;
	private static final Integer MONTHS_IN_YEAR = 12;
	private static final Integer SINCE_YEAR     = 1840;
	private static final Integer CURRENT_YEAR   = new GregorianCalendar().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> DAYS;
	private static final Map<Integer, Integer> MONTHS;
	private static final Map<Integer, Integer> YEARS;
	
	static {
		
		DAYS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = 1; i <= DAYS_IN_MONTH; i++) {
			DAYS.put(i, i);
		}
		
		MONTHS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = 1; i <= MONTHS_IN_YEAR; i++) {
			MONTHS.put(i, i);
		}
		
		YEARS = new LinkedHashMap<Integer, Integer>();
		for (Integer i = CURRENT_YEAR; i >= SINCE_YEAR; i--) {
			YEARS.put(i, i);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public void showForm(final Model model) {
		model.addAttribute("days", DAYS);
		model.addAttribute("months", MONTHS);
		model.addAttribute("years", YEARS);
		model.addAttribute(new AddStampsForm());
	}
	
}

