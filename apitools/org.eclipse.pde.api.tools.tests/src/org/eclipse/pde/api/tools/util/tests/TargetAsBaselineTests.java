/*******************************************************************************
 * Copyright (c) 2015 Manumitting Technologies Inc and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Manumitting Technologies Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.util.tests;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.pde.api.tools.internal.model.ApiModelFactory;
import org.eclipse.pde.api.tools.internal.provisional.ApiPlugin;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiBaseline;
import org.eclipse.pde.api.tools.model.tests.TestSuiteHelper;
import org.eclipse.pde.api.tools.tests.AbstractApiTest;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;

public class TargetAsBaselineTests extends AbstractApiTest {
	ITargetDefinition definition;

	@Override
	public void setUp() {
		IPath path = TestSuiteHelper.getPluginDirectoryPath();
		path = path.append("test-plugins"); //$NON-NLS-1$
		File file = path.toFile();
		assertTrue(file.exists());

		ITargetPlatformService service = (ITargetPlatformService) ApiPlugin.getDefault().acquireService(ITargetPlatformService.class.getName());
		definition = service.newTarget();
		definition.setName("Test Definition"); //$NON-NLS-1$
		ITargetLocation location = service.newDirectoryLocation(file.getAbsolutePath());
		definition.setTargetLocations(new ITargetLocation[] { location });
	}

	/**
	 * Test that an API baseline can be loaded from a target
	 * 
	 * @throws CoreException
	 */
	public void testLoadTarget() throws CoreException {
		IApiBaseline baseline = ApiModelFactory.newApiBaselineFromTarget(getClass().getName(), definition, null);
		assertTrue("This baseline should appear to be from a target definition", ApiModelFactory.isDerivedFromTarget(baseline)); //$NON-NLS-1$
		assertTrue("This baseline should be from this particular target definition", ApiModelFactory.isDerivedFromTarget(baseline, definition)); //$NON-NLS-1$
		assertTrue(baseline.getApiComponents().length >= 3); // includes EEs
	}

	/**
	 * Test that an API baseline loaded from a target can be detected as stale
	 * 
	 * @throws CoreException
	 */
	public void testCheckStale() throws CoreException {
		IApiBaseline baseline = ApiModelFactory.newApiBaselineFromTarget(getClass().getName(), definition, null);
		assertTrue(ApiModelFactory.isDerivedFromTarget(baseline));

		// Assumes definition is a TargetDefinition and setOS() increments the
		// sequence number
		assertTrue(ApiModelFactory.isUpToDateWithTarget(baseline, definition));
		definition.setOS("next"); //$NON-NLS-1$
		assertFalse(ApiModelFactory.isUpToDateWithTarget(baseline, definition));
		assertTrue("This baseline should still be from this particular target definition", ApiModelFactory.isDerivedFromTarget(baseline, definition)); //$NON-NLS-1$
	}
}
