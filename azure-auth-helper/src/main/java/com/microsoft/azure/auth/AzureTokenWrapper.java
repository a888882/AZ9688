/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.auth;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.auth.configuration.AuthMethod;
import com.microsoft.azure.credentials.AzureTokenCredentials;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.ArrayUtils;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AzureTokenWrapper extends AzureTokenCredentials {

    private static final String TEMPLATE = "Auth Type : %s";
    private static final String TEMPLATE_WITH_FILE = "Auth Type : %s, Auth Files : [%s]";

    private AuthMethod authMethod;
    private File[] authFileLocation;
    private AzureTokenCredentials azureTokenCredentials;

    public AzureTokenWrapper(AuthMethod authMethod, AzureTokenCredentials credentials) {
        super(credentials.environment(), credentials.domain());
        this.authMethod = authMethod;
        this.azureTokenCredentials = credentials;
    }

    public AzureTokenWrapper(AuthMethod authMethod, AzureTokenCredentials credentials, File... authFileLocation) {
        this(authMethod, credentials);
        this.authFileLocation = authFileLocation;
    }

    public AuthMethod getAuthMethod() {
        return authMethod;
    }

    public AzureTokenCredentials getAzureTokenCredentials() {
        return azureTokenCredentials;
    }

    public File[] getAuthFileLocation() {
        return authFileLocation;
    }

    public String getCredentialDescription() {
        if (ArrayUtils.isEmpty(authFileLocation)) {
            return String.format(TEMPLATE, authMethod.getAuthType());
        }
        final String authFiles = Arrays.stream(authFileLocation).map(file -> file.getAbsolutePath()).collect(Collectors.joining(", "));
        return String.format(TEMPLATE_WITH_FILE, authMethod.getAuthType(), authFiles);
    }

    @Override
    public String getToken(String s) throws IOException {
        return azureTokenCredentials.getToken(s);
    }

    @Override
    public String domain() {
        return azureTokenCredentials.domain();
    }

    @Override
    public AzureEnvironment environment() {
        return azureTokenCredentials.environment();
    }

    @Override
    public String defaultSubscriptionId() {
        return azureTokenCredentials.defaultSubscriptionId();
    }

    @Override
    public AzureTokenCredentials withDefaultSubscriptionId(String subscriptionId) {
        return azureTokenCredentials.withDefaultSubscriptionId(subscriptionId);
    }

    @Override
    public Proxy proxy() {
        return azureTokenCredentials.proxy();
    }

    @Override
    public SSLSocketFactory sslSocketFactory() {
        return azureTokenCredentials.sslSocketFactory();
    }

    @Override
    public AzureTokenCredentials withProxy(Proxy proxy) {
        return azureTokenCredentials.withProxy(proxy);
    }

    @Override
    public AzureTokenCredentials withSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        return azureTokenCredentials.withSslSocketFactory(sslSocketFactory);
    }

    @Override
    public void applyCredentialsFilter(OkHttpClient.Builder clientBuilder) {
        azureTokenCredentials.applyCredentialsFilter(clientBuilder);
    }
}
