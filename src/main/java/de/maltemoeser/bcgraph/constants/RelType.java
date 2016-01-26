package de.maltemoeser.bcgraph.constants;

import org.neo4j.graphdb.RelationshipType;

/**
 * Relationships between nodes in the property graph model.
 */
public enum RelType implements RelationshipType {
    TX_INPUT,       // (Output)<-[:TX_INPUT]-(Transaction)
    IN_BLOCK,       // (Transaction)-[:IN_BLOCK]->(Block)
    BELONGS_TO,     // (Output)-[:BELONGS_TO]->(Address)
    TX_OUTPUT,      // (Transaction)-[:TX_OUTPUT]->(Output)
    PREV_BLOCK,     // (Block)<-[:PREV_BLOCK]-(Block)
    PREV_TX         // (Transaction)<-[:PREV_TX]-(Transaction)
}
