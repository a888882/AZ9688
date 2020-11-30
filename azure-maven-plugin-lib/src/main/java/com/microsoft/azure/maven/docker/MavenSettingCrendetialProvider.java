package com.microsoft.azure.maven.docker;

import com.microsoft.azure.common.docker.IDockerCrendetialProvider;

import org.apache.maven.settings.Settings;

public class MavenSettingCrendetialProvider implements IDockerCrendetialProvider {
    private Settings settings;
    private String serverId;

    public MavenSettingCrendetialProvider(Settings settings, String serverId) {
        this.settings = settings;
        this.serverId = serverId;
    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }
}
