[![Build Status](https://travis-ci.org/maltemoeser/BCGraph.svg?branch=master)](https://travis-ci.org/maltemoeser/BCGraph)
[![Coverage Status](https://coveralls.io/repos/github/maltemoeser/BCGraph/badge.svg?branch=master)](https://coveralls.io/github/maltemoeser/BCGraph?branch=master)

# BCGraph
BCGraph allows to import the Bitcoin blockchain into a Neo4j graph database.

## Requirements
- Bitcoin Core block files (to import data into the graph database)
- At least 100 GB of storage

## Install
- Use maven to install dependencies
- To use this in production, copy `test.properties`, rename it to `production.properties` and fill in the missing directories
