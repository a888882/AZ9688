/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.handlers;

import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.WebAppBase;
import org.apache.maven.plugin.MojoExecutionException;

public interface RuntimeHandler<T extends WebAppBase> {

    WebAppBase.DefinitionStages.WithCreate defineAppWithRuntime() throws MojoExecutionException;

    WebAppBase.Update updateAppRuntime(final T app) throws MojoExecutionException;

    AppServicePlan updateAppServicePlan(final T app) throws MojoExecutionException;
}
