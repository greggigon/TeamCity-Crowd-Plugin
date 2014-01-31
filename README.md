TeamCity Crowd Plugin
=================================

Introduction
-------------

TeamCity Crowd integration plugin is used by TeamCity for user authentication.

The plugin replaces standard TeamCity Authentication mechanism by talking to Atlassian Crowd.
When user logs in with username and password, plugin validates credentials with Crowd. If the user "checks out" fine, the plugin creates user in TeamCity if the user doesn't exists.

Once the user is created Plugin will update user's group membership.
There are two modes in which Plugin can work (configured via plugin configuration file):
  - Plugin will only update membership of a user with groups that already exists in TeamCity. Previously created. (default functionality)
  - Plugin will create missing groups and update user membership (set via configuration file, not default)

__Note__
Realm!

Building the plugin
-------------------

To build the plugin you need Java installed on your machine and access to the Internet.
Plugin uses Gradle to build the project.

    gradlew buildPlugin

The build will run tests and prepare zip file in the __build/dist__ project folder.

__Note__
First time the plugin is build it will download a distribution of TeamCity, it might take a while (~300MB).

If you have a distribution of TeamCity deployed somewhere you might copy it to project directory __teamcitydist/__ folder or modify __build.gradle__ file.

You can also change the versions of TeamCity distribution and Crowd libraries in the __build.gradle__ file.

Installation
------------

To install TeamCity Crowd plugin you need to copy the pugin distribution file (build/dist/teamcity-crowd-plugin-(version).zip) into __TeamCityDataDir/plugins__ folder.
__TeamCityDataDir__ is the server data folder. By default it is in the __User Home/.Buildserver__ .

You will also need to copy the