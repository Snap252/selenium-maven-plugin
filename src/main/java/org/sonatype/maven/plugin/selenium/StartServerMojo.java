package org.sonatype.maven.plugin.selenium;

import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.selenium.remote.server.SeleniumServer;

@Mojo(name = "start-server", requiresProject = false, threadSafe = false)
class StartServerMojo extends AbstractMojo {

	@Parameter
	StandaloneConfiguration serverConfiguration = new StandaloneConfiguration();
	@Parameter
	boolean skip;

	@Parameter
	boolean wait;

	@Parameter
	private Properties systemProperties;

	@Override
	public void execute() {
		if (skip) {
			getLog().info("Skipping startup");
			return;
		}

		if (serverConfiguration == null) {
			throw new NullPointerException("serverConfiguration must not be null");
		}

		final SeleniumServer server = new SeleniumServer(serverConfiguration);
		getLog().info("booting selenium server:" + serverConfiguration);
		Properties oldSystemProperties = new Properties(System.getProperties());
		if (systemProperties != null) {
			getLog().info("setting System properties: " + systemProperties);
			System.getProperties().putAll(systemProperties);;
		}
		
		try {
			server.boot();
			if (wait)
				try {
					getLog().info("waiting for ever... (Press Ctrl+C to exit)...");
					Thread.sleep(Long.MAX_VALUE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		} finally {
			if (systemProperties != null)
			System.setProperties(oldSystemProperties);
		}
	}

}
