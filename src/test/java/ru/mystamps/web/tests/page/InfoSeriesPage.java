/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.tests.page;

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

public class InfoSeriesPage extends AbstractPage {
	
	public InfoSeriesPage(final WebDriver driver) {
		super(driver, Url.INFO_SERIES_PAGE);
	}
	
	public String getImageUrl() {
		return getElementById("image").getAttribute("src");
	}
	
	public String getCountry() {
		return getTextOfElementById("country_name");
	}
	
	public String getYear() {
		return getTextOfElementById("issue_year");
	}
	
	public String getQuantity() {
		return getTextOfElementById("quantity");
	}
	
	public String getPerforated() {
		return getTextOfElementById("perforated");
	}
	
	public String getMichelNumbers() {
		return getTextOfElementById("michel_numbers");
	}
	
	public String getScottNumbers() {
		return getTextOfElementById("scott_numbers");
	}
	
	public String getYvertNumbers() {
		return getTextOfElementById("yvert_numbers");
	}
	
	public String getGibbonsNumbers() {
		return getTextOfElementById("gibbons_numbers");
	}
	
	public String getComment() {
		return getTextOfElementById("comment");
	}
	
}
