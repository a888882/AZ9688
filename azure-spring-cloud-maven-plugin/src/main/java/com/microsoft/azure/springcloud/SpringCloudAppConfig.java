/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.springcloud;

import org.apache.commons.lang3.BooleanUtils;

public class SpringCloudAppConfig {

    private String subscriptionId;
    private String clusterName;
    private String appName;
    private Boolean isPublic;
    private String resourceGroup;
    private String runtimeVersion;
    private String activeDeploymentName;
    private SpringCloudAppDeploymentConfig deploymentConfig;

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public Boolean isPublic() {
        return BooleanUtils.isTrue(isPublic);
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public String getClusterName() {
        return clusterName;
    }

    public String getAppName() {
        return appName;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public String getActiveDeploymentName() {
        return activeDeploymentName;
    }

    public SpringCloudAppDeploymentConfig getDeployment() {
        return deploymentConfig;
    }

    public SpringCloudAppConfig withPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public SpringCloudAppConfig withSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SpringCloudAppConfig withResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
        return this;
    }

    public SpringCloudAppConfig withClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public SpringCloudAppConfig withAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public SpringCloudAppConfig withRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
        return this;
    }

    public SpringCloudAppConfig withActiveDeploymentName(String deploymentName) {
        this.activeDeploymentName = deploymentName;
        return this;
    }

    public SpringCloudAppConfig withDeployment(SpringCloudAppDeploymentConfig deploymentConfig) {
        this.deploymentConfig = deploymentConfig;
        return this;
    }
}
