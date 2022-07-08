package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
public class LigaController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @GET
    @Path("ligen") // GET http://localhost:8080/ligen
    public Response getLiga() throws SQLException {

        Connection connection = dataSource.getConnection();


        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Liga ");

        stmt.closeOnCompletion();
        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> titels = new ArrayList<>();
        Map<String, Object> liga;
        while (resultset.next()) {
            liga = new HashMap<>();
            liga.put("ligaid", resultset.getInt(1));
            liga.put("name", resultset.getString(2));
            titels.add(liga);

        }
        return Response.status(Response.Status.OK).entity(titels).build();
    }


    // @RolesAllowed("Spiele")  mit rolesallowed?
    // 404Handling ist richtig?
    @GET
    @Path("/ligen/{ligaid}/spiele") // GET http://localhost:8080//ligen/{ligaid}/spiele
    public Response createLiga(@PathParam("ligaid") int ligaid) throws SQLException {
        if (ligaid == 0) ligaid = '%';

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;

        //404 Handling
        stmt = connection.prepareStatement("SELECT ROWID FROM Liga WHERE ROWID = '"+ ligaid +"'");
        stmt.closeOnCompletion();
        ResultSet resultSet= stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Liga Not Found!" );

        //Get Handling
        stmt = connection.prepareStatement("SELECT ID,VODLink,Spieldatum FROM Spiel WHERE LigaID = "+ ligaid +"");

        stmt.closeOnCompletion();

        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> ligen = new ArrayList<>();
        Map<String, Object> liga;
        while (resultset.next()) {
            liga = new HashMap<>();
            liga.put("spielid", resultset.getInt(1));
            liga.put("link", resultset.getString(2));
            liga.put("datum", resultset.getString(3));
            ligen.add(liga);

        }
        return Response.status(Response.Status.OK).entity(ligen).build();
    }

}