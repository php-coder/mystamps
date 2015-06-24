/*
 * Copyright (C) 2009-2015 Slava Semushin <slava.semushin@gmail.com>
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

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ru.mystamps.web.Url;

import static java.util.stream.Collectors.toList;

public class InfoSeriesPage extends AbstractPage {
	
	public InfoSeriesPage(WebDriver driver) {
		super(driver, Url.INFO_SERIES_PAGE);
	}
	
	public List<String> getImageUrls() {
		return getElementsByClassName("series-images")
			.stream()
			.map((WebElement el) -> el.getAttribute("src"))
			.collect(toList());
	}
	
	public String getCategory() {
		return getTextOfElementById("category_name");
	}
	
	public String getCountry() {
		return getTextOfElementById("country_name");
	}
	
	public String getDateOfRelease() {
		return getTextOfElementById("issue_date");
	}
	
	public String getQuantity() {
		return getTextOfElementById("quantity");
	}
	
	public String getPerforated() {
		return getTextOfElementById("perforated");
	}
	
	public String getMichelCatalogInfo() {
		return getTextOfElementById("michel_catalog_info");
	}
	
	public String getScottCatalogInfo() {
		return getTextOfElementById("scott_catalog_info");
	}
	
	public String getYvertCatalogInfo() {
		return getTextOfElementById("yvert_catalog_info");
	}
	
	public String getGibbonsCatalogInfo() {
		return getTextOfElementById("gibbons_catalog_info");
	}
	
	public String getComment() {
		return getTextOfElementById("comment");
	}
	
}
