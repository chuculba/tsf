/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service.advanced;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import common.advanced.Person;
import common.advanced.PersonService;
import common.advanced.PersonCollection;

/**
 * JAX-RS PersonService root resource
 */
public class PersonServiceImpl implements PersonService {

    private PersonInfoStorage storage;

    /**
     * Thread-safe JAX-RS UriInfo proxy providing the information about the current request URI, etc
     */
    @Context
    private UriInfo uriInfo;

    public PersonServiceImpl() {
    }

    public void setStorage(PersonInfoStorage storage) {
        this.storage = storage;
    }

    @Override
    public Person getPersonSubresource(Long id) {
        return storage.getPerson(id);
    }

    @Override
    public Response getPersons(Integer start, Integer size) {
        List<Person> collPer = storage.getPersons(start, size);
        PersonCollection perColl = new PersonCollection();
        perColl.setList(collPer);
        ResponseBuilder rb;
        if (collPer.size() == 0) {
            rb = Response.noContent();
        } else {
            rb = Response.ok(perColl);
        }
        return rb.build();
    }

    @Override
    public Response addChild(Long parentId, Person child) {
        Person parent = storage.getPerson(parentId);
        if (parent == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        parent.addChild(child);

        Long childId = storage.addPerson(child);

        UriBuilder locationBuilder = uriInfo.getBaseUriBuilder();
        locationBuilder.path(PersonService.class);
        URI childLocation = locationBuilder.path("{id}").build(childId);

        return Response.status(Response.Status.CREATED).location(childLocation).build();
    }

}
