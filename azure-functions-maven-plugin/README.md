# Maven Plugin for Azure Functions
[![Maven Central](https://img.shields.io/maven-central/v/com.microsoft.azure/azure-functions-maven-plugin.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.microsoft.azure%22%20AND%20a%3A%22azure-functions-maven-plugin%22)

#### Table of Content
- [Prerequisites](#prerequisites)
- [Goals](#goals)
- [Usage](#usage)
- [Common Configuration](#common-configuration)
- [Configuration](#configuration)
    - [Supported Regions](#supported-regions)
    - [Supported Pricing Tiers](#supported-pricing-tiers)
- [How To](#how-to)
    - [Add new function to current project](#add-new-function-to-current-project)
    - [Generate `function.json` from current project](#generate-functionjson-from-current-project)
    - [Run Azure Functions locally](#run-azure-functions-locally)
        - [Run all Azure Functions](#run-all-azure-functions)
        - [Run a single Azure Function](#run-a-single-azure-function)
    - [Deploy Azure Functions to Azure](#deploy-azure-functions-to-azure)

## Prerequisites

Tool | Required Version
---|---
JDK | 1.8 and above
Maven | 3.0 and above
[.Net Core SDK](https://www.microsoft.com/net/core) | Latest version
[Azure Functions Core Tools](https://www.npmjs.com/package/azure-functions-core-tools) | 2.0 and above

## Goals

#### `azure-functions:package`
- Scan the output directory (default is `${project.basedir}/target/classes`) and generating `function.json` for each function (method annotated with `FunctionName`) in the staging directory.
- Copy JAR files from the build directory (default is `${project.basedir}/target/`) to the staging directory.

>NOTE:
>Default staging directory is `${project.basedir}/target/azure-functions/${function-app-name}/`

#### `azure-functions:add`
- Create new Java function and add to current project.
- You will be prompted to choose template and enter parameters. Templates for below triggers are supported as of now:
    - HTTP Trigger
    - Azure Storage Blob Trigger
    - Azure Storage Queue Trigger
    - Timer Trigger

#### `azure-functions:run`
- Invoke Azure Functions Local Emulator to run all functions. Default working directory is the staging directory.
- Use property `-Dfunctions.target=myFunction` to run a single function named `myFunction`

#### `azure-functions:deploy` 
- Deploy the staging directory to target Function App.
- If target Function App does not exist already, it will be created.
 

## Usage

To use the Maven Plugin for Azure Functions in your Maven Java app, add the following snippet to your `pom.xml` file:

```xml
<project>
  ...
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <groupId>com.microsoft.azure</groupId>
          <artifactId>azure-functions-maven-plugin</artifactId>
          <version>0.1.5</version>
          <configuration>
            ...
          </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```

Read [How-To](#how-to) section to learn detailed usages.

## Common Configuration

This Maven plugin supports common configurations of all Maven Plugins for Azure.
Detailed documentation of common configurations is at [here](../docs/common-configuration.md).

## Configurations

This Maven Plugin supports the following configuration properties:

Property | Required | Description
---|---|---
`<resourceGroup>` | true | Specifies the Azure Resource Group for your Function App.
`<appName>` | true | Specifies the name of your Function App.
`<region>`* | false | Specifies the region where your Function App will be hosted; default value is **westus**. All valid regions are at [Supported Regions](#supported-regions) section.
`<pricingTier>`* | false | Specifies the pricing tier for your Function App; default value is **Consumption**. All valid pricing tiers are at [Supported Pricing Tiers](#supported-pricing-tiers) section.
`<appSettings>` | false | Specifies the application settings for your Function App, which are defined in name-value pairs like following example:<br>`<property>`<br>&nbsp;&nbsp;&nbsp;&nbsp;`<name>xxxx</name>`<br>&nbsp;&nbsp;&nbsp;&nbsp;`<value>xxxx</value>`<br>`</property>`
`<deploymentType>` | false | Specifies the deployment approach you want to use.<br>Supported values are `msdeploy` and `ftp`. Default value is **`msdeploy`**.
>*: This setting will be used to create a new Function App if specified Function App does not exist; if target Function App already exists, this setting will be ignored.

### Supported Regions
All valid regions are listed as below. Read more at [Azure Region Availability](https://azure.microsoft.com/en-us/regions/services/).
- `westus`
- `westus2`
- `eastus`
- `eastus2`
- `northcentralus`
- `southcentralus`
- `westcentralus`
- `canadacentral`
- `canadaeast`
- `brazilsouth`
- `northeurope`
- `westeurope`
- `uksouth`
- `eastasia`
- `southeastasia`
- `japaneast`
- `japanwest`
- `australiaeast`
- `australiasoutheast`
- `centralindia`
- `southindia`

### Supported Pricing Tiers
Consumption plan is the default if you don't specify anything for your Azure Functions.
You can also run Functions within your App Service Plan. All valid App Service plan pricing tiers are listed as below.
Read more at [Azure App Service Plan Pricing](https://azure.microsoft.com/en-us/pricing/details/app-service/).
- `F1`
- `D1`
- `B1`
- `B2`
- `B3`
- `S1`
- `S2`
- `S3`
- `P1`
- `P2`
- `P3`

## How-To

### Add new function to current project
Run below command to create a new function:
- In package `com.your.package`
- Named `NewFunction`
- Bound to `HttpTrigger`

```cmd
mvn azure-functions:add -Dfunctions.package=com.your.package -Dfunctions.name=NewFunction -Dfunctions.template=HttpTrigger
```

You don't have to provide all properties on command line. Missing properties will be prompted for input during the execution of the goal.

### Generate `function.json` from current project

Follow below instructions, you don't need to handwrite `function.json` any more.
1. Use annotations from package `com.microsoft.azure:azure-functions-java-core` to decorate your functions. 
2. Run `mvn clean package azure-functions:package`; then `function.json` files will be automatically generated for all functions in your project.

### Run Azure Functions locally

With the help of goal `azure-functions:run`, you can run your Azure Functions locally.
>Note:
>Before you can run Azure Functions locally, install [.Net Core SDK](https://www.microsoft.com/net/core) and 
[Azure Functions Core Tools](https://www.npmjs.com/package/azure-functions-core-tools) first.

#### Run all Azure Functions

Run all Azure Functions in current project with below command.

```cmd
mvn azure-functions:run
```

#### Run a single Azure Function

You can also run a single Azure Function if you specify the target function on command line as below examples.

- `mvn azure-functions:run -Dfunctions.target=HttpTrigger1 -Dfunctions.input=inputString`

    Invoke function `HttpTrigger1` with input `inputString` from command line.

- `mvn azure-functions:run -Dfunctions.target=HttpTrigger1 -Dfunctions.inputFile=C:\input.json`

    Invoke function `HttpTrigger1` with input from file `C:\input.json`.

### Deploy Azure Functions to Azure

Directly deploy to target Function App by running `mvn azure-functions:deploy`.

Supported deployment methods are listed as below. Default value is **MSDeploy**.
- MSDeploy
- FTP
