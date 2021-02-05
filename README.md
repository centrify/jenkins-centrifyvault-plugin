# Jenkins Centrify Vault Plugin

[![Build Status](https://ci.jenkins.io/job/Plugins/job/centrify-vault-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/centrify-vault-plugin/job/master/)
[![Contributors](https://img.shields.io/github/contributors/jenkinsci/centrify-vault-plugin.svg)](https://github.com/jenkinsci/centrify-vault-plugin/graphs/contributors)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/centrify-vault.svg)](https://plugins.jenkins.io/centrify-vault)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/centrify-vault-plugin.svg?label=changelog)](https://github.com/jenkinsci/centrify-vault-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/centrify-vault.svg?color=blue)](https://plugins.jenkins.io/centrify-vault)

## Introduction

This plugin extends Jenkins Credentials Plugin to provide credentials stored in Centrify Vault to Jenkins jobs. It injects retrieved credentails from Centrify Vault into build freestyle and pipeline project.

## Getting started

The plugin is packaged in self-contained **.hpi** file which can be installed via Jenkins web UI or CLI.
Refer to [Managing Plugins](https://www.jenkins.io/doc/book/managing/plugins/) for details of how to intall the plugin.

## Configuration

Before the plugin can be used, it must be configured to connect to Centrify Vault. Currently, configuration is done at global level at **Manage Jenkins > Configure System**.

* **Tenant URL** - Centrify tenant or on-prem PAS URL.
* **OAuth App ID** - OAuth application ID configured in Centrify Vault web app.
* **OAuth Scope** - OAuth scope configured in Centrify Vault web app.
* **Client Credentials** - Select a username/password credential that is used to authenticate against Centrify Vault.
* **Enable Debugging** - Turn on debug logging.

![Vault Configuration](/images/vault_configuration.png)

## Plugin Usage


## Contributing

TODO review the default [CONTRIBUTING](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md) file and make sure it is appropriate for your plugin, if not then add your own one adapted from the base file

Refer to our [contribution guidelines](https://github.com/jenkinsci/.github/blob/master/CONTRIBUTING.md)

## LICENSE

Licensed under MIT, see [LICENSE](LICENSE.md)

