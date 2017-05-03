package com.bridge18.relationship;

import com.bridge18.relationship.api.LagomRelationshipService;
import com.bridge18.relationship.services.lagom.LagomRelationshipServiceImpl;
import com.bridge18.relationship.services.objects.RelationshipService;
import com.bridge18.relationship.services.objects.impl.RelationshipServiceImpl;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class RelationshipModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bind(RelationshipService.class).to(RelationshipServiceImpl.class);

        bindServices(serviceBinding(LagomRelationshipService.class, LagomRelationshipServiceImpl.class));
    }
}