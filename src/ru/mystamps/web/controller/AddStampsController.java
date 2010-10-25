package ru.mystamps.web.controller;

import java.util.Calendar;
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
	private static final Integer CURRENT_YEAR   = Calendar.getInstance().get(Calendar.YEAR);
	
	private static final Map<Integer, Integer> days;
	private static final Map<Integer, Integer> months;
	private static final Map<Integer, Integer> years;
	
	static {
		
		days = new LinkedHashMap<Integer, Integer>();
		for (Integer i = 1; i <= DAYS_IN_MONTH; ++i) {
			days.put(i, i);
		}
		
		months = new LinkedHashMap<Integer, Integer>();
		for (Integer i = 1; i <= MONTHS_IN_YEAR; ++i) {
			months.put(i, i);
		}
		
		years = new LinkedHashMap<Integer, Integer>();
		for (Integer i = CURRENT_YEAR; i >= SINCE_YEAR; --i) {
			years.put(i, i);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public void showForm(final Model model) {
		model.addAttribute("days", days);
		model.addAttribute("months", months);
		model.addAttribute("years", years);
		model.addAttribute(new AddStampsForm());
	}
	
}

