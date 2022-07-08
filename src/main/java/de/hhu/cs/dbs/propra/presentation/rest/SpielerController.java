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
public class SpielerController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

   //GET anfragen noch mit sql, Authe auch
    @GET
    @Path("spieler") // GET http://localhost:8080/spieler
    public Response getSpieler(@QueryParam("nachname") String nachname) throws SQLException {

        Connection connection = dataSource.getConnection();

        if(nachname == null )nachname = "%";

        PreparedStatement stmt = connection.prepareStatement("SELECT ID,Vorname,Nachname,Spitzname FROM Spieler  " +
                "WHERE Nachname LIKE '%" + nachname + "%' ");


        stmt.closeOnCompletion();
        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> spielern = new ArrayList<>();
        Map<String, Object> spieler;
        while (resultset.next()) {
            spieler = new HashMap<>();
            spieler.put("spielerid", resultset.getInt(1));
            spieler.put("vorname", resultset.getString(2));
            spieler.put("nachname", resultset.getString(3));
            spieler.put("spitzname", resultset.getString(4));
            spielern.add(spieler);

        }
        return Response.status(Response.Status.OK).entity(spielern).build();
    }

    @GET
    @Path("/spieler/{spielerid}/verletzungen") // GET http://localhost:8080//spieler/{spielerid}/verletzungen
    public Response getSpielerverletzung(@PathParam("spielerid") int spielerid) throws SQLException {

        if (spielerid == 0) spielerid = '%';

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;

        //404 Handling
        stmt = connection.prepareStatement("SELECT ID FROM Spieler WHERE ID = '"+ spielerid +"'");
        stmt.closeOnCompletion();
        ResultSet resultSet= stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Spieler Not Found!");

        //Get Handling
        stmt = connection.prepareStatement("SELECT ID,Verletzungsart,Verletzungsdauer FROM Verletzung WHERE SpielerID = "+ spielerid +"");

        stmt.closeOnCompletion();

        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> spielern = new ArrayList<>();
        Map<String, Object> spieler;
        while (resultset.next()) {
            spieler = new HashMap<>();
            spieler.put("verletzungid", resultset.getInt(1));
            spieler.put("art", resultset.getString(2));
            spieler.put("dauer", resultset.getInt(3));
            spielern.add(spieler);

        }
        return Response.status(Response.Status.OK).entity(spielern).build();
    }

   //record noch aufschreiben und declaration davon machen
    @POST
    @RolesAllowed("MITARBEITER")
    @Path("/spieler/{spielerid}/verletzungen") // POST http://localhost:8080//spieler/{spielerid}/verletzungen
    public Response createSpieler(@PathParam("spielerid") int spielerid,@FormDataParam("art") String art,@FormDataParam("dauer") int dauer) throws SQLException {

        if (spielerid == 0 || art == null || dauer == 0) {
            throw new BadRequestException("Ungueltige Parameter");
        }

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        String email = securityContext.getUserPrincipal().getName();

        //404 Handling
        stmt = connection.prepareStatement("SELECT ID FROM Spieler WHERE ID = '" + spielerid + "' ");
        stmt.closeOnCompletion();
        ResultSet resultSet = stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Spieler Not Found!");

        //Create Verletzung ID
        stmt = connection.prepareStatement("SELECT MAX(ID) FROM Verletzung");
        stmt.closeOnCompletion();
        ResultSet vId = stmt.executeQuery();
        int verletzungID = vId.getInt(1) + 1;


        stmt = connection.prepareStatement("INSERT INTO Verletzung VALUES('" + verletzungID+ "','" + art + "','" + dauer + "','" + spielerid + "')");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        String id = "";
        ResultSet key = stmt.getGeneratedKeys();
        if (key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
    }

}