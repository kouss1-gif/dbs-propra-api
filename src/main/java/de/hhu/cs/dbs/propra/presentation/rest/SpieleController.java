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
public class SpieleController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    //SQL-Anfragen müssen noch richtig geschrieben werden
    @GET
    @Path("spiele") // GET http://localhost:8080/spiele
    public Response getSpiele(@QueryParam("angeschaut")boolean angeschaut) throws SQLException {

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;

        if(angeschaut==true){
        stmt = connection.prepareStatement("SELECT DISTINCT ID,VODLink,Spieldatum FROM Spiel,PremiumnutzeranschauenSpiel WHERE ID IN (SELECT SpielID FROM PremiumnutzeranschauenSpiel)");}
        else {
            stmt = connection.prepareStatement("SELECT DISTINCT ID,VODLink,Spieldatum FROM Spiel,PremiumnutzeranschauenSpiel WHERE ID NOT IN (SELECT SpielID FROM PremiumnutzeranschauenSpiel)");}

        stmt.closeOnCompletion();
        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> spiele = new ArrayList<>();
        Map<String, Object> spiel;
        while (resultset.next()) {
            spiel = new HashMap<>();
            spiel.put("spielid", resultset.getInt(1));
            spiel.put("link", resultset.getString(2));
            spiel.put("datum", resultset.getString(3));
            spiele.add(spiel);

        }
        return Response.status(Response.Status.OK).entity(spiele).build();
    }
    @RolesAllowed("MITARBEITER")
    @POST
    @Path("spiele") // POST http://localhost:8080/spiele
    public Response createSpiel(@FormDataParam("link") String link,@FormDataParam("datum") String datum,@FormDataParam("manschaft1id") int manschaft1id,@FormDataParam("manschaft2id") int manschaft2id,@FormDataParam("ligaid") int ligaid) throws SQLException {

        if ((link == null) || (datum == null) || (manschaft1id == 0) || (manschaft2id == 0) || (ligaid == 0)){
            throw new BadRequestException("Ungueltige Parameter");}

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;

        stmt= connection.prepareStatement("SELECT MAX(ROWID) FROM Spiel ");
        stmt.closeOnCompletion();
        ResultSet sId = stmt.executeQuery();
        int spielid =  sId.getInt(1) + 1;

        stmt = connection.prepareStatement("INSERT INTO Spiel VALUES('"+ spielid +"','"+ datum +"','"+ link +"','"+ ligaid +"' )");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        String id="";
        ResultSet key = stmt.getGeneratedKeys();
        if(key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
    }

}