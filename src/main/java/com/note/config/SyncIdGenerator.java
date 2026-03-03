package com.note.config;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.io.Serializable;

public class SyncIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        // Retrieve the current ID value of the entity being saved
        Object id = session.getEntityPersister(null, object).getIdentifier(object, session);

        if (id != null) {
            return (Serializable) id; // Use existing MySQL ID
        }

        // Return null to let the database handle auto-increment/identity
        return null;
    }
}