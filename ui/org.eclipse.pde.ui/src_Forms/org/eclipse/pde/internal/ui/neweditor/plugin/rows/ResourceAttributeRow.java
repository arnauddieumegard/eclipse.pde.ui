/*
 * Created on Jan 30, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.pde.internal.ui.neweditor.plugin.rows;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.internal.core.ischema.ISchemaAttribute;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.neweditor.IContextPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.*;
/**
 * @author dejan
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ResourceAttributeRow extends ReferenceAttributeRow {
	public ResourceAttributeRow(IContextPart part, ISchemaAttribute att) {
		super(part, att);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.neweditor.plugin.ReferenceAttributeRow#openReference()
	 */
	protected void openReference() {
		IFile file = getFile();
		if (file!=null && file.exists()) {
			try {
				IDE.openEditor(PDEPlugin.getActivePage(), file, true);
			} catch (PartInitException e) {
				PDEPlugin.logException(e);
			}
		} else {
			Display.getCurrent().beep();
		}
	}
	private IFile getFile() {
		String value = text.getText();
		if (value.length()==0) return null;
		IPath path = getProject().getFullPath().append(value);
		return getProject().getWorkspace().getRoot().getFile(path);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.internal.ui.neweditor.plugin.ReferenceAttributeRow#browse()
	 */
	protected void browse() {
		final IProject project = part.getPage().getPDEEditor()
				.getCommonProject();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				PDEPlugin.getActiveWorkbenchShell(),
				new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setInput(project.getWorkspace());
		IFile file = getFile();
		if (file!=null)
			dialog.setInitialSelection(file);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IProject)
					return ((IProject) element).equals(project);
				return true;
			}
		});
		dialog.setAllowMultiple(false);
		dialog
				.setTitle(PDEPlugin
						.getResourceString("ManifestEditor.ResourceAttributeCellEditor.title"));
		dialog
				.setMessage(PDEPlugin
						.getResourceString("ManifestEditor.ResourceAttributeCellEditor.message"));
		dialog.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection != null && selection.length > 0
						&& selection[0] instanceof IFile)
					return new Status(IStatus.OK, PDEPlugin.getPluginId(),
							IStatus.OK, "", null);
				else
					return new Status(IStatus.ERROR, PDEPlugin.getPluginId(),
							IStatus.ERROR, "", null);
			}
		});
		if (dialog.open() == ElementTreeSelectionDialog.OK) {
			file = (IFile) dialog.getFirstResult();
			String value = file.getProjectRelativePath().toString();
			text.setText(value);
		}
	}
}