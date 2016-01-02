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
package ru.mystamps.web.support.spring.boot;

import org.springframework.context.annotation.Configuration;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

@Configuration
public class JettyServletContainerCustomizer implements EmbeddedServletContainerCustomizer {
	
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
	public void customize(ConfigurableEmbeddedServletContainer container) {
		if (container instanceof JettyEmbeddedServletContainerFactory) {
			JettyEmbeddedServletContainerFactory jetty =
				(JettyEmbeddedServletContainerFactory)container;
			jetty.addServerCustomizers(JETTY_CUSTOMIZER);
		}
	}
	
}
