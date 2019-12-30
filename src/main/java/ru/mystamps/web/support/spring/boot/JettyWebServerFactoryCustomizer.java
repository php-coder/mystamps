/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.support.spring.boot;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JettyWebServerFactoryCustomizer
	implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {
	
	private static final JettyServerCustomizer JETTY_CUSTOMIZER = new JettyServerCustomizer() {
		@Override
		@SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
		public void customize(Server server) {
			for (Connector connector : server.getConnectors()) {
				if (connector instanceof ServerConnector) {
					HttpConnectionFactory connectionFactory =
						connector.getConnectionFactory(HttpConnectionFactory.class);
					if (connectionFactory != null) {
						HttpConfiguration httpConfiguration =
							connectionFactory.getHttpConfiguration();
						if (httpConfiguration != null) {
							httpConfiguration.setSendServerVersion(false);
						}
					}
				}
			}
		}
	};
	
	@Override
	public void customize(JettyServletWebServerFactory factory) {
			factory.addServerCustomizers(JETTY_CUSTOMIZER);
	}
	
}
