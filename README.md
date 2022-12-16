# Acapulco
Acapulco is a tool to support Product Line configuration using genetic algorithms and consistency-preserving configuration operators.

## Installation
Requirements
- Java JDK 13+
- Eclipse 2020 (4.18.0)+

In your eclipse installation, install
- [FeatureIDE](https://featureide.github.io/) framework for feature-oriented software development. Tested with version 3.8.1.
- [Eclipse Henshin](https://www.eclipse.org/henshin/) 1.7.0. The required plugin is `org.eclipse.emf.henshin.model`.

Import the acapulco plugins (`plugins` folder of this repository) in the Eclipse workspace. Then right click the plugin and select `Run As` -> `Eclipse Application`

## Getting started
- Right click a feature model and click on `Acapulco prepare`. A set of resources will be generated with a message about how to perform the `Acapulco launch`. When launching, a window to configure the parameters for the optimization will appear. The pareto front configurations and a CSV file with their objectives' values will be generated at the end of the process.

The video below shows a demo of Acapulco.

https://user-images.githubusercontent.com/7057319/207927725-13893bbd-da10-43c9-afec-cc9b89e585f2.mp4

## Optimization objectives and extensibility
- Objectives for the optimization can be contributed through the `acapulco.objective` extension point.
- Predefined generic objectives already included are: Total sum of numeric feature attributes in a CSV file, Number of features in the configuration, or Number of generated files after deriving the configuration through the Product Line.

## Other resources
- A comparison of a version of Acapulco with other two tools is available in a [replication package](https://github.com/acapulco-spl/acapulco_replication_package). The first version of Acapulco is a refactoring of this other repository.
