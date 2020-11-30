/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.webapp.deploytarget;

import com.microsoft.azure.management.appservice.DeploymentSlot;
import com.microsoft.azure.maven.appservice.DeployTargetType;
import com.microsoft.azure.maven.deploytarget.DeployTarget;

import java.io.File;

public class DeploymentSlotDeployTarget extends DeployTarget<DeploymentSlot> {
    public DeploymentSlotDeployTarget(final DeploymentSlot slot) {
        super(slot, DeployTargetType.SLOT);
    }

    public void warDeploy(final File war, final String path) {
        app.warDeploy(war, path);
    }
}
