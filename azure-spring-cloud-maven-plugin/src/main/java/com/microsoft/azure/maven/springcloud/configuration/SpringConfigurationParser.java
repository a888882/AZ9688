/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.springcloud.configuration;

import com.microsoft.azure.maven.springcloud.AbstractSpringMojo;
import com.microsoft.azure.springcloud.SpringCloudAppConfig;

public interface SpringConfigurationParser {
    SpringCloudAppConfig parse(AbstractSpringMojo springMojo);
}
