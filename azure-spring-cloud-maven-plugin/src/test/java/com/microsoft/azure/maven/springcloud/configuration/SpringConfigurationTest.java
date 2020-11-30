/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.springcloud.configuration;

import com.microsoft.azure.springcloud.SpringCloudAppDeploymentConfig;
import com.microsoft.azure.springcloud.SpringCloudAppConfig;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class SpringConfigurationTest {
    @Test
    public void testWithIsPublic() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        config.withPublic(Boolean.TRUE);
        assertEquals(Boolean.TRUE, config.isPublic());
    }

    @Test
    public void testWithSubscriptionId() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        config.withSubscriptionId("subscriptionId1");
        assertEquals("subscriptionId1", config.getSubscriptionId());
    }

    @Test
    public void testWithResourceGroup() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        config.withResourceGroup("resourceGroup1");
        assertEquals("resourceGroup1", config.getResourceGroup());
    }

    @Test
    public void testWithClusterName() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        config.withClusterName("clusterName1");
        assertEquals("clusterName1", config.getClusterName());
    }

    @Test
    public void testWithAppName() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        config.withAppName("appName1");
        assertEquals("appName1", config.getAppName());
    }

    @Test
    public void testWithRuntimeVersion() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        config.withRuntimeVersion("runtimeVersion1");
        assertEquals("runtimeVersion1", config.getRuntimeVersion());
    }

    @Test
    public void testWithDeployment() {
        final SpringCloudAppConfig config = new SpringCloudAppConfig();
        final SpringCloudAppDeploymentConfig deploy = Mockito.mock(SpringCloudAppDeploymentConfig.class);
        config.withDeployment(deploy);
        assertEquals(deploy, config.getDeployment());
    }
}
