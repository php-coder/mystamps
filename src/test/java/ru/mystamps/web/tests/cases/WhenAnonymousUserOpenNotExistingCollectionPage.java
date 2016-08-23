/*
 * Copyright (C) 2009-2016 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.tests.cases;

import java.net.HttpURLConnection;

import org.testng.annotations.Test;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.NotFoundErrorPage;

import static ru.mystamps.web.tests.TranslationUtils.tr;

import static org.fest.assertions.api.Assertions.assertThat;

public class WhenAnonymousUserOpenNotExistingCollectionPage
	extends WhenAnyUserAtAnyPage<NotFoundErrorPage> {

	public WhenAnonymousUserOpenNotExistingCollectionPage() {
		super(NotFoundErrorPage.class);
		hasTitleWithoutStandardPrefix(tr("t_404_title"));
		hasResponseServerCode(HttpURLConnection.HTTP_NOT_FOUND);
	}

	@Test(groups = "logic")
	public void shouldShow404Page() {
		String url = Url.INFO_COLLECTION_PAGE.replace("{slug}", "collection-404-error-test");
		page.open(url);

		checkStandardStructure();

		assertThat(page.getErrorMessage()).isEqualTo(tr("t_404_description", "\n"));
		assertThat(page.getErrorCode()).isEqualTo("404");
	}

}
