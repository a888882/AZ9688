/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.spring.utils;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class MavenUtils {

    public static Xpp3Dom getPluginConfiguration(MavenProject mavenProject, String pluginKey) {
        final Plugin plugin = getPluginFromMavenModel(mavenProject.getModel(), pluginKey);
        return plugin == null ? null : (Xpp3Dom) plugin.getConfiguration();
    }

    private static Plugin getPluginFromMavenModel(Model model, String pluginKey) {
        if (model.getBuild() == null) {
            return null;
        }
        for (final Plugin plugin: model.getBuild().getPlugins()) {
            if (pluginKey.equalsIgnoreCase(plugin.getKey())) {
                return plugin;
            }
        }

        if (model.getBuild().getPluginManagement() == null) {
            return null;
        }

        for (final Plugin plugin: model.getBuild().getPluginManagement().getPlugins()) {
            if (pluginKey.equalsIgnoreCase(plugin.getKey())) {
                return plugin;
            }
        }

        return null;
    }

    private MavenUtils() {

    }

}
