# Jenetics (_3.5.1_)

**Jenetics** is an **Genetic Algorithm**, respectively an **Evolutionary Algorithm**, library written in Java. It is designed with a clear separation of the several concepts of the algorithm, e.g. `Gene`, `Chromosome`, `Genotype`, `Phenotype`, `Population` and fitness `Function`. **Jenetics** allows you to minimize and maximize the given fitness function without tweaking it. In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java Stream API.

## Documentation

The library is fully documented ([javadoc](http://jenetics.io/javadoc/org.jenetics/3.5/index.html)) and comes with an user manual ([pdf](http://jenetics.io/manual/manual-3.5.0.pdf)).


## Requirements

### Runtime
*  **JRE 8**: Java runtime version 8 is needed for using the library, respectively for running the examples.

### Build time
*  **JDK 8**: The `JAVA_HOME` variable must be set to your java installation directory.
*  **Gradle 2.x**: [Gradle](http://www.gradle.org/) is used for building the library. (Gradle is download automatically, if you are using the Gradle Wrapper script `gradlew`, located in the base directory, for building the library.)

### Test compile/execution
*  **TestNG 6.9.10**: Jenetics uses [TestNG](http://testng.org/doc/index.html) framework for unit tests.
*  **Apache Commons Math 3.6**: [Library](http://commons.apache.org/proper/commons-math/) is used for testing statistical collectors.

## Download
* **Github**: <https://github.com/jenetics/jenetics/releases/download/v3.5.1/jenetics-3.5.1.zip>
*  **Sourceforge**:  <https://sourceforge.net/projects/jenetics/files/latest/download>
*  **Maven**: `org.bitbucket.fwilhelm:org.jenetics:3.5.1` on [Maven Central](http://search.maven.org/#search|ga|1|a%3A%22org.jenetics%22)

## Build Jenetics

[![Build Status](https://travis-ci.org/jenetics/jenetics.svg?branch=master)](https://travis-ci.org/jenetics/jenetics)

For building the Jenetics library from source, download the most recent, stable package version from [Github](https://github.com/jenetics/jenetics/releases/download/v3.5.1/jenetics-3.5.1.zip) (or [Sourceforge](https://sourceforge.net/projects/jenetics/files/latest/download)) and extract it to some build directory.

    $ unzip jenetics-<version>.zip -d <builddir>

`<version>` denotes the actual Jenetics version and `<builddir>` the actual build directory. Alternatively you can check out the master branch from Github.

    $ git clone https://github.com/jenetics/jenetics.git <builddir>

Jenetics uses [Gradle](http://www.gradle.org/downloads) as build system and organizes the source into *sub*-projects (modules). Each sub-project is located in it’s own sub-directory:

* **org.jenetics**: This project contains the source code and tests for the Jenetics core-module.
* **org.jenetics.example**: This project contains example code for the *core*-module.
* **org.jenetics.doc**: Contains the code of the web-site and the manual.

For building the library change into the `<builddir>` directory (or one of the module directory) and call one of the available tasks:

* **compileJava**: Compiles the Jenetics sources and copies the class files to the `<builddir>/<module-dir>/build/classes/main` directory.
* **jar**: Compiles the sources and creates the JAR files. The artifacts are copied to the `<builddir>/<module-dir>/build/libs` directory.
* **javadoc**: Generates the API documentation. The Javadoc is stored in the `<builddir>/<module-dir>/build/docs` directory
* **test**: Compiles and executes the unit tests. The test results are printed onto the console and a test-report, created by TestNG, is written to `<builddir>/<module-dir>` directory.
* **clean**: Deletes the `<builddir>/build/*` directories and removes all generated artifacts.

For building the library jar from the source call

    $ cd <build-dir>
    $ ./gradlew jar


**IDE Integration**

Gradle has tasks which creates the project file for Eclipse and IntelliJ IDEA. Call

    $ ./gradlew [eclipse|idea]

for creating the project files for Eclipse or IntelliJ, respectively. Whereas the latest version of [IntelliJ IDEA](https://www.jetbrains.com/idea/) has decent native Gradle support.

The latest Eclipse version (4.4.2) has problems compiling some _valid_ lambda expressions; e.g. the `HelloWorld::eval` function in the example below. If you have such problems when trying to compile the library with Eclipse, you can fix this by adding an explicit cast to the method reference:

```java
 Engine
     .builder((Function<Genotype<BitGene>, Integer>)HelloWorld::eval, gtf)
     .build();
```

 Or you are using [IntelliJ](https://www.jetbrains.com/idea/download/) instead.

## Example

### Hello World (Ones counting)

The minimum evolution Engine setup needs a genotype factory, `Factory<Genotype<?>>`, and a fitness `Function`. The `Genotype` implements the `Factory` interface and can therefore be used as prototype for creating the initial `Population` and for creating new random `Genotypes`.

```java
import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Genotype;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

public class HelloWorld {
    // 2.) Definition of the fitness function.
    private static Integer eval(Genotype<BitGene> gt) {
        return ((BitChromosome)gt.getChromosome()).bitCount();
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<BitGene>> gtf =
            Genotype.of(BitChromosome.of(10, 0.5));

        // 3.) Create the execution environment.
        Engine<BitGene, Integer> engine = Engine
            .builder(HelloWorld::eval, gtf)
            .build();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<BitGene> result = engine.stream()
            .limit(100)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println("Hello World:\n" + result);
    }
}
```

In contrast to other GA implementations, the library uses the concept of an evolution stream (`EvolutionStream`) for executing the evolution steps. Since the `EvolutionStream` implements the Java Stream interface, it works smoothly with the rest of the Java streaming API. Now let's have a closer look at listing above and discuss this simple program step by step:

1. The probably most challenging part, when setting up a new evolution `Engine`, is to transform the problem domain into a appropriate `Genotype` (factory) representation. In our example we want to count the number of ones of a `BitChromosome`. Since we are counting only the ones of one chromosome, we are adding only one `BitChromosome` to our `Genotype`. In general, the `Genotype` can be created with 1 to n chromosomes.

1. Once this is done, the fitness function which should be maximized, can be defined. Utilizing the new language features introduced in Java 8, we simply write a private static method, which takes the genotype we defined and calculate it's fitness value. If we want to use the optimized bit-counting method, `bitCount()`, we have to cast the `Chromosome<BitGene>` class to the actual used `BitChromosome` class. Since we know for sure that we created the Genotype with a `BitChromosome`, this can be done safely. A reference to the eval method is then used as fitness function and passed to the `Engine.build` method.

1. In the third step we are creating the evolution `Engine`, which is responsible for changing, respectively evolving, a given population. The `Engine` is highly configurable and takes parameters for controlling the evolutionary and the computational environment. For changing the evolutionary behavior, you can set different alterers and selectors. By changing the used `Executor` service, you control the number of threads, the Engine is allowed to use. An new `Engine` instance can only be created via its builder, which is created by calling the `Engine.builder` method.

1. In the last step, we can create a new `EvolutionStream` from our `Engine`. The `EvolutionStream` is the model or view of the evolutionary process. It serves as a »process handle« and also allows you, among other things, to control the termination of the evolution. In our example, we simply truncate the stream after 100 generations. If you don't limit the stream, the `EvolutionStream` will not terminate and run forever. Since the `EvolutionStream` extends the `java.util.stream.Stream` interface, it integrates smoothly with the rest of the Java Stream API. The final result, the best `Genotype` in our example, is then collected with one of the predefined collectors of the `EvolutionResult` class.


### Evolving images

This example tries to approximate a given image by semitransparent polygons.  It comes with an Swing UI, where you can immediately start your own experiments. After compiling the sources with

    $ ./gradlew compileTestJava

you can start the example by calling

    $ ./jrun org.jenetics.example.image.EvolvingImages
    
![Evolving images](https://raw.githubusercontent.com/jenetics/jenetics/master/org.jenetics.doc/src/main/resources/graphic/EvolvingImagesExampleScreenShot.png)

The previous image shows the GUI after evolving the default image for about 4,000 generations. With the »Open« button it is possible to load other images for polygonization. The »Save« button allows to store polygonized images in PNG format to disk. At the button of the UI, you can change some of the GA parameters of the example.

## Citations

* Andreas Holzinger (Editor), Igo Jurisica (Editor). [Interactive Knowledge Discovery and Data Mining in Biomedical Informatics.](http://www.springer.com/computer/database+management+%26+information+retrieval/book/978-3-662-43967-8) _Lecture Notes in Computer Science, Vol. 8401._ [Springer](http://www.springer.com), 2014
* Lyazid Toumi , Abdelouahab Moussaoui, Ahmet Ugur. [Particle swarm optimization for bitmap join indexes selection problem in data warehouses.](http://link.springer.com/article/10.1007%2Fs11227-013-1058-9) _[The Journal of Supercomputing](http://link.springer.com/journal/11227), May 2014, Volume 68, [Issue 2](http://link.springer.com/journal/11227/68/2/page/1), pp 672-708_

## License

The library is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

	Copyright 2007-2016 Franz Wilhelmstötter

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.


## Release notes

_[All Release Notes](RELEASE_NOTES.md)_

### 3.5.1

#### Bug fixes

* [#111](https://github.com/jenetics/jenetics/issues/111): Dead lock for single-threaded executors.

### 3.5.0

#### Improvement

* [#81](https://github.com/jenetics/jenetics/issues/81): It is now easier to register user-defined JAXB marshallings -- `org.jenetics.util.IO.JAXB.register`
* [#90](https://github.com/jenetics/jenetics/issues/90), [#91](https://github.com/jenetics/jenetics/issues/91): The manual contains now a section where the performance of the `MonteCarloSelector` and an evolutionary `Selector` is compared (fig. 6.8, page 52).
* [#96](https://github.com/jenetics/jenetics/issues/96): Merge branch with incubation module `org.jenetix`, which contains experimental classes.
* [#101](https://github.com/jenetics/jenetics/issues/101): Add manual example for solving the *Rastrigin* function.

## Used software

<a href="https://www.jetbrains.com/idea/">![IntelliJ](http://jenetics.io/img/icon_IntelliJIDEA.png)</a>

