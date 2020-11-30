/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.springcloud.configuration;

public enum SpringConfigurationParserFactory {

    INSTANCE;

    public SpringConfigurationParser getConfigurationParser() {
        return new SpringConfigurationParserImpl();
    }
}
