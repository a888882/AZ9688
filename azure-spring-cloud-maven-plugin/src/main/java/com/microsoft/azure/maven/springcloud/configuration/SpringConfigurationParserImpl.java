/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.springcloud.configuration;

import com.microsoft.azure.maven.springcloud.AbstractSpringMojo;
import com.microsoft.azure.springcloud.SpringCloudAppConfig;

public class SpringConfigurationParserImpl implements SpringConfigurationParser {
    @Override
    public SpringCloudAppConfig parse(AbstractSpringMojo springMojo) {
        return new SpringCloudAppConfig()
                .withAppName(springMojo.getAppName())
                .withClusterName(springMojo.getClusterName())
                .withDeployment(springMojo.getDeployment())
                .withRuntimeVersion(springMojo.getRuntimeVersion())
                .withPublic(springMojo.isPublic())
                .withSubscriptionId(springMojo.getSubscriptionId());
    }
}
