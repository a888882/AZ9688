/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.webapp.handlers;

import com.microsoft.azure.management.appservice.AppServicePlan;
import com.microsoft.azure.management.appservice.OperatingSystem;
import com.microsoft.azure.management.appservice.WebApp;
import com.microsoft.azure.maven.Utils;
import com.microsoft.azure.maven.webapp.utils.WebAppUtils;
import org.apache.maven.settings.Server;

import static com.microsoft.azure.maven.Utils.assureServerExist;

public class PrivateDockerHubRuntimeHandlerImpl extends BaseRuntimeHandler {
    public static class Builder extends BaseRuntimeHandler.Builder<Builder>{
        @Override
        protected PrivateDockerHubRuntimeHandlerImpl.Builder self() {
            return this;
        }

        @Override
        public PrivateDockerHubRuntimeHandlerImpl build() {
            return new PrivateDockerHubRuntimeHandlerImpl(this);
        }
    }

    private PrivateDockerHubRuntimeHandlerImpl(final Builder builder) {
        super(builder);
    }

    @Override
    public WebApp.DefinitionStages.WithCreate defineAppWithRuntime() throws Exception {
        final Server server = Utils.getServer(settings, serverId);
        assureServerExist(server, serverId);

        final AppServicePlan plan = WebAppUtils.createOrGetAppServicePlan(servicePlanName, resourceGroup, azure,
            servicePlanResourceGroup, region, pricingTier, log, OperatingSystem.LINUX);
        return WebAppUtils.defineLinuxApp(resourceGroup, appName, azure, plan)
            .withPrivateDockerHubImage(image)
            .withCredentials(server.getUsername(), server.getPassword());
    }

    @Override
    public WebApp.Update updateAppRuntime(final WebApp app) throws Exception {
        WebAppUtils.assureLinuxWebApp(app);
        WebAppUtils.clearTags(app);

        final Server server = Utils.getServer(settings, serverId);
        assureServerExist(server, serverId);
        return app.update()
            .withPrivateDockerHubImage(image)
            .withCredentials(server.getUsername(), server.getPassword());
    }
}
