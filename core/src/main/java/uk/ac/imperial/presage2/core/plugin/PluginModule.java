/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.core.plugin;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Binds an array of plugin classes.
 * 
 * @author Sam Macbeth
 * 
 */
public class PluginModule extends AbstractModule {

	final Set<Class<? extends Plugin>> plugins;
	final Set<Plugin> pluginInstances;

	public PluginModule() {
		super();
		this.plugins = new HashSet<Class<? extends Plugin>>();
		this.pluginInstances = new HashSet<Plugin>();
	}

	public PluginModule(Class<? extends Plugin>... plugins) {
		this();
		for (Class<? extends Plugin> p : plugins) {
			this.plugins.add(p);
		}
	}

	public PluginModule addPlugin(Class<? extends Plugin> plugin) {
		this.plugins.add(plugin);
		return this;
	}

	public PluginModule addPluginInstance(Plugin plugin) {
		this.pluginInstances.add(plugin);
		return this;
	}

	@Override
	protected void configure() {
		Multibinder<Plugin> pluginBinder = Multibinder.newSetBinder(binder(),
				Plugin.class);
		for (Class<? extends Plugin> p : plugins) {
			pluginBinder.addBinding().to(p);
		}
		for (Plugin p : pluginInstances) {
			pluginBinder.addBinding().toInstance(p);
		}
	}

}
