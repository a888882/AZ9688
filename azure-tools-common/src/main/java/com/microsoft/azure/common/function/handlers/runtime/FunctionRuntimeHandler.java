/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.common.function.handlers.runtime;

import com.microsoft.azure.common.docker.IDockerCredentialProvider;
import com.microsoft.azure.common.exceptions.AzureExecutionException;
import com.microsoft.azure.common.function.configurations.FunctionExtensionVersion;
import com.microsoft.azure.common.function.configurations.RuntimeConfiguration;
import com.microsoft.azure.common.handlers.runtime.BaseRuntimeHandler;
import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.FunctionApp;
import com.microsoft.azure.management.resources.ResourceGroup;

public abstract class FunctionRuntimeHandler extends BaseRuntimeHandler<FunctionApp> {

    protected FunctionExtensionVersion functionExtensionVersion;
    protected RuntimeConfiguration runtimeConfiguration;
    protected IDockerCredentialProvider dockerCredentialProvider;

    public abstract static class Builder<T extends FunctionRuntimeHandler.Builder<T>> extends BaseRuntimeHandler.Builder<T> {

        protected FunctionExtensionVersion functionExtensionVersion;
        protected RuntimeConfiguration runtimeConfiguration;
        protected IDockerCredentialProvider dockerCredentialProvider;

        public T functionExtensionVersion(final FunctionExtensionVersion value) {
            this.functionExtensionVersion = value;
            return self();
        }

        public T runtime(final RuntimeConfiguration value) {
            this.runtimeConfiguration = value;
            return self();
        }

        public T dockerCredentialProvider(IDockerCredentialProvider value) {
            this.dockerCredentialProvider = value;
            return self();
        }

        public abstract FunctionRuntimeHandler build();

        protected abstract T self();
    }

    protected FunctionRuntimeHandler(Builder<?> builder) {
        super(builder);
        this.functionExtensionVersion = builder.functionExtensionVersion;
        this.runtimeConfiguration = builder.runtimeConfiguration;
        this.dockerCredentialProvider = builder.dockerCredentialProvider;
    }

    @Override
    public abstract FunctionApp.DefinitionStages.WithCreate defineAppWithRuntime() throws AzureExecutionException;

    @Override
    public abstract FunctionApp.Update updateAppRuntime(FunctionApp app) throws AzureExecutionException;

    @Override
    protected void changeAppServicePlan(FunctionApp app, AppServicePlan appServicePlan) throws AzureExecutionException {
        app.update().withExistingAppServicePlan(appServicePlan).apply();
    }

    protected FunctionApp.DefinitionStages.Blank defineFunction() {
        return azure.appServices().functionApps().define(appName);
    }

    protected ResourceGroup getResourceGroup() {
        return azure.resourceGroups().getByName(resourceGroup);
    }
}
