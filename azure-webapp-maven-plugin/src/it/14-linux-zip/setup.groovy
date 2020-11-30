/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

import com.microsoft.azure.maven.webapp.utils.TestUtils

TestUtils.azureLogin()

TestUtils.deleteAzureResourceGroup("maven-webapp-it-rg-14", true)

return true