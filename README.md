TeamCity Crowd Plugin
=================================
Binary versions of Plugin for a specific version of Crowd and TeamCity are fully available as a supported, Enterprise version. 

Link to the website to follow soon. 

Recent changes
--------------

Build status [![CircleCI](https://circleci.com/gh/greggigon/TeamCity-Crowd-Plugin/tree/master.svg?style=svg)](https://circleci.com/gh/greggigon/TeamCity-Crowd-Plugin/tree/master)

* Refactored out Java and Groovy into Kotlin.
* Latest versions of TeamCity (2018.1) and Crowd (3.2.1) used


Introduction
-------------

[TeamCity](http://www.jetbrains.com/teamcity/) Crowd integration plugin is used by TeamCity for user authentication.

[Plugin listed on Jetbrains page](https://plugins.jetbrains.com/plugin/9086-crowd-authentication).

The plugin replaces standard TeamCity Authentication mechanism by connecting to [Atlassian Crowd](https://www.atlassian.com/software/crowd/overview).
When user logs in with username and password, plugin validates credentials with Crowd. If the user "checks out" fine, the plugin creates user in TeamCity if the user doesn't exists.

Once the user is created Plugin will update user's group membership.
There are two modes in which Plugin can work (configured via plugin configuration file):
  - Plugin will only update membership of a user with groups that already exists in TeamCity (groups previously created in TeamCity). This means that user will only be added to groups that already exist in TeamCity (default functionality)
  - Plugin will create missing groups and update user membership (this functionality can be switched on via configuration file)

##### Note
Plugin uses different REALM to TeamCity. Once plugin activated the entire user base and groups prior to installation will __NOT__ be accessible.

Building the plugin
-------------------

To build the plugin you need Java installed on your machine and access to the Internet.
Plugin uses Maven 3 to build the project.

    $ mvn clean package

The build will run tests and prepare zip file in the __target/__ project folder.

You can modify a specific versions of TeamCity and Crowd you are building for, in the __pom.xml__ file (see __teamcity-version__ and __crowd.client.version__).

##### Note
First time the plugin is build it will download a distribution of TeamCity, it might take a while (~500MB).

### Versions

The plugin was extensively tested with `Teamcity 2018.1` and `Atlassian Crowd 3.2.1`. 

Installation
------------

To install TeamCity Crowd plugin you need to copy the plugin distribution file (build/dist/teamcity-crowd-plugin.zip) into __TeamCityDataDir/plugins__ folder.
__TeamCityDataDir__ is the server data folder. By default it is in the __User Home/.Buildserver__. More info at [JetBrains TeamCity WIKI](https://confluence.jetbrains.com/display/TCD18/TeamCity+Data+Directory).

You can also copy presets file __crowd.xml__ from [this repository](https://raw2.github.com/greggigon/TeamCity-Crowd-Plugin/master/crowd.xml) and place it in the __TeamCityDataDir/config/_auth__ folder.
It is Authentication preset file that will make Crowd Authentication selectable from the Administrator view in TeamCity.

If you don't copy the file you need to manually modify __auth-config.xml__ in  __TeamCityDataDir/config__  folder.
Sample configuration [auth-config.xml](https://raw2.github.com/greggigon/TeamCity-Crowd-Plugin/master/auth-config.xml) can be found in this repository.

For the plugin to work you need to create or copy Plugin Configuration file called __teamcity-crowd-plugin.properties__ and place it in the __TeamCityDataDir/config__. It has to be the exact name.
Sample configuration file can be found [in this repository](https://raw2.github.com/greggigon/TeamCity-Crowd-Plugin/master/teamcity-crowd-plugin.properties).

##### Note
Plugin uses different REALM to TeamCity. The entire user base created prior to plugin activation will not be visible/usable.
One plugin activated you can log into TeamCity with your Crowd credentials. To see the administration panel you will have to log in as SUPER USER into TeamCity with TOKEN from the teamcity-server.log file and make your user administrator (via checkbox on user page).

__Remember to restart TeamCity for plugin to work.__

Configuration
-------------

If you have the presets file copied to TeamCity configuration folder, you can select the Authentication mechanism in the Administration -> Authentication panel of TeamCity.
You can manually enable the plugin by modifying __auth-config.xml__ file.

You also need to configure Crowd Server details in the __teamcity-crowd-plugin.properties__ file. Example configuration:

```
    application.name=teamcity
    application.password=password
    application.login.url=http://localhost:8111/login.html

    crowd.base.url=http://localhost:8095/crowd

    session.isauthenticated=session.isauthenticated
    session.tokenkey=whateva
    session.validationinterval=0
    session.lastvalidation=session.lastvalidation
```

!Note!
Make sure to add Application to the Crowd server with appropriate name and password.

#### Extra config parameter for group creation

There is one additional property that you can setup in the __teamcity-crowd-plugin.properties__ file.

```
    tc.crowd.plugin.createGroups=true # default false
```

If you set this up the Plugin will create Groups in TeamCity when user logs in and Group is not there.
It is switched off by default. Creation of a group requires the creation of a Group Key in TeamCity is limited to 16 characters. If multiple groups are created with the same 16 character prefix then and id is generated by appending an incrementing 3 digit number to the end of the first 13 chars of the group name.
Creating Groups manually gives more control over Project Permissions, etc.