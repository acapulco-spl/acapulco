# Acapulco
Acapulco is a tool to support Product Line configuration using genetic algorithms and consistency-preserving configuration operators.

## Installation

For the moment Acapulco is to be launched from an Eclipse workspace and not as an update site or similar.

Requirements
- Java JDK 13+
- Eclipse 2020 (4.18.0)+

In your eclipse installation, install
- [FeatureIDE](https://featureide.github.io/) framework for feature-oriented software development. Tested with version 3.8.1 and 3.9.3.
- [Xtend](https://eclipse.dev/Xtext/xtend/) framework. Some acapulco source code uses Xtend instead of Java, so once you will import the acapulco plugins in the workspace, the automatic build of the source code will create some needed Java classes in the `xtend-gen` package of the `acapulco` project. Tested with version 2.31

Import the plugin `org.eclipse.emf.henshin.model` in the Eclipse workspace [Eclipse Henshin](https://www.eclipse.org/henshin/) [Eclipse Henshin repository](https://git.eclipse.org/c/henshin/org.eclipse.emft.henshin.git/) Tested with version 1.9.0.

Import the acapulco plugins (`plugins` folder of this repository) in the Eclipse workspace. Then right click the plugin and select `Run As` -> `Eclipse Application`. A new instance of Eclipse will be opened with the Acapulco functionality.

## Getting started
- Right click a feature model (in the Package Explorer or Project Explorer) and click on `Acapulco prepare`. A set of resources will be generated with a message about how to perform the `Acapulco launch`. When launching, a window to configure the parameters for the optimization will appear. The pareto front configurations and a CSV file with their objectives' values will be generated at the end of the process.

The video below shows a demo of Acapulco.

https://user-images.githubusercontent.com/7057319/207927725-13893bbd-da10-43c9-afec-cc9b89e585f2.mp4

## Optimization objectives and extensibility
- Objectives for the optimization can be contributed through the `acapulco.objective` extension point.
- Predefined generic objectives already included are: Total sum of numeric feature attributes in a CSV file, Number of features in the configuration, or Number of generated files after deriving the configuration through the Product Line.

## Other resources
- A comparison of a version of Acapulco with other two tools is available in a [replication package](https://github.com/acapulco-spl/acapulco_replication_package). The first version of Acapulco is a refactoring of this other repository.
