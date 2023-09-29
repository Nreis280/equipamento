package br.com.fiap.domain.resource;

import br.com.fiap.domain.entity.Equipamento;
import br.com.fiap.domain.repository.EquipamentoRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EquipamentoResource implements Resource<Equipamento, Long> {

    UriInfo uriInfo;

    private EquipamentoRepository repo = EquipamentoRepository.build();
    @POST
    @Override
    public Response persist(Equipamento equipamento) {
        Equipamento persist = repo.persist(equipamento);
        //Criando a URI da requisição
        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI uri = ub.path(String.valueOf(persist.getId())).build();
        return Response.created(uri).entity(equipamento).build();
    }

    @GET
    @Override
    public Response findAll() {
        List<Equipamento> all = repo.findAll();
        return Response
                .status(Response.Status.OK)
                .entity(all)
                .build();
    }

    @GET
    @Path("/{id}")
    @Override
    public Response findById(@PathParam("id") Long id) {
        Equipamento e = repo.findById(id);
        if (Objects.isNull(e)) return Response.status(404).build();
        return Response
                .status(Response.Status.OK)
                .entity(e)
                .build();
    }
    @PUT
    @Path("/{id}")
    @Override
    public Response update(@PathParam("id") Long id, Equipamento equipamento) {
        equipamento.setId(id);
        Equipamento e = repo.update(equipamento);
        if (Objects.isNull(e)) return Response.status(404).build();
        return Response.ok().entity(e).build();
    }
    @DELETE
    @Path("/{id}")
    @Override
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = repo.delete(id);
        if (deleted) return Response.ok().build();
        return Response.status(404).build();
    }
}
