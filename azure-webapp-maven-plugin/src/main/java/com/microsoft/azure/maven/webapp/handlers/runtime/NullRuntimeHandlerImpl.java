/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.webapp.handlers.runtime;

import com.microsoft.azure.common.exceptions.AzureExecutionException;
import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.management.appservice.WebApp.DefinitionStages.WithCreate;
import com.microsoft.azure.management.appservice.WebApp.Update;
import com.microsoft.azure.maven.handlers.RuntimeHandler;

public class NullRuntimeHandlerImpl implements RuntimeHandler<WebApp> {
    public static final String NO_RUNTIME_CONFIG = "No runtime related configuration is specified in pom.xml. " +
        "For V1 schema version, please use <javaVersion>, <linuxRuntime> or <containerSettings>, " +
        "For V2 schema version, please use <runtime>.";

    @Override
    public WithCreate defineAppWithRuntime() throws AzureExecutionException {
        throw new AzureExecutionException(NO_RUNTIME_CONFIG);
    }

    @Override
    public Update updateAppRuntime(final WebApp app) {
        return null;
    }

    @Override
    public AppServicePlan updateAppServicePlan(final WebApp app) {
        return null;
    }
}
