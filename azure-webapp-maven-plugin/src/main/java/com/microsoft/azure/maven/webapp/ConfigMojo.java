/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.maven.webapp;

import com.microsoft.azure.management.appservice.JavaVersion;
import com.microsoft.azure.management.appservice.WebContainer;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.maven.appservice.PricingTierEnum;
import com.microsoft.azure.maven.queryer.MavenPluginQueryer;
import com.microsoft.azure.maven.queryer.QueryFactory;
import com.microsoft.azure.maven.webapp.configuration.Deployment;
import com.microsoft.azure.maven.webapp.configuration.DeploymentSlotSetting;
import com.microsoft.azure.maven.webapp.configuration.OperatingSystemEnum;
import com.microsoft.azure.maven.webapp.configuration.RuntimeSetting;
import com.microsoft.azure.maven.webapp.configuration.SchemaVersion;
import com.microsoft.azure.maven.webapp.handlers.WebAppPomHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mojo(name = "config")
public class ConfigMojo extends AbstractWebAppMojo {

    public static final String NOT_EMPTY_REGEX = "[\\s\\S]+";
    public static final String BOOLEAN_REGEX = "[YyNn]";

    public static final String CONFIG_ONLY_SUPPORT_V2 = "Config only support V2 schema";
    public static final String CHANGE_OS_WARNING = "The plugin may not work if you change the os of an existing " +
        "webapp.";
    public static final String CONFIGURATION_NO_RUNTIME = "No runtime configuration, skip it.";
    public static final String SAVING_TO_POM = "Saving configuration to pom.";

    private MavenPluginQueryer queryer;
    private WebAppPomHandler pomHandler;
    private static final String[] configTypes = {"Application", "Runtime", "DeploymentSlot"};

    @Override
    protected void doExecute() throws Exception {
        queryer = QueryFactory.getQueryer(settings, getLog());
        pomHandler = new WebAppPomHandler("pom.xml");

        try {
            final WebAppConfiguration configuration = pomHandler.getConfiguration() == null ? null :
                getWebAppConfiguration();
            if (isV1Configuration(configuration)) {
                warning(CONFIG_ONLY_SUPPORT_V2);
            } else {
                config(configuration);
            }
        } finally {
            queryer.close();
        }
    }

    private boolean isV1Configuration(WebAppConfiguration configuration) {
        return configuration != null && configuration.getSchemaVersion().equals(SchemaVersion.V1.toString());
    }

    protected void config(WebAppConfiguration configuration) throws MojoFailureException, MojoExecutionException,
        IOException {
        final WebAppConfiguration oldConfiguration = configuration;
        do {
            if (configuration == null) {
                configuration = initConfig();
            } else {
                configuration = updateConfiguration(configuration);
            }
        } while (!confirmConfiguration(configuration));
        info(SAVING_TO_POM);
        pomHandler.updatePluginConfiguration(configuration, oldConfiguration);
    }

    protected boolean confirmConfiguration(WebAppConfiguration configuration) throws MojoExecutionException,
        MojoFailureException {
        System.out.println("Please confirm webapp properties");
        System.out.println("AppName : " + configuration.getAppName());
        System.out.println("ResourceGroup : " + configuration.getResourceGroup());
        System.out.println("Region : " + configuration.getRegion());
        System.out.println("PricingTier : " + configuration.getPricingTier());

        if (configuration.getOs() == null) {
            System.out.println(CONFIGURATION_NO_RUNTIME);
        } else {
            System.out.println("OS : " + configuration.getOs().toString());
            switch (configuration.getOs()) {
                case Windows:
                    System.out.println("Java : " + configuration.getJavaVersion());
                    System.out.println("WebContainer : " + configuration.getWebContainer());
                    break;
                case Linux:
                    System.out.println("RuntimeStack : " + configuration.getRuntimeStack());
                    break;
                case Docker:
                    System.out.println("Image : " + configuration.getImage());
                    System.out.println("ServerId : " + configuration.getServerId());
                    System.out.println("RegistryUrl : " + configuration.getRegistryUrl());
                    break;
                default:
                    throw new MojoExecutionException("The value of <os> is unknown.");
            }
        }
        System.out.println("Deploy to slot : " + (configuration.getDeploymentSlotSetting() != null));
        if (configuration.getDeploymentSlotSetting() != null) {
            final DeploymentSlotSetting slotSetting = configuration.getDeploymentSlotSetting();
            System.out.println("Slot name : " + slotSetting.getName());
            System.out.println("ConfigurationSource : " + slotSetting.getConfigurationSource());
        }
        final String result = queryer.assureInputFromUser("confirm", "Y", BOOLEAN_REGEX, "Confirm (Y/N)? : ", null);
        return result.equalsIgnoreCase("Y");
    }

    protected WebAppConfiguration initConfig() throws MojoFailureException, MojoExecutionException {
        final WebAppConfiguration result = getDefaultConfiguration();
        return getRuntimeConfiguration(result);
    }

    private WebAppConfiguration getDefaultConfiguration() throws MojoExecutionException {
        final WebAppConfiguration.Builder builder = new WebAppConfiguration.Builder();
        final String defaultName = getProject().getArtifactId() + "-" + System.currentTimeMillis();
        final String resourceGroup = defaultName + "-rg";
        final String defaultSchemaVersion = "V2";
        final Region defaultRegion = WebAppConfiguration.DEFAULT_REGION;
        final PricingTierEnum pricingTierEnum = WebAppConfiguration.DEFAULT_PRICINGTIER;
        return builder.appName(defaultName)
            .resourceGroup(resourceGroup)
            .region(defaultRegion)
            .pricingTier(pricingTierEnum.toPricingTier())
            .resources(Deployment.getDefaultDeploymentConfiguration(getProject().getPackaging()).getResources())
            .schemaVersion(defaultSchemaVersion)
            .build();
    }

    protected WebAppConfiguration updateConfiguration(WebAppConfiguration configuration)
        throws MojoFailureException, MojoExecutionException {
        final String selection = queryer.assureInputFromUser("selection", null, Arrays.asList(configTypes),
            "Please choose which part to config");
        switch (selection) {
            case "Application":
                return getWebAppConfiguration(configuration);
            case "Runtime":
                return getRuntimeConfiguration(configuration);
            case "DeploymentSlot":
                return getSlotConfiguration(configuration);
            default:
                throw new MojoExecutionException("Unknow webapp setting");
        }
    }

    private WebAppConfiguration getWebAppConfiguration(WebAppConfiguration configuration)
        throws MojoFailureException, MojoExecutionException {
        final WebAppConfiguration.Builder builder = configuration.getBuilderFromConfiguration();

        final String defaultAppName = getDefaultValue(configuration.appName, getProject().getArtifactId());
        final String appName = queryer.assureInputFromUser("appName", defaultAppName,
            NOT_EMPTY_REGEX, null, null);

        final String defaultResourceGroup = getDefaultValue(configuration.resourceGroup,
            String.format("%s-rg", appName));
        final String resourceGroup = queryer.assureInputFromUser("resourceGroup",
            defaultResourceGroup,
            NOT_EMPTY_REGEX, null, null);

        final String defaultRegion = configuration.getRegionOrDefault();
        final String region = queryer.assureInputFromUser("region", defaultRegion, NOT_EMPTY_REGEX,
            null, null);

        final String defaultPricingTier = configuration.getPricingTierOrDefault();
        final String pricingTier = queryer.assureInputFromUser("pricingTier", defaultPricingTier,
            getAvailablePricingTierList(), null);

        return builder.appName(appName)
            .resourceGroup(resourceGroup)
            .region(Region.fromName(region))
            .pricingTier(PricingTierEnum.valueOf(pricingTier).toPricingTier())
            .build();
    }

    private WebAppConfiguration getSlotConfiguration(WebAppConfiguration configuration)
        throws MojoFailureException {
        final WebAppConfiguration.Builder builder = configuration.getBuilderFromConfiguration();

        final DeploymentSlotSetting deploymentSlotSetting = configuration.getDeploymentSlotSetting();
        final String defaultIsSlotDeploy = deploymentSlotSetting == null ? "N" : "Y";
        final String isSlotDeploy = queryer.assureInputFromUser("isSlotDeploy", defaultIsSlotDeploy, BOOLEAN_REGEX,
            "Deploy to slot?(Y/N): ", null);
        if (isSlotDeploy.toLowerCase().equals("n")) {
            return builder.deploymentSlotSetting(null).build();
        }

        final String defaultSlotName = deploymentSlotSetting == null ? String.format("%s-slot",
            configuration.getAppName()) : deploymentSlotSetting.getName();
        final String slotName = queryer.assureInputFromUser("slotName", defaultSlotName, NOT_EMPTY_REGEX,
            null, null);

        final String defaultConfigurationSource = deploymentSlotSetting == null ? null :
            deploymentSlotSetting.getConfigurationSource();
        final String configurationSource = queryer.assureInputFromUser("configurationSource",
            defaultConfigurationSource, null, null, null);

        final DeploymentSlotSetting result = new DeploymentSlotSetting();
        result.setName(slotName);
        result.setConfigurationSource(configurationSource);
        return builder.deploymentSlotSetting(result).build();
    }

    private WebAppConfiguration getRuntimeConfiguration(WebAppConfiguration configuration)
        throws MojoFailureException, MojoExecutionException {
        WebAppConfiguration.Builder builder = configuration.getBuilderFromConfiguration();
        warning(CHANGE_OS_WARNING);
        final OperatingSystemEnum defaultOs = configuration.getOs() == null ? OperatingSystemEnum.Linux :
            configuration.getOs();
        final String os = queryer.assureInputFromUser("OS", defaultOs, null);
        builder.os(OperatingSystemEnum.fromString(os));

        switch (os.toLowerCase()) {
            case "linux":
                builder = getRuntimeConfigurationOfLinux(builder, configuration);
                break;
            case "windows":
                builder = getRuntimeConfigurationOfWindows(builder, configuration);
                break;
            case "docker":
                builder = getRuntimeConfigurationOfDocker(builder, configuration);
                break;
            default:
                throw new MojoExecutionException("The value of <os> is unknown.");
        }
        return builder.build();
    }

    private WebAppConfiguration.Builder getRuntimeConfigurationOfLinux(WebAppConfiguration.Builder builder,
                                                                       WebAppConfiguration configuration)
        throws MojoFailureException {
        final String defaultLinuxRuntimeStack = configuration.getRuntimeStackOrDefault();
        final String runtimeStack = queryer.assureInputFromUser("runtimeStack",
            defaultLinuxRuntimeStack, RuntimeSetting.getValidLinuxRuntime(), null);
        return builder.runtimeStack(RuntimeSetting.getLinuxRuntimeStackByJavaVersion(runtimeStack));
    }

    private WebAppConfiguration.Builder getRuntimeConfigurationOfWindows(WebAppConfiguration.Builder builder,
                                                                         WebAppConfiguration configuration)
        throws MojoFailureException {
        final String defaultJavaVersion = configuration.getJavaVersionOrDefault();
        final String javaVersion = queryer.assureInputFromUser("javaVersion",
            defaultJavaVersion, getAvailableJavaVersion(), null);

        final String defaultWebContainer = configuration.getWebContainerOrDefault();
        final String webContainer = queryer.assureInputFromUser("webContainer",
            defaultWebContainer, getAvailableWebContainer(), null);
        return builder.javaVersion(JavaVersion.fromString(javaVersion))
            .webContainer(WebContainer.fromString(webContainer));
    }

    private WebAppConfiguration.Builder getRuntimeConfigurationOfDocker(WebAppConfiguration.Builder builder,
                                                                        WebAppConfiguration configuration)
        throws MojoFailureException {
        final String image = queryer.assureInputFromUser("image", configuration.image,
            NOT_EMPTY_REGEX, null, null);
        final String serverId = queryer.assureInputFromUser("serverId", configuration.serverId,
            null, null, null);
        final String registryUrl = queryer.assureInputFromUser("registryUrl", configuration.registryUrl,
            null, null, null);
        return builder.image(image)
            .serverId(serverId)
            .registryUrl(registryUrl);
    }

    private static List<String> getAvailableJavaVersion() {
        final List<String> result = new ArrayList<>();
        for (final JavaVersion javaVersion : JavaVersion.values()) {
            if (!javaVersion.toString().equals("null")) {
                result.add(javaVersion.toString());
            }
        }
        Collections.sort(result);
        return result;
    }

    private static List<String> getAvailableWebContainer() {
        final List<String> result = new ArrayList<>();
        for (final WebContainer webContainer : WebContainer.values()) {
            result.add(webContainer.toString());
        }
        Collections.sort(result);
        return result;
    }

    private static List<String> getAvailablePricingTierList() {
        final Set<String> pricingTierSet = new HashSet<>();
        for (final PricingTierEnum pricingTierEnum : PricingTierEnum.values()) {
            pricingTierSet.add(pricingTierEnum.toString().toLowerCase());
        }
        final List<String> result = new ArrayList<>(pricingTierSet);
        Collections.sort(result);
        return result;
    }

    private String getDefaultValue(String defaultValue, String fallBack) {
        return StringUtils.isNotEmpty(defaultValue) ? defaultValue : fallBack;
    }
}
