/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.editor.feature;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.FormEntryAdapter;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.PDESection;
import org.eclipse.pde.internal.ui.parts.FormEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class IncludedFeaturesDetailsSection extends PDESection implements
		IFormPart, IPartSelectionListener {
	private static final String SECTION_DESC = "SiteEditor.IncludedFeaturesDetailsSection.desc"; //$NON-NLS-1$

	private static final String SECTION_FEATURE_LABEL = "SiteEditor.IncludedFeaturesDetailsSection.featureLabel"; //$NON-NLS-1$

	private static final String SECTION_TITLE = "SiteEditor.IncludedFeaturesDetailsSection.title"; //$NON-NLS-1$

	private static final String SECTION_OPTIONAL = "SiteEditor.IncludedFeaturesDetailsSection.optional"; //$NON-NLS-1$

	private static final String SECTION_SEARCH_LOCATION = "SiteEditor.IncludedFeaturesDetailsSection.searchLocation"; //$NON-NLS-1$

	private static final String SECTION_ROOT = "SiteEditor.IncludedFeaturesDetailsSection.root"; //$NON-NLS-1$

	private static final String SECTION_SELF = "SiteEditor.IncludedFeaturesDetailsSection.self"; //$NON-NLS-1$

	private static final String SECTION_BOTH = "SiteEditor.IncludedFeaturesDetailsSection.both"; //$NON-NLS-1$

	protected IFeatureChild fInput;

	private FormEntry fNameText;

	private Button fOptionalButton;

	private Button fSearchRootButton;

	private Button fSearchSelfButton;

	private Button fSearchBothButton;

	private boolean fBlockNotification;

	public IncludedFeaturesDetailsSection(PDEFormPage page, Composite parent) {
		this(page, parent, PDEPlugin.getResourceString(SECTION_TITLE),
				PDEPlugin.getResourceString(SECTION_DESC), SWT.NULL);
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public IncludedFeaturesDetailsSection(PDEFormPage page, Composite parent,
			String title, String desc, int toggleStyle) {
		super(page, parent, Section.DESCRIPTION | toggleStyle);
		getSection().setText(title);
		getSection().setDescription(desc);
		createClient(getSection(), page.getManagedForm().getToolkit());
	}

	public void cancelEdit() {
		fNameText.cancelEdit();
		super.cancelEdit();
	}

	public void commit(boolean onSave) {
		fNameText.commit();
		super.commit(onSave);
	}

	public void createClient(Section section, FormToolkit toolkit) {
		Composite container = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 6;
		container.setLayout(layout);

		fNameText = new FormEntry(container, toolkit, PDEPlugin
				.getResourceString(SECTION_FEATURE_LABEL), null, false);
		fNameText.setFormEntryListener(new FormEntryAdapter(this) {
			public void textValueChanged(FormEntry text) {
				if (fInput != null)
					try {
						fInput.setName(text.getValue());
					} catch (CoreException e) {
						PDEPlugin.logException(e);
					}
			}
		});
		limitTextWidth(fNameText);
		fNameText.setEditable(isEditable());

		fOptionalButton = toolkit.createButton(container, PDEPlugin
				.getResourceString(SECTION_OPTIONAL), SWT.CHECK);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		fOptionalButton.setLayoutData(gd);
		fOptionalButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!fBlockNotification) {
					try {
						fInput.setOptional(fOptionalButton.getSelection());
					} catch (CoreException ce) {
					}
				}
			}
		});
		Label fSearchLocationDescLabel = toolkit.createLabel(container,
				PDEPlugin.getResourceString(SECTION_SEARCH_LOCATION));
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		fSearchLocationDescLabel.setLayoutData(gd);

		fSearchRootButton = toolkit.createButton(container, PDEPlugin
				.getResourceString(SECTION_ROOT), SWT.RADIO);
		fSearchRootButton.setSelection(true);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 5;
		fSearchRootButton.setLayoutData(gd);
		fSearchRootButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!fBlockNotification) {
					try {
						if (fSearchRootButton.getSelection())
							fInput.setSearchLocation(IFeatureChild.ROOT);
					} catch (CoreException ce) {
					}
				}
			}
		});

		fSearchSelfButton = toolkit.createButton(container, PDEPlugin
				.getResourceString(SECTION_SELF), SWT.RADIO);
		fSearchSelfButton.setSelection(true);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 5;
		fSearchSelfButton.setLayoutData(gd);
		fSearchSelfButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!fBlockNotification) {
					try {
						if (fSearchSelfButton.getSelection())
							fInput.setSearchLocation(IFeatureChild.SELF);
					} catch (CoreException ce) {
					}
				}
			}
		});

		fSearchBothButton = toolkit.createButton(container, PDEPlugin
				.getResourceString(SECTION_BOTH), SWT.RADIO);
		fSearchBothButton.setSelection(true);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.horizontalIndent = 5;
		fSearchBothButton.setLayoutData(gd);
		fSearchBothButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!fBlockNotification) {
					try {
						if (fSearchBothButton.getSelection())
							fInput.setSearchLocation(IFeatureChild.BOTH);
					} catch (CoreException ce) {
					}
				}
			}
		});

		toolkit.paintBordersFor(container);
		section.setClient(container);
	}

	public void dispose() {
		IFeatureModel model = (IFeatureModel) getPage().getModel();
		if (model != null)
			model.removeModelChangedListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
	 */
	public void initialize(IManagedForm form) {
		IFeatureModel model = (IFeatureModel) getPage().getModel();
		if (model != null)
			model.addModelChangedListener(this);
		super.initialize(form);
	}

	protected void limitTextWidth(FormEntry entry) {
		GridData gd = (GridData) entry.getText().getLayoutData();
		gd.widthHint = 30;
	}

	public void modelChanged(IModelChangedEvent e) {
		markStale();
	}

	public void refresh() {
		update();
		super.refresh();
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			if (o instanceof IFeatureChild) {
				fInput = (IFeatureChild) o;
			} else {
				fInput = null;
			}
		} else
			fInput = null;
		update();
	}

	public void setFocus() {
		if (fNameText != null)
			fNameText.getText().setFocus();
	}

	private void update() {
		fBlockNotification = true;

		if (fInput != null) {
			fNameText.setValue(fInput.getName(), true);
			fOptionalButton.setSelection(fInput.isOptional());
			int searchLocation = fInput.getSearchLocation();
			fSearchRootButton
					.setSelection(searchLocation == IFeatureChild.ROOT);
			fSearchSelfButton
					.setSelection(searchLocation == IFeatureChild.SELF);
			fSearchBothButton
					.setSelection(searchLocation == IFeatureChild.BOTH);
		} else {
			fNameText.setValue(null);
			fOptionalButton.setSelection(false);
			fSearchRootButton.setSelection(true);
			fSearchSelfButton.setSelection(false);
			fSearchBothButton.setSelection(false);
		}
		fNameText.setEditable(fInput != null && isEditable());
		fOptionalButton.setEnabled(fInput != null && isEditable());
		fSearchRootButton.setEnabled(fInput != null && isEditable());
		fSearchSelfButton.setEnabled(fInput != null && isEditable());
		fSearchBothButton.setEnabled(fInput != null && isEditable());

		fBlockNotification = false;
	}

	public boolean isEditable() {
		return getPage().getPDEEditor().getAggregateModel().isEditable();
	}
}
