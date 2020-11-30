/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.function.utils;

import com.microsoft.azure.maven.function.configurations.FunctionExtensionVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.Arrays;


public class FunctionUtils {

    private static final String INVALID_FUNCTION_EXTENSION_VERSION = "FUNCTIONS_EXTENSION_VERSION is empty or invalid, " +
            "please check the configuration";

    public static FunctionExtensionVersion parseFunctionExtensionVersion(String version) throws MojoExecutionException {
        return Arrays.stream(FunctionExtensionVersion.values())
                .filter(versionEnum -> StringUtils.equalsAnyIgnoreCase(versionEnum.getVersion(), version))
                .findFirst()
                .orElseThrow(() -> new MojoExecutionException(INVALID_FUNCTION_EXTENSION_VERSION));
    }
}
