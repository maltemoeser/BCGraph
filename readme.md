[![Build Status](https://travis-ci.org/maltemoeser/BCGraph.svg?branch=master)](https://travis-ci.org/maltemoeser/BCGraph)
[![Coverage Status](https://coveralls.io/repos/github/maltemoeser/BCGraph/badge.svg?branch=master)](https://coveralls.io/github/maltemoeser/BCGraph?branch=master)

# BCGraph

BCGraph allows to import the Bitcoin blockchain into a Neo4j graph database and provides multiple tools to analyze the resulting data set.

## Requirements

- Bitcoin Core block files (to import data into the graph database)
- Up to twice the amount of storage that the Bitcoin Core block files require

## Install

- Use maven to install dependencies
- Copy `test.properties` to `src/main/resources/`, rename it to `production.properties` and fill in the missing directories

## Examples

A few examples are available at [maltemoeser/BCGraph-Examples](https://github.com/maltemoeser/BCGraph-Examples).
