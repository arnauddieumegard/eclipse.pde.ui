/*
 * Copyright (c) 2002 IBM Corp.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */
package org.eclipse.pde.internal.ui.util;

import org.eclipse.jface.viewers.*;
import java.util.*;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.pde.internal.ui.PDEPluginImages;
import org.eclipse.core.runtime.IPluginDescriptor;
import java.net.*;

/**
 * @version 	1.0
 * @author
 */
public class SharedLabelProvider
	extends LabelProvider
	implements ITableLabelProvider {
	public static final int F_ERROR = 1;
	public static final int F_WARNING = 2;
	public static final int F_EXPORT = 4;
	public static final int F_EDIT = 8;
	public static final int F_BINARY = 16;
	public static final int F_EXTERNAL = 32;
	public static final int F_JAVA = 64;
	public static final int F_JAR = 128;
	Hashtable images = new Hashtable();
	ArrayList consumers = new ArrayList();

	public SharedLabelProvider() {

	}
	public void connect(Object consumer) {
		if (!consumers.contains(consumer))
			consumers.add(consumer);
	}
	public void disconnect(Object consumer) {
		consumers.remove(consumer);
		if (consumers.size() == 0) {
			reset();
		}
	}
	private void reset() {
		for (Enumeration enum = images.elements(); enum.hasMoreElements();) {
			Image image = (Image) enum.nextElement();
			image.dispose();
		}
		images.clear();
	}

	public Image get(ImageDescriptor desc) {
		return get(desc, 0);
	}

	public Image get(ImageDescriptor desc, int flags) {
		Object key = desc;

		if (flags != 0) {
			key = getKey(desc.hashCode(), flags);
		}
		Image image = (Image) images.get(key);
		if (image == null) {
			image = createImage(desc, flags);
			images.put(key, image);
		}
		return image;
	}
	
	public Image get(Image image, int flags) {
		if (flags==0) return image;
		String key = getKey(image.hashCode(), flags);
		Image resultImage = (Image)images.get(key);
		if (resultImage == null) {
			resultImage = createImage(image, flags);
			images.put(key, resultImage);
		}
		return resultImage;
	}

	private String getKey(long hashCode, int flags) {
		return (""+hashCode) + ":"+flags;
	}

	private Image createImage(ImageDescriptor baseDesc, int flags) {
		if (flags == 0) {
			return baseDesc.createImage();
		}
		ImageDescriptor[] lowerLeft = getLowerLeftOverlays(flags);
		ImageDescriptor[] upperRight = getUpperRightOverlays(flags);
		ImageDescriptor[] lowerRight = getLowerRightOverlays(flags);
		ImageDescriptor[] upperLeft = getUpperLeftOverlays(flags);
		OverlayIcon compDesc =
			new OverlayIcon(
				baseDesc,
				new ImageDescriptor[][] { upperRight, lowerRight, lowerLeft, upperLeft });
		return compDesc.createImage();
	}
	
	private Image createImage(Image baseImage, int flags) {
		if (flags == 0) {
			return baseImage;
		}
		ImageDescriptor[] lowerLeft = getLowerLeftOverlays(flags);
		ImageDescriptor[] upperRight = getUpperRightOverlays(flags);
		ImageDescriptor[] lowerRight = getLowerRightOverlays(flags);
		ImageDescriptor[] upperLeft = getUpperLeftOverlays(flags);
		ImageOverlayIcon compDesc =
			new ImageOverlayIcon(
				baseImage,
				new ImageDescriptor[][] { upperRight, lowerRight, lowerLeft, upperLeft });
		return compDesc.createImage();
	}

	private ImageDescriptor[] getLowerLeftOverlays(int flags) {
		if ((flags & F_ERROR) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_ERROR_CO };
		if ((flags & F_WARNING) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_WARNING_CO };
		return null;
	}

	private ImageDescriptor[] getUpperRightOverlays(int flags) {
		if ((flags & F_EXPORT) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_EXPORT_CO };
		if ((flags & F_EDIT) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_DOC_CO };
		if ((flags & F_JAVA) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_JAVA_CO };
		return null;
	}
	
	private ImageDescriptor[] getLowerRightOverlays(int flags) {
		if ((flags & F_JAR) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_JAR_CO };
		return null;
	}
	
	private ImageDescriptor[] getUpperLeftOverlays(int flags) {
		if ((flags & F_EXTERNAL) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_EXTERNAL_CO };
		if ((flags & F_BINARY) != 0)
			return new ImageDescriptor[] { PDEPluginImages.DESC_BINARY_CO };
		return null;
	}

	public String getColumnText(Object obj, int index) {
		return getText(obj);
	}
	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}

	public Image getImageFromPlugin(
		IPluginDescriptor pluginDescriptor,
		String subdirectoryAndFilename) {
		URL installURL = pluginDescriptor.getInstallURL();
		return getImageFromURL(installURL, subdirectoryAndFilename);
	}

	public Image getImageFromURL(
		URL installURL,
		String subdirectoryAndFilename) {
		Image image = null;
		try {
			URL newURL = new URL(installURL, subdirectoryAndFilename);
			String key = newURL.toString();
			image = (Image)images.get(key);
			if (image == null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(newURL);
				image = desc.createImage();
				images.put(key, image);
			}
		} catch (MalformedURLException e) {
		} catch (SWTException e) {
		}
		return image;
	}
}