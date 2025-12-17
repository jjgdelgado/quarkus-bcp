package com.bcp.training;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/suggestion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SuggestionResource {

    @POST
    public Uni<Suggestion> create(Suggestion suggestion) {
        return Panache.withTransaction(suggestion::persist);
    }

    @GET
    @Path("/{id}")
    public Uni<Suggestion> get(Long id) {
        return Suggestion.findById(id);
    }

    @GET
    public Multi<Suggestion> list() {
        return Panache.withSession(() -> Suggestion.<Suggestion>listAll())
                .onItem()
                .transformToMulti(list -> Multi.createFrom().iterable(list));
    }

    @DELETE
    public Uni<Long> deleteAll() {
        return Suggestion.deleteAll();
    }
}
