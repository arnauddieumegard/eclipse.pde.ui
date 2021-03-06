/*******************************************************************************
 * Copyright (c) May 16, 2014 IBM Corporation and others.
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
package x.y.z;

import i.INoRefJavadocDefaultInterface;

/**
 * Tests an interface ref for a restricted default method
 */
public class test9 implements INoRefJavadocDefaultInterface {

	public static void main(String[] args) {
		INoRefJavadocDefaultInterface two = new test9();
		two.m1();
	}
}
