/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.springcloud.configuration;

import com.microsoft.azure.maven.springcloud.pom.PomXmlUpdater;
import com.microsoft.azure.springcloud.SpringCloudAppDeploymentConfig;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpringCloudAppDeploymentSettingsTest {
    @Test
    public void testSetCpu() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withCpu(1);
        assertEquals(Integer.valueOf(1), deploy.getCpu());
    }

    @Test
    public void testSetMemoryInGB() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withMemoryInGB(2);
        assertEquals(Integer.valueOf(2), deploy.getMemoryInGB());
    }

    @Test
    public void testSetInstanceCount() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withInstanceCount(3);
        assertEquals(Integer.valueOf(3), deploy.getInstanceCount());
    }

    @Test
    public void testSetDeploymentName() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withDeploymentName("deploymentName1");
        assertEquals("deploymentName1", deploy.getDeploymentName());
    }

    @Test
    public void testSetJvmOptions() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withJvmOptions("jvmOptions1");
        assertEquals("jvmOptions1", deploy.getJvmOptions());
    }

    @Test
    public void testSetRuntimeVersion() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withRuntimeVersion("8");
        assertEquals("8", deploy.getRuntimeVersion());
    }

    @Test
    public void testSaveToXml() {
        final SpringCloudAppDeploymentConfig deploy = new SpringCloudAppDeploymentConfig();
        deploy.withCpu(1);
        deploy.withMemoryInGB(2);
        deploy.withInstanceCount(3);
        deploy.withRuntimeVersion("8");
        deploy.withJvmOptions("jvmOptions1");
        final Document document = DocumentHelper.createDocument();
        final Element root = document.addElement("test");
        PomXmlUpdater.applyToDom4j(deploy, root);
        final String xml = "<test>" +
            "<cpu>1</cpu>" +
            "<memoryInGB>2</memoryInGB>" +
            "<instanceCount>3</instanceCount>" +
            "<jvmOptions>jvmOptions1</jvmOptions>" +
            "<runtimeVersion>8</runtimeVersion>" +
            "</test>" +
            "";
        assertEquals(xml, root.asXML());
    }
}
