/*
 * Copyright 2015-2023 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @since 1.0
 */
class ConsoleLauncherIntegrationTests {

	@Test
	void executeWithoutArgumentsFailsAndPrintsHelpInformation() {
		var result = new ConsoleLauncherWrapper().execute(-1);
		assertAll("empty args array results in display of help information and an exception stacktrace", //
			() -> assertThat(result.err).contains("help information"), //
			() -> assertThat(result.err).contains(
				"Please specify an explicit selector option or use --scan-class-path or --scan-modules") //
		);
	}

	@Test
	void executeWithoutExcludeClassnameOptionDoesNotExcludeClassesAndMustIncludeAllClassesMatchingTheStandardClassnamePattern() {
		String[] args = { "-e", "junit-jupiter", "-p", "org.junit.platform.console.subpackage" };
		assertEquals(9, new ConsoleLauncherWrapper().execute(args).getTestsFoundCount());
	}

	@Test
	void executeWithExcludeClassnameOptionExcludesClasses() {
		String[] args = { "-e", "junit-jupiter", "-p", "org.junit.platform.console.subpackage", "--exclude-classname",
				"^org\\.junit\\.platform\\.console\\.subpackage\\..*" };
		var result = new ConsoleLauncherWrapper().execute(args);
		assertAll("all subpackage test classes are excluded by the class name filter", //
			() -> assertArrayEquals(args, result.args), //
			() -> assertEquals(0, result.code), //
			() -> assertEquals(0, result.getTestsFoundCount()) //
		);
	}

	@Test
	void executeSelectingModuleNames() {
		String[] args1 = { "-e", "junit-jupiter", "-o", "java.base" };
		assertEquals(0, new ConsoleLauncherWrapper().execute(args1).getTestsFoundCount());
		String[] args2 = { "-e", "junit-jupiter", "--select-module", "java.base" };
		assertEquals(0, new ConsoleLauncherWrapper().execute(args2).getTestsFoundCount());
	}

	@Test
	void executeScanModules() {
		String[] args1 = { "-e", "junit-jupiter", "--scan-modules" };
		assertEquals(0, new ConsoleLauncherWrapper().execute(args1).getTestsFoundCount());
	}

}
