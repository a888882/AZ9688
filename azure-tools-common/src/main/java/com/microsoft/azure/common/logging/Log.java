/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.common.logging;

public final class Log {
    private static ILogger logger;

    public static void configureDefaultStdLog() {
        logger = new DefaultLogger();
    }

    public static void error(String message, Exception ex) {
        logger.error(message, ex);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String message, Exception ex) {
        logger.info(message, ex);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void debug(String message, Exception ex) {
        logger.debug(message, ex);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void warn(String message, Exception ex) {
        logger.warn(message, ex);
    }

}
