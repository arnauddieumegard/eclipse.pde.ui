/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.pde.internal.core.ifeature;

import org.eclipse.core.runtime.CoreException;

public interface IFeatureInfo extends IFeatureObject {
	String P_URL = "p_url"; //$NON-NLS-1$

	String P_DESC = "p_desc"; //$NON-NLS-1$

	/**
	 * @return the url for the feature, possibly <code>null</code>
	 */
	public String getURL();

	public String getDescription();

	public void setURL(String url) throws CoreException;

	public void setDescription(String desc) throws CoreException;

	public boolean isEmpty();

	public int getIndex();
}
