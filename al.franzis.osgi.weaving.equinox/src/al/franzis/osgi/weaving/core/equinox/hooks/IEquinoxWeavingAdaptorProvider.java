/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package al.franzis.osgi.weaving.core.equinox.hooks;

import al.franzis.osgi.weaving.core.equinox.adaptors.IEquinoxWeavingAdaptor;


public interface IEquinoxWeavingAdaptorProvider {

    public IEquinoxWeavingAdaptor getAdaptor(long bundleID);

    public IEquinoxWeavingAdaptor getHostBundleAdaptor(long bundleID);

    public void resetAdaptor(long bundleID);

}
