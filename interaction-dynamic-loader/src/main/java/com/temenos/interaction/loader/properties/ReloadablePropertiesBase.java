package com.temenos.interaction.loader.properties;

/*
 * #%L
 * interaction-springdsl
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.springframework.core.io.Resource;


/**
 * Useful base class for implementing {@link ReloadableProperties}. Credit to:
 * http://www.wuenschenswert.net/wunschdenken/archives/127
 */
public class ReloadablePropertiesBase extends DelegatingProperties implements ReloadableProperties {
	private static final long serialVersionUID = 1882584866192427533L;
	private List<ReloadablePropertiesListener> listeners = new ArrayList<ReloadablePropertiesListener>();
	private Properties internalProperties;

	public void setListeners(List<ReloadablePropertiesListener> listeners) {
		this.listeners = listeners;
	}

	protected Properties getDelegate() {
		synchronized (this) {
			return internalProperties;
		}
	}

	public Properties getProperties() {
		return getDelegate();
	}
	
	public void addReloadablePropertiesListener(ReloadablePropertiesListener l) {
		listeners.add(l);
	}

	public boolean removeReloadablePropertiesListener(ReloadablePropertiesListener l) {
		return listeners.remove(l);
	}

	protected void notifyPropertiesLoaded(Resource resource, Properties newProperties) {
		PropertiesLoadedEventImpl event = new PropertiesLoadedEventImpl(this, resource, newProperties);
		for (ReloadablePropertiesListener listener : listeners) {
			listener.propertiesChanged(event);
		}
	}
	
	protected boolean updateProperties(Properties newProperties){
		synchronized (this) {
			boolean bNew = false;
			Iterator<Object> iter = newProperties.keySet().iterator();
			while(iter.hasNext()){
				Object key = iter.next();
				Object value = newProperties.get(key);
				bNew = internalProperties.put(key,  value) == null;
			}	
			return bNew;
		}
	}
	
	
	protected void notifyPropertiesChanged(Resource resource, Properties newProperties) {
	
		PropertiesChangedEventImpl event = new PropertiesChangedEventImpl(this, resource, newProperties);
		for (ReloadablePropertiesListener listener : listeners) {
			listener.propertiesChanged(event);
		}
	}

	protected void setProperties(Properties properties) {
		synchronized (this) {
			internalProperties = properties;
		}
	}
}
