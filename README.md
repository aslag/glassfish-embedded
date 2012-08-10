glassfish-embedded
==================

A Gradle plugin for easy deployment of packaged WARs to the GlassFish web 
application server.

usage
=====

This plugin provides the task "glassFishRunWar" (or "gFRW").  Executing this 
task will create a WAR and deploy it in an embedded GlassFish instance running
on localhost, port 8080.  Output is written to the logfile 
<project_dir>/glassfish-embedded.log.

The easiest way to apply this plugin to Gradle projects is to use the published 
plugin jars in the TEHLULZ artifact repository at 
http://repo.tehlulz.com:8081/artifactory/libs-snapshot-local/.  To do so, add the
following lines to a Gradle build file:

    buildscript {
      repositories {
        maven {
         url 'http://repo.tehlulz.com:8081/artifactory/libs-release'
        }
        maven {
         url 'http://repo.tehlulz.com:8081/artifactory/libs-snapshot'
        }
      }

      dependencies {
        classpath group: 'com.tehlulz.gradle.plugins', name: 'tehlulz-glassfish-embedded', version: '0.2-SNAPSHOT'
      }
    }

    apply plugin: 'tehlulz-glassfish-embedded'


Alternatively, you can clone this project's source and build it with the
command "gradle build".  Install it to your local maven repository with
"gradle install".  Use the following lines in a Gradle build file to resolve
dependencies from a local Maven cache:

    buildscript {
      repositories {
        mavenLocal()
      }
    }

For more information, consult http://gradle.org/Documentation/.

acknowledgements
================

The structure of this code was duplicated from Gradle's own bundled Jetty
plugin: http://gradle.org/docs/current/userguide/jetty_plugin.html.


license
=======

All code and configuration files Copyright 2012 aslag.

This file is part of glassfish-embedded.

glassfish-embedded is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

glassfish-embedded is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with glassfish-embedded.  If not, see <http://www.gnu.org/licenses/>.
