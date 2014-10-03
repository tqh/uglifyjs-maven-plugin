uglifyjs-maven-plugin
=====================

A maven plugin that runs UglifyJS on a directory of javascript files.

Based on the excellent https://github.com/kawasima/handlebars-maven-plugin

Introduction
------------

uglifyjs-maven-plugin is used to run UglifyJS on the javascript files of your project.

Goals
-----

Goal                 |Description
---------------------|-------------------------------
uglifyjs:uglify      |Compress javascript files

### uglifyjs:uglify

Full name
:net.tqh.plugins:uglifyjs-maven-plugin:1.0:uglify

Description
:uglify Run UglifyJS on files in sourceDirectory.

#### Optional parameters

Name             |Type    |Description
-----------------|--------|--------------------------------------
skip             |Boolean |Flag that allows user to skip execution of the plugin.
sources          |FileSet |The directory containing javascript source files.
outputDirectory  |String  |The output directory to put uglified files.
encoding         |String  |Charset of javascript files.

Repo
----

If you want to use this plugin in your maven project add the following plugin repository

    <pluginRepositories>
      <pluginRepository>
        <id>uglifyjs-maven-plugin</id>
        <url>https://raw.github.com/tqh/uglifyjs-maven-plugin/master/repo</url>
      </pluginRepository>
    </pluginRepositories>

and call the plugin during the build process, e.g.:

    <build>
        <plugins>
            <plugin>
                <groupId>net.tqh.plugins</groupId>
                <artifactId>uglifyjs-maven-plugin</artifactId>
                <version>1.0</version>
                <executions>
                    <execution>
                        <id>uglify-js</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>uglify</goal>
                        </goals>
                        <configuration>
                            <skip>false</skip>
                            <sources>
                                <directory>${project.build.directory}/classes/js</directory>
                                <excludes>
                                    <exclude>org/foo</exclude>
                                    <exclude>org/bar</exclude>
                                    <exclude>**/*.min.js</exclude>
                                </excludes>
                                <includes>
                                    <include>org/foo/lib</include>
                                </includes>
                            </sources>
                            <outputDirectory>${project.build.directory}/classes/js</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
