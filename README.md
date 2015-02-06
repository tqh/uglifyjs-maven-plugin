uglifyjs-cleancss-maven-plugin
=====================

A maven plugin that runs UglifyJS and CleanCss on a directory of javascript files.

UglifyJs and CleanCss should be installed with npm. Also added to env commands "uglifyjs" and "cleancss"

Based on the excellent https://github.com/kawasima/handlebars-maven-plugin
and on https://github.com/tqh/uglifyjs-cleancss-maven-plugin

Introduction
------------

uglifyjs-cleancss-maven-plugin is used to run UglifyJS on the javascript files of your project.

Goals
-----

Goal                 |Description
---------------------|-------------------------------
uglifyjs:uglify      |Compress javascript files

### uglifyjs:uglify

Full name
:net.pudovika.plugins:uglifyjs-cleancss-maven-plugin:1.0:uglify

Description
:uglify Run UglifyJS on files in sourceDirectory. If set cssSource clearcss would run in cssSourceDirectory.  

#### Optional parameters

Name             |Type    |Description
-----------------|--------|--------------------------------------
skip             |Boolean |Flag that allows user to skip execution of the plugin. (Currently only available when building this plugin from source.)
sources          |FileSet |The directory containing javascript source files.
cssSources       |FileSet |The directory containing css source files.
outputDirectory  |String  |The output directory to put uglified files.
encoding         |String  |Charset of javascript files.

Repo
----

If you want to use this plugin in your maven project add the following plugin repository

    <pluginRepositories>
      <pluginRepository>
        <id>uglifyjs-cleancss-maven-plugin</id>
        <url>https://raw.github.com/pudovika/uglifyjs-cleancss-maven-plugin/master/repo</url>
      </pluginRepository>
    </pluginRepositories>

and call the plugin during the build process, e.g.:

    <build>
        <plugins>
            <plugin>
                <groupId>net.pudovika.plugins</groupId>
                <artifactId>uglifyjs-cleancss-maven-plugin</artifactId>
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
                            <cssSources>
                                <directory>${basedir}/assets</directory>
                                <includes>
                                   <include>org/foo/*.css</include>
                                </includes>
                            </cssSources>
                            <outputDirectory>${project.build.directory}/classes/js</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
