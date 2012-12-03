uglifyjs-maven-plugin
=====================

A maven plugin that runs uglifyjs on a directory of javascript files.

Based on the excellent https://github.com/kawasima/handlebars-maven-plugin

Introduction
------------

uglifyjs-maven-plugin is used to run uglify on the javascript files of your project.

Goals
-----

Goal                 |Description
---------------------|-------------------------------
uglifyjs:uglify      |Compress javascript files

### uglifyjs:uglify

Full name
:net.tqh.plugins:uglifyjs-maven-plugin:1.0:uglify

Description
:uglify Run uglifyJS on files in sourceDirectory.

#### Optional parameters

Name             |Type   |Description
-----------------|-------|--------------------------------------
sourceDirectory  |String |The directory of javascript files.
outputDirectory  |String |The output directory to put uglified files
encoding         |String |charset of javascript files.

Repo
----

If you want to use this plugin in your maven project add
    <pluginRepositories>
      <pluginRepository>
        <id>uglifyjs-maven-plugin</id>
        <url>https://raw.github.com/tqh/uglifyjs-maven-plugin/master/repo</url>
      </pluginRepository>
    </pluginRepositories>
