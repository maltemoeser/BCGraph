# BCGraph
BCGraph allows to import the Bitcoin blockchain into a Neo4j graph database.

## Requirements
- Bitcoin Core block files (to import data into the graph database)
- At least 100 GB of storage

## Install
- Use maven to install dependencies
- To use this in production, copy `test.properties`, rename it to `production.properties` and fill in the missing directories
