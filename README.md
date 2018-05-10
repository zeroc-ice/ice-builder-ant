[![Build Status](https://travis-ci.org/zeroc-ice/ice-builder-ant.svg)](https://travis-ci.org/zeroc-ice/ice-builder-ant)

# Ice Builder for Ant

The Ice Builder for Ant provides two ant tasks, `slice2java` and `slice2jfreezej`, that automate the execution of the [Slice-to-Java](https://doc.zeroc.com/display/Ice/slice2java+Command-Line+Options) and [Slice-to-FreezeJ](https://doc.zeroc.com/display/Ice36/Using+a+Freeze+Map+in+Java) compilers.

## Contents
- [Install Instructions](#install-instructions)
  - [Download](#download)
  - [Build From Source](#build-from-source)
- [Execution Environment](#execution-environment)
- [`slice2java` Task](#slice2java-task)
  - [Parameters](#parameters)
  - [Nested Elements](#nested-elements)
  - [Using the Task](#using-the-task)
- [`slice2freezej` Task](#slice2freezej-task)
  - [Parameters](#parameters-1)
  - [Nested Elements](#nested-elements-1)
  - [Using the Task](#using-the-task-1)
- [When do the `slice2java` and `slice2freezej` tasks recompile Slice files?](#when-do-the-slice2java-and-slice2freezej-tasks-recompile-slice-files)

## Install Instructions

### Download
The Ice Builder for Ant is available from ZeroC's  [`Nexus` repository](https://repo.zeroc.com/nexus) with _groupId_:`com.zeroc` and _artifactId_:`ant-ice`. Refer to your build systems' documentation for adding additional repositories.

You can also download the Ice Builder for Ant jar file [directly](https://repo.zeroc.com/nexus/content/repositories/releases/com/zeroc/ant-ice/4.0.0/) from `Nexus`.

### Build From Source

Use the following command to build the Ice Builder for Ant jar file (`ant-ice-4.0.0.jar`):

```shell
  $ ./gradlew build
```

## Execution Environment

The `slice2java` and `slice2freezej` tasks must be able to locate and spawn the `slice2java` and `slice2freezej` executables. You can specify the directory of your Ice installation by defining the `ice.home` ant property or the `ICE_HOME` environment variable, in which case the task assumes that the Slice compiler's executable is located in the `bin` subdirectory of the specified installation directory. For example, if `ICE_HOME` is set to `/opt/Ice` on Linux, the task assumes that the executable path name is `/opt/Ice/bin/slice2java` or `/opt/Ice/bin/slice2freezej`. Furthermore, the tasks also configure their shared library search path (if necessary for your platform) to ensure the executable can resolve its library dependencies.

If both `ice.home` and `ICE_HOME` are defined, `ice.home` takes precedence. If neither are defined, the task assumes that the executables can be found in your `PATH` and that your shared library search path is configured correctly.

Finally, you can use a task parameter to specify the full path name of the Slice compiler. Again, the tasks assume that your shared library search path is configured correctly.

## `slice2java` Task

### Parameters

The task supports the parameters listed in the following table:

| Attribute        | Description                                                                                                                                                                                                                                                                                                                                                         | Required |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------: |
| `checksum`       | Specifies the name of a class to contain the [Slice checksums](https://doc.zeroc.com/display/Ice/Using+Slice+Checksums+in+Java).                                                                                                                                                                                                                                    | No       |
| `dependencyfile` | Specifies an alternate name for the `.depend` file. If you specify a relative filename, it is relative to ant's current working directory. If not specified, the task uses the name `.depend` by default. If you do not define this attribute and `outputdir` is defined, the task creates the `.depend` file in the designated output directory (see `outputdir`). | No       |
| `ice`            | Instructs the Slice compiler to permit symbols that have a reserved prefix such as `Ice` and `Glacier2`.                                                                                                                                                                                                                                                            | No       |
| `outputdir`      | Specifies the directory in which the Slice compiler generates Java source files. If not specified, the task uses ant's current working directory.                                                                                                                                                                                                                   | No       |
| `stream`         | Indicates whether to generate [streaming support](https://doc.zeroc.com/display/Ice/Streaming+Interfaces). If not specified, streaming support is not generated.                                                                                                                                                                                                    | No       |
| `tie`            | Indicates whether to generate [tie classes](https://doc.zeroc.com/display/Ice36/Tie+Classes+in+Java). If not specified, tie classes are not generated.                                                                                                                                                                                                                | No       |
| `compat`         | Indicates whether to use the [Java Compat](https://doc.zeroc.com/display/Ice/Selecting+the+Java+Compat+Mapping) mapping. If not specified, the newer mapping will be used.                                                                                                                                                                                          | No       |
| `translator`     | Specifies the path name of the Slice compiler. If not specified, the task locates the Slice compiler in its [execution environment](#execution-environment).                                                                                                                                                                                                        | No       |

For the flag parameters (`ice`, `stream`, and `tie`), valid positive values are `on`, `true`, or `yes`; negative values are `off`, `false`, or `no`.

### Nested Elements

Several Slice compiler options must be defined as nested elements of the task:
* `define`

    Defines a preprocessor macro. The element supports the attributes name and (optionally) value, as shown below:

    ```ant
    <define name="FOO">
    <define name="BAR" value="5">
    ```

    These definitions are equivalent to the command-line options `-DFOO` and `-DBAR=5`, respectively.

* `fileset`

    Specifies the set of Slice files to be compiled. Refer to the ant documentation of its FileSet type for more information.

* `includepath`

    Specifies the include file search path for Slice files. In ant terminology, `includepath` is a path-like structure. Refer to the ant documentation of its `Path` type for more information.

* `meta`

    Defines a global metadata directive in each Slice file as well as in each included Slice file. The element supports name and value attributes.

### Using the Task

Define the following `taskdef` element in your project's build file to enable the task:

```ant
<taskdef name="slice2java" classname="Slice2JavaTask"/>
```

This configuration assumes that `ant-ice-4.0.0.jar` is already present in ant's class path. Alternatively, you can specify the JAR explicitly as follows:

```ant
<taskdef name="slice2java" classpath="/opt/Ice/lib/ant-ice-4.0.0.jar"
    classname="Slice2JavaTask"/>
```

Once activated, you can invoke the task to translate your Slice files. The example shown below is a simplified version of the ant project for the `hello` demo:

```ant
<target name="generate" depends="init">
    <mkdir dir="generated"/>
    <slice2java outputdir="generated">
        <fileset dir="." includes="Hello.ice"/>
    </slice2java>
</target>

<target name="compile" depends="generate">
    <mkdir dir="classes"/>
    <javac srcdir=".:generated" destdir="classes">
        <exclude name="generated/**"/>
        ...
    </javac>
</target>

<target name="all" depends="compile"/>

<target name="clean">
    <delete dir="generated"/>
    <delete dir="classes"/>
</target>
```

This project demonstrates some practices that we encourage you to adopt in your own projects. First, it is helpful to keep the source files generated by the Slice compiler separate from your application's source files by dedicating an output directory for the exclusive use of the Slice compiler. Doing so helps to minimize confusion and makes it easier to configure a source-code management system to ignore generated files.

Next, we also recommend that you include a `clean` target in your ant project that removes this output directory. Assuming that the dependency file (`.depend`) is also stored in this directory, removing the output directory is an efficient way to clean up your project's source tree and guarantees that all of your Slice files are recompiled in the next build.

Finally, after seeing the exclude element in the invocation of `javac` you might infer that the generated code was not being compiled, but the presence of the output directory in the `srcdir` attribute ensures that the generated code is included in the build. The purpose of the `exclude` element is to prevent ant from including the generated files twice in its target list.

## `slice2freezej` Task

### Parameters

| Attribute        | Description                                                                                                                                                                                                                                                                                                                                                         | Required |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | :------: |
| `dependencyfile` | Specifies an alternate name for the `.depend` file. If you specify a relative filename, it is relative to ant's current working directory. If not specified, the task uses the name `.depend` by default. If you do not define this attribute and `outputdir` is defined, the task creates the `.depend` file in the designated output directory (see `outputdir`). | No       |
| `ice`            | Instructs the Slice compiler to permit symbols that have a reserved prefix such as `Ice` or `Glacier2`.                                                                                                                                                                                                                                                             | No       |
| `outputdir`      | Specifies the directory in which the Slice compiler generates Java source files. If not specified, the task uses ant's current working directory.                                                                                                                                                                                                                   | No       |
| `translator`     | Specifies the path name of the Slice compiler. If not specified, the task locates the Slice compiler in its [execution environment](#execution-environment).                                                                                                                                                                                                        | No       |

### Nested Elements

Several Slice compiler options must be defined as nested elements of the task:

* `define`

    Defines a preprocessor macro. The element supports the attributes name and (optionally) value, as shown below:

    ```ant
    <define name="FOO">
    <define name="BAR" value="5">
    ```

    These definitions are equivalent to the command-line options `-DFOO` and `-DBAR=5`, respectively.

* `dict`

    Generates a Freeze map. This element is equivalent to the `--dict` [command line option](https://doc.zeroc.com/display/Ice36/Using+a+Freeze+Map+in+Java#UsingaFreezeMapinJava-slice2freezej) and supports three attributes: `name`, `key`, and `value`.

* `dictindex`

    Generates an index for a Freeze map. This element is equivalent to the `--dict-index` [command line option](https://doc.zeroc.com/display/Ice36/Using+a+Freeze+Map+in+Java#UsingaFreezeMapinJava-slice2freezej) and supports three attributes: `name`, `member`, and `casesensitive`.

* `fileset`

    Specifies the set of Slice files to be compiled. Refer to the ant documentation of its `FileSet` type for more information.

* `includepath`

    Specifies the include file search path for Slice files. In ant terminology, `includepath` is a path-like structure. Refer to the ant documentation of its `Path` type for more information.

* `index`

    Generates an index for a Freeze evictor. This element is equivalent to the `--index` [command line option](https://doc.zeroc.com/display/Ice36/Using+a+Freeze+Map+in+Java#UsingaFreezeMapinJava-slice2freezej) and supports four attributes: `name`, `type`, `member`, and `casesensitive`.

* `meta`

    Defines a global metadata directive in each Slice file as well as in each included Slice file. The element supports the attributes name and value.

### Using the Task

Define the following `taskdef` element in your project's build file to enable the task:

```ant
<taskdef name="slice2freezej" classname="Slice2FreezeJTask"/>
```

This configuration assumes that `ant-ice-4.0.0.jar` is already present in ant's class path. Alternatively, you can specify the JAR explicitly as follows:

```ant
<taskdef name="slice2freezej" classpath="/opt/Ice/lib/ant-ice-4.0.0.jar"
    classname="Slice2FreezeJTask"/>
```

Once activated, you can invoke the task to translate your Slice files. The example shown below is a simplified version of the ant project for the library demo:

```ant
<target name="generate" depends="init">
    <mkdir dir="generated"/>
    <slice2java outputdir="generated">
        <fileset dir="." includes="Library.ice"/>
    </slice2java>
    <slice2freezej ice="on" outputdir="generated">
        <fileset dir="/opt/Ice/slice/Ice" includes="BuiltinSequences.ice"/>
        <fileset dir="." includes="Library.ice"/>
        <dict name="StringIsbnSeqDict" key="string" value="Ice::StringSeq"/>
    </slice2freezej>
</target>
```

This invocation of the `slicefreezej` task enables the `ice` option because the generated Freeze map relies on a type that is defined in the `Ice` module and therefore loads the Slice file `BuiltinSequences.ice` directly.

## When do the `slice2java` and `slice2freezej` tasks recompile Slice files?

The tasks minimize recompilation by keeping track of dependencies between Slice files. For example, `A.ice` depends on `B.ice` and `C.ice` when `A.ice` includes `B.ice` and `B.ice` includes `C.ice`. Each task stores this information in a file named `.depend` in the output directory and updates these dependencies after each compilation. (You can also specify a different name for this file using a task parameter.)

Note that the tasks do not keep track of the Java source files generated from the Slices files. Consequently, removing the generated Java source files does not trigger a recompilation of the Slice file. In fact, a task compiles a Slice file only when any of the following conditions are true:

* The `.depend` file does not exist.
* The `.depend` file does not contain any information for this Slice file.
* The modification time of this Slice file is later than the modification time of the `.depend` file.
* This Slice file includes another Slice file that is eligible for (re)compilation.

The simplest way to force the tasks to recompile all of your Slice files is to remove the `.depend` file.
