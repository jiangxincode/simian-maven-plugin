# Simian Maven Plugin

`simian-maven-plugin` is a maven plugin for `simian`. You can visit <http://www.harukizaemon.com/simian> for further information of `simian`.

There is a `maven-simian-plugin`(http://maven.apache.org/archives/maven-1.x/plugins/simian), however it can be used in maven 1.x only, so I write the `simian-maven-plugin` for maven 2+.

This project is not fully finished, but spring is coming. 

## How to Use

`maven-simian-plugin` has transparent dependency of `simian`, but `simian` is not a free software. Therefore there is not a package in maven center. 

1. Download the package in <http://www.harukizaemon.com/simian/index.html>, warn the license of `simian` and unzip the package.

2. Put the jar to your local maven repository with below command:

```
mvn install:install-file -DgroupId="com.harukizaemon.simian" -DartifactId=”simian” -Dversion="2.5.10" -Dpackaging=”jar” -Dfile="${PATH_OF_simian-2.5.10.jar}"
```

3. Add the below content to your `<project><reporting><plugins>` node of pom.xml. You can find latest `${simian-maven-plugin-version}` from <https://search.maven.org/>

```xml
    <plugin>
        <groupId>com.github.jiangxincode</groupId>
        <artifactId>simian-maven-plugin</artifactId>
        <version>${simian-maven-plugin-version}</version>
    </plugin>
```

4. Run `mvn clean site`

You can see the example from my another project:
<https://jiangxincode.github.io/ApkToolBoxGUI/simian-report.html>

## License

* Apache License V2.0 http://www.apache.org/licenses/LICENSE-2.0

*WARN*

`simian-maven-plugin` is a completely free and open-source project but `simian` is not. So you must read the license of it: <http://www.harukizaemon.com/simian/license.pdf>