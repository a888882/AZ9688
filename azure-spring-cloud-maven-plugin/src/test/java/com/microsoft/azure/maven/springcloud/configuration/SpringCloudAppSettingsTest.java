/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.springcloud.configuration;

import com.microsoft.azure.maven.springcloud.pom.PomXmlUpdater;
import com.microsoft.azure.springcloud.SpringCloudAppConfig;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SpringCloudAppSettingsTest {
    @Test
    public void testSetSubscriptionId() {
        final SpringCloudAppConfig app = new SpringCloudAppConfig();
        app.withSubscriptionId("subscriptionId1");
        assertEquals("subscriptionId1", app.getSubscriptionId());
    }

    @Test
    public void testSetClusterName() {
        final SpringCloudAppConfig app = new SpringCloudAppConfig();
        app.withClusterName("clusterName1");
        assertEquals("clusterName1", app.getClusterName());
    }

    @Test
    public void testSetAppName() {
        final SpringCloudAppConfig app = new SpringCloudAppConfig();
        app.withAppName("appName1");
        assertEquals("appName1", app.getAppName());
    }

    @Test
    public void testSetIsPublic() {
        final SpringCloudAppConfig app = new SpringCloudAppConfig();
        app.withPublic(true);
        assertEquals(Boolean.TRUE, app.getIsPublic());
    }

    @Test
    public void testSaveToDom4j() {
        final SpringCloudAppConfig app = new SpringCloudAppConfig();
        app.withSubscriptionId("subscriptionId1");
        app.withClusterName("clusterName1");
        app.withAppName("appName1");
        app.withPublic(true);
        final Document document = DocumentHelper.createDocument();
        final Element root = document.addElement("test");
        PomXmlUpdater.applyToDom4j(app, root);
        final String xml = "<test>" +
            "<subscriptionId>subscriptionId1</subscriptionId>" +
            "<clusterName>clusterName1</clusterName>" +
            "<appName>appName1</appName>" +
            "<isPublic>true</isPublic></test>";
        assertEquals(xml, root.asXML());

        app.withPublic(null);
    }

    @Test
    public void testSaveToDom4jNullProperty() {
        final SpringCloudAppConfig app = new SpringCloudAppConfig();
        app.withSubscriptionId("subscriptionId1");
        app.withClusterName("clusterName1");
        app.withAppName("appName1");
        final Document document = DocumentHelper.createDocument();
        final Element root = document.addElement("test");
        PomXmlUpdater.applyToDom4j(app, root);
        final String xml = "<test>" +
            "<subscriptionId>subscriptionId1</subscriptionId>" +
            "<clusterName>clusterName1</clusterName>" +
            "<appName>appName1</appName>" +
            "</test>";
        assertEquals(xml, root.asXML());
    }
}
