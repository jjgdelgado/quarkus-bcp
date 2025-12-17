package com.bcp.training.resource;

import com.bcp.training.event.SpeakerWasCreated;
import com.bcp.training.model.Speaker;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.net.URI;
import java.util.List;

@Path("/speakers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SpeakerResource {

    @Channel("new-speakers-out")
    Emitter<SpeakerWasCreated> emitter;

    @GET
    @Path("/{id}")
    public Uni<Speaker> get(Long id) {
        return Speaker.findById(id);
    }

    @GET
    public Uni<List<Speaker>> listAll() {
        return Speaker.listAll();
    }

    @POST
    public Uni<Response> create(Speaker newSpeaker) {
        return Panache.withTransaction(() ->
                newSpeaker.<Speaker>persist()
                        .map(speaker -> {
                            SpeakerWasCreated event = new SpeakerWasCreated(
                                    speaker.id,
                                    speaker.fullName,
                                    speaker.affiliation,
                                    speaker.email
                            );
                            emitter.send(event);
                            return Response.created(URI.create("/speakers/" + speaker.id))
                                    .build();
                        })
        );
    }
}
