# YamlToDottedProperty
Library for converting yaml to plain Java property file with full path in dotted format. Just like Spring Boot do, but without Spring Boot.


Draft for creating classical property file: https://gist.github.com/msangel/116730b2f64b8eeacb80a0a4fffa73ff

## Install
Follow instructions here: https://jitpack.io/#msangel/Yaml2DotNotation

## Usage
TBD

## Alternatives
Here are two similar projects with answers about why they did not fit my needs:
* [Spring Boot](https://github.com/spring-projects/spring-boot) has a [module for this](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-Configuration-Binding). Actually, my project inspirited by spring. But unfortunately, I was needed the lightweight implementation that respects "S" from "SOLID". And also I already had the DI container there, and that was not a spring. 
* [Governator](https://github.com/Netflix/governator) is another "multitool" with such functionality. And it has exactly the same problems as a project above. Governator is a meta-library for google guice with a [similar module](https://github.com/Netflix/governator/wiki/Configuration-Mapping). And even if my project was built based on the guice too, I still reject the option of taking a lot of unknown stuff for solving simple task like, which I can solve by homebrew solution with minimal dependencies and with nothing extra.

This project is not taking any DI container with it. 
