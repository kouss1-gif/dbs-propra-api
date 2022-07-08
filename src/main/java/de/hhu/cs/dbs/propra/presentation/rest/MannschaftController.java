package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class MannschaftController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @GET
    @Path("mannschaften")// GET http://localhost:8080/mannschaften
    public Response getMannschaft(@QueryParam("mannschaftid") int mannschaftid,@QueryParam("name") String name) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement stmt;

        if (name == null) name = "%";
        if (mannschaftid == 0) mannschaftid = '%';


        stmt = connection.prepareStatement("SELECT * FROM Mannschaft  " +
                "WHERE Mannschaftsname LIKE '%" + name + "%' ");


        stmt.closeOnCompletion();

        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> mannschaften = new ArrayList<>();
        Map<String, Object> mannschaft;
        while (resultset.next()) {
            mannschaft = new HashMap<>();
            mannschaft.put("mannschaftid", resultset.getInt(1));
            mannschaft.put("name", resultset.getString(2));
            mannschaften.add(mannschaft);

        }
        return Response.status(Response.Status.OK).entity(mannschaften).build();
    }
}