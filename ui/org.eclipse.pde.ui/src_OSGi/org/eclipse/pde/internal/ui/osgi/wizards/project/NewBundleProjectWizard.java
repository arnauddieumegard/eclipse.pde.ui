/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.osgi.wizards.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.internal.ui.elements.ElementList;
import org.eclipse.pde.internal.ui.wizards.*;
import org.eclipse.pde.ui.IProjectProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

public class NewBundleProjectWizard
	extends NewWizard
	implements IExecutableExtension {

	private WizardNewProjectCreationPage mainPage;
	private BundleProjectStructurePage structurePage;
	private BundleProjectCodeGeneratorsPage codegenPage;
	private IConfigurationElement config;

	public static final String PLUGIN_POINT = "projectGenerators";
	public static final String TAG_DESCRIPTION = "description";
	public static final String KEY_TITLE = "NewBundleProjectWizard.MainPage.title";
	public static final String KEY_FTITLE = "NewBundleProjectWizard.MainPage.ftitle";
	public static final String KEY_DESC = "NewBundleProjectWizard.MainPage.desc";
	public static final String KEY_FDESC = "NewBundleProjectWizard.MainPage.fdesc";
	public static final String TAG_WIZARD = "wizard";
	public static final String ATT_FRAGMENT = "fragmentWizard";
	public static final String KEY_CODEGEN_MESSAGE =
		"NewBundleProjectWizard.ProjectCodeGeneratorsPage.message";
	private static final String KEY_WTITLE = "NewBundleProjectWizard.title";

	public NewBundleProjectWizard() {
		setDefaultPageImageDescriptor(PDEPluginImages.DESC_NEWPPRJ_WIZ);
		setDialogSettings(PDEPlugin.getDefault().getDialogSettings());
		setWindowTitle(PDEPlugin.getResourceString(KEY_WTITLE));
		setNeedsProgressMonitor(true);
		PDEPlugin.getDefault().getLabelProvider().connect(this);
	}
	public void addPages() {
		super.addPages();
		mainPage = new WizardNewProjectCreationPage("main");
		if (isFragmentWizard()) {
			mainPage.setTitle(PDEPlugin.getResourceString(KEY_FTITLE));
			mainPage.setDescription(PDEPlugin.getResourceString(KEY_FDESC));
		} else {
			mainPage.setTitle(PDEPlugin.getResourceString(KEY_TITLE));
			mainPage.setDescription(PDEPlugin.getResourceString(KEY_DESC));
		}
		addPage(mainPage);

		IProjectProvider provider = new IProjectProvider() {
			public String getProjectName() {
				return mainPage.getProjectName();
			}
			public IProject getProject() {
				return mainPage.getProjectHandle();
			}
			public IPath getLocationPath() {
				return mainPage.getLocationPath();
			}
		};

		structurePage = new BundleProjectStructurePage(provider, isFragmentWizard());
		addPage(structurePage);
		codegenPage =
			new BundleProjectCodeGeneratorsPage(
				provider,
				structurePage,
				getAvailableCodegenWizards(),
				PDEPlugin.getResourceString(KEY_CODEGEN_MESSAGE),
				isFragmentWizard(),
				config);
		addPage(codegenPage);
	}
	public boolean canFinish() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page == mainPage)
			return false;
		if (page == structurePage && page.getNextPage() == null && page.isPageComplete())
			return true;
		return super.canFinish();
	}
	protected WizardElement createWizardElement(IConfigurationElement config) {
		String name = config.getAttribute(WizardElement.ATT_NAME);
		String id = config.getAttribute(WizardElement.ATT_ID);
		String className = config.getAttribute(WizardElement.ATT_CLASS);
		if (name == null || id == null || className == null)
			return null;
		WizardElement element = new WizardElement(config);
		String imageName = config.getAttribute(WizardElement.ATT_ICON);
		if (imageName != null) {
			IPluginDescriptor pd =
				config.getDeclaringExtension().getDeclaringPluginDescriptor();
			Image image =
				PDEPlugin.getDefault().getLabelProvider().getImageFromPlugin(
					pd,
					imageName);
			element.setImage(image);
		}
		return element;
	}
	public void dispose() {
		super.dispose();
		PDEPlugin.getDefault().getLabelProvider().disconnect(this);
	}

	public ElementList getAvailableCodegenWizards() {
		ElementList wizards = new ElementList("CodegenWizards");
		IPluginRegistry registry = Platform.getPluginRegistry();
		IExtensionPoint point =
			registry.getExtensionPoint(PDEPlugin.getPluginId(), PLUGIN_POINT);
		if (point == null)
			return wizards;
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements =
				extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				if (elements[j].getName().equals(TAG_WIZARD)) {
					WizardElement element = createWizardElement(elements[j]);

					if (element != null) {
						String fragmentAtt =
							element.getConfigurationElement().getAttribute(
								ATT_FRAGMENT);
						boolean fragmentWizard =
							fragmentAtt != null
								&& fragmentAtt.toLowerCase().equals("true");
						if (fragmentWizard == isFragmentWizard()) {
							wizards.add(element);
						}
					}
				}
			}
		}
		return wizards;
	}
	public boolean isFragmentWizard() {
		return false;
	}
	public boolean performFinish() {
		if (structurePage.finish()) {
			boolean hasCodegen = structurePage.getNextPage()!=null;
			
			if (!hasCodegen || codegenPage.finish()) {
				revealSelection(mainPage.getProjectHandle()); 
				return true;
			}
		}
		return false;
	}

	public void setInitializationData(
		IConfigurationElement config,
		String propertyName,
		Object data)
		throws CoreException {
		this.config = config;
	}

}
