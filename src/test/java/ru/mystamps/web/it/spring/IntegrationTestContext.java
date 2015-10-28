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
package ru.mystamps.web.it.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

// CheckStyle: ignore AvoidStarImportCheck for next 1 line
import ru.mystamps.web.it.page.*;

@Configuration
public class IntegrationTestContext {
	
	@Bean
	public WebDriver getWebDriver() {
		boolean enableJavascript = true;
		return new HtmlUnitDriver(BrowserVersion.FIREFOX_38, enableJavascript);
	}
	
	@Bean
	public AddCategoryPage getAddCategoryPage() {
		return new AddCategoryPage(getWebDriver());
	}
	
	@Bean
	public AuthAccountPage getAuthAccountPage() {
		return new AuthAccountPage(getWebDriver());
	}
	
	@Bean
	public ErrorPage getErrorPage() {
		return new ErrorPage(getWebDriver());
	}
	
	@Bean
	public IndexPage getIndexPage() {
		return new IndexPage(getWebDriver());
	}
	
	@Bean
	public InfoCategoryPage getInfoCategoryPage() {
		return new InfoCategoryPage(getWebDriver());
	}
	
}
