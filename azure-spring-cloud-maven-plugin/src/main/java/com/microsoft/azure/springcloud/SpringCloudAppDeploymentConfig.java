/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.springcloud;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.maven.model.Resource;

import java.util.List;
import java.util.Map;

public class SpringCloudAppDeploymentConfig {
    private Integer cpu;
    private Integer memoryInGB;
    private Integer instanceCount;
    private String deploymentName;
    private String jvmOptions;
    private String runtimeVersion;
    private Boolean enablePersistentStorage;
    private Map<String, String> environment;
    private List<Resource> resources;

    public Integer getCpu() {
        return cpu;
    }

    public Integer getMemoryInGB() {
        return memoryInGB;
    }

    public Integer getInstanceCount() {
        return instanceCount;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public Map<String, String> getEnvironment() {
        return environment;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public Boolean isEnablePersistentStorage() {
        return BooleanUtils.isTrue(enablePersistentStorage);
    }

    public String getJvmOptions() {
        return jvmOptions;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public SpringCloudAppDeploymentConfig withCpu(Integer cpu) {
        this.cpu = cpu;
        return this;
    }

    public SpringCloudAppDeploymentConfig withMemoryInGB(Integer memoryInGB) {
        this.memoryInGB = memoryInGB;
        return this;
    }

    public SpringCloudAppDeploymentConfig withInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
        return this;
    }

    public SpringCloudAppDeploymentConfig withJvmOptions(String jvmOptions) {
        this.jvmOptions = jvmOptions;
        return this;
    }

    public SpringCloudAppDeploymentConfig withEnvironment(Map<String, String> environment) {
        this.environment = environment;
        return this;
    }

    public SpringCloudAppDeploymentConfig withDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
        return this;
    }

    public SpringCloudAppDeploymentConfig withResources(List<Resource> resources) {
        this.resources = resources;
        return this;
    }

    public SpringCloudAppDeploymentConfig withEnablePersistentStorage(Boolean enablePersistentStorage) {
        this.enablePersistentStorage = enablePersistentStorage;
        return this;
    }

    public SpringCloudAppDeploymentConfig withRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
        return this;
    }
}
