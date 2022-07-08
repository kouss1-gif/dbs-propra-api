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
public class ReportagenController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;


    @GET
    @Path("reportagen") // GET http://localhost:8080/reportagen
    public Response getReportage() throws SQLException {

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT ID,Ueberschrifft,Text FROM Reportage");


        stmt.closeOnCompletion();
        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> reportagen = new ArrayList<>();
        Map<String, Object> reportage;
        while (resultset.next()) {
            reportage = new HashMap<>();
            reportage.put("reportageid", resultset.getInt(1));
            reportage.put("ueberschrift", resultset.getString(2));
            reportage.put("text", resultset.getString(3));
            reportagen.add(reportage);

        }
        return Response.status(Response.Status.OK).entity(reportagen).build();
    }

    @POST
    @RolesAllowed("MITARBEITER")
    @Path("reportagen") // POST http://localhost:8080/reportagen
    public Response createReportage(@FormDataParam("ueberschrift") String ueberschrift,@FormDataParam("text") String text) throws SQLException {
        if ((ueberschrift == null) || (text == null)){
            throw new BadRequestException("Ungueltige Parameter");}

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        String email = securityContext.getUserPrincipal().getName();

        stmt= connection.prepareStatement("SELECT MAX(ID) FROM Reportage");
        stmt.closeOnCompletion();
        ResultSet rId = stmt.executeQuery();
        int reportageid =  rId.getInt(1) + 1;

        stmt = connection.prepareStatement("SELECT Bildone,Bildtwo,Mitarbeiternutzeremail,SpielID FROM Reportage");
        stmt.closeOnCompletion();
        ResultSet resultSet = stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Reportage Not Found!");
        String bildone = resultSet.getString("Bildone");
        String bildtwo = resultSet.getString("Bildtwo");
        String mitarbeiternutzeremail = resultSet.getString("Mitarbeiternutzeremail");
        int spielid = resultSet.getInt("SpielID");

        stmt = connection.prepareStatement("INSERT INTO Reportage VALUES('"+ reportageid + "','"+ bildone+ "','"+ bildtwo+ "','"+ text + "','"+ ueberschrift + "','"+ mitarbeiternutzeremail+ "','"+ spielid+"' )");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        String id="";
        ResultSet key = stmt.getGeneratedKeys();
        if(key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
    }

    @PATCH
    @RolesAllowed("MITARBEITER")
    @Path("/reportagen/{reportageid}") // PATCH http://localhost:8080//reportagen/{reportageid}
    public Response updateReportage(@PathParam("reportageid") int reportageid,@FormDataParam("ueberschrift") String ueberschrift, @FormDataParam("text") String text) throws SQLException {

        if ((ueberschrift == null) && (text == null) ){
            throw new BadRequestException("Ungueltige Parameter");}

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        String email = securityContext.getUserPrincipal().getName();

        if (ueberschrift == null){
            stmt= connection.prepareStatement("SELECT Ueberschrifft FROM Reportage WHERE ROWID = '"+ reportageid +"'  ");
            stmt.closeOnCompletion();
            ResultSet newueberschrift = stmt.executeQuery();
            ueberschrift =  newueberschrift.getString(1);
        }
        if (text == null){
            stmt= connection.prepareStatement("SELECT Text FROM Reportage WHERE ROWID = '"+ reportageid +"'  ");
            stmt.closeOnCompletion();
            ResultSet newtext = stmt.executeQuery();
            text =  newtext.getString(2);
        }

        //404 Handling
        stmt = connection.prepareStatement("SELECT ROWID FROM Reportage WHERE ROWID = '"+ reportageid +"' ");
        stmt.closeOnCompletion();
        ResultSet resultSet= stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Reportage Not Found!" );

        //Update Reportage
        stmt = connection.prepareStatement("UPDATE Reportage SET Ueberschrifft = ?, Text = ?" +
                "WHERE ROWID = '"+ reportageid +"'" );
        stmt.closeOnCompletion();
        stmt.setObject(1, ueberschrift);
        stmt.setObject(2, text);

        stmt.closeOnCompletion();
        stmt.executeUpdate();

        return Response.status(Response.Status.NO_CONTENT).build();
    }
    @DELETE
    @RolesAllowed("MITARBEITER")
    @Path("/reportagen/{reportageid}")// DELETE http://localhost:8080/reportagen/{reportageid}
    public Response deleteReportage(@PathParam("reportageid") int reportageid) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;

        String email = securityContext.getUserPrincipal().getName();

        //404 Handling
        stmt= connection.prepareStatement("SELECT ID FROM Reportage WHERE ID = '"+ reportageid +"'");
        stmt.closeOnCompletion();
        ResultSet resultset=stmt.executeQuery();
        if (!resultset.isBeforeFirst())
            throw new NotFoundException("404 Reportage not found!");

        //DELETE Handling
        stmt= connection.prepareStatement("DELETE FROM Reportage WHERE ID = '"+ reportageid +"' ");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}