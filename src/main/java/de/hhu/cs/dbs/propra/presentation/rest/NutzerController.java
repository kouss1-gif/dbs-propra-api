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
public class NutzerController {
    @Inject
    private DataSource dataSource;

    @Context
    private SecurityContext securityContext;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("nutzer")// GET http://localhost:8080/nutzer
    public Response getNutzer(@QueryParam("email") String email) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement stmt;

        stmt = connection.prepareStatement("SELECT ROWID,Email,Passwort FROM Nutzer WHERE Email LIKE '%" + email + "%' ");

        stmt.closeOnCompletion();

        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> nutzern = new ArrayList<>();
        Map<String, Object> nutzer;
        while (resultset.next()) {
            nutzer = new HashMap<>();
            nutzer.put("nutzerid", resultset.getInt(1));
            nutzer.put("email", resultset.getString(2));
            nutzer.put("passwort", resultset.getString(3));
            nutzern.add(nutzer);

        }
        return Response.status(Response.Status.OK).entity(nutzern).build();
    }

    @POST
    @Path("nutzer") // POST http://localhost:8080/nutzer
    public Response createNutzer(@FormDataParam("email") String email,@FormDataParam("passwort") String passwort) throws SQLException {
        if ((email == null) || (passwort == null)){
            throw new BadRequestException("Ungueltige Parameter");}

            Connection connection = dataSource.getConnection();

            PreparedStatement stmt;
            stmt = connection.prepareStatement("SELECT Email FROM Nutzer WHERE Email= '" + email + "'");
            stmt.closeOnCompletion();
            ResultSet resultSet = stmt.executeQuery();

            stmt = connection.prepareStatement("INSERT INTO Nutzer VALUES('" + email + "','" + passwort + "')");
            stmt.closeOnCompletion();
            stmt.executeUpdate();

            String id = "";
            ResultSet key = stmt.getGeneratedKeys();
            if (key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();

    }

    @GET
    @Path("fans")// GET http://localhost:8080/fans
    public Response getFan(@QueryParam("username") String username) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement stmt;

        if (username == null) username = "%";

        stmt = connection.prepareStatement("SELECT F.ROWID,N.ROWID,Nutzeremail,Username,Passwort FROM Nutzer N JOIN Fan F on N.Email = F.Nutzeremail WHERE Username = '" + username + "'");

        stmt.closeOnCompletion();
        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> fans = new ArrayList<>();
        Map<String, Object> fan;
        while (resultset.next()) {
            fan = new HashMap<>();

            fan.put("fanid", resultset.getInt(1));
            fan.put("nutzerid", resultset.getInt(2));
            fan.put("email", resultset.getString(3));
            fan.put("username", resultset.getString(4));
            fan.put("passwort", resultset.getString(5));
            fans.add(fan);

        }
        return Response.status(Response.Status.OK).entity(fans).build();
    }

    @POST
    @Path("fans") // POST http://localhost:8080/fans
    public Response createFan(@FormDataParam("email") String email,@FormDataParam("passwort") String passwort,@FormDataParam("username") String username) throws SQLException {
        if ((email==null) || (passwort == null) || (username == null)){
            throw new BadRequestException("Ungueltige Parameter");}

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        stmt = connection.prepareStatement("SELECT Email FROM Nutzer WHERE Email = '"+ email +"'");
        stmt.closeOnCompletion();
        ResultSet resultSet = stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Nutzer Not Found!");

        stmt = connection.prepareStatement("SELECT MannschaftsID FROM Fan");
        stmt.closeOnCompletion();
        ResultSet resultSett = stmt.executeQuery();
        int mannschaftid = resultSett.getInt("MannschaftsID");

        //if(!resultSet.isBeforeFirst()) createNutzer(email,passwort);

        stmt = connection.prepareStatement("INSERT INTO Fan VALUES('"+ email  +"','"+ username +"','"+ mannschaftid+"')");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        String id = "";
        ResultSet key = stmt.getGeneratedKeys();
        if(key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
    }
    @GET
    @Path("/fans/{fanid}/mannschaft") // GET http://localhost:8080//fans/{fanid}/mannschaft
    public Response getFanmannschaft(@PathParam("fanid") int fanid) throws SQLException {
        if (fanid == 0) fanid = '%';

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;

        //404 Handling
        stmt = connection.prepareStatement("SELECT ROWID FROM Fan WHERE ROWID = '"+ fanid +"'");
        stmt.closeOnCompletion();
        ResultSet resultSet= stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Fan Not Found!" );

        //Get Handling
        stmt = connection.prepareStatement("SELECT MannschaftsID FROM Fan WHERE ROWID = '"+ fanid + "'");

        stmt.closeOnCompletion();

        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> mannscahften = new ArrayList<>();
        Map<String, Object> mannschaft;
        while (resultset.next()) {
            mannschaft = new HashMap<>();
            mannschaft.put("mannschaftsid", resultset.getInt(1));
            mannscahften.add(mannschaft);

        }
        return Response.status(Response.Status.OK).entity(mannscahften).build();
    }
    @POST
    @RolesAllowed("FAN")
    @Path("/fans/{fanid}/mannschaft") // POST http://localhost:8080///fans/{fanid}/mannschaft
    public Response createFanmannschaft(@PathParam("fanid") int fanid,@FormDataParam("mannschaftid") int mannschaftid) throws SQLException {

        if ((fanid == 0) && (mannschaftid == 0)) {
            throw new BadRequestException("Ungueltige Parameter");}

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        String email = securityContext.getUserPrincipal().getName();

        //404
        stmt = connection.prepareStatement("SELECT ROWID,Nutzeremail,Username FROM Fan WHERE ROWID = '" + fanid + "'");
        stmt.closeOnCompletion();
        ResultSet resultSet = stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Fan Not Found!");
        String fannutzeremail = resultSet.getString("Nutzeremail");
        String username = resultSet.getString("Username");

        //DELETE
        stmt= connection.prepareStatement("DELETE FROM Fan WHERE ROWID = '"+ fanid +"'");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        stmt = connection.prepareStatement("INSERT INTO Fan VALUES('" +fannutzeremail  + "','" + username + "','" + mannschaftid + "')");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        String id = "";
        ResultSet key = stmt.getGeneratedKeys();
        if (key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
    }
    @PATCH
    @RolesAllowed("FAN")
    @Path("/fans/{fanid}/mannschaft") // PATCH http://localhost:8080//fans/{fanid}/mannschaft
    public Response updateFan(@PathParam("fanid") int fanid,@FormDataParam("mannschaftid")int mannschaftid) throws SQLException {

        if ((fanid == 0) && (mannschaftid == 0) ){
            throw new BadRequestException("Ungueltige Parameter");}

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        String email = securityContext.getUserPrincipal().getName();

        if (mannschaftid == 0){
            stmt= connection.prepareStatement("SELECT ID FROM Mannschaft WHERE ID = '"+ mannschaftid +"'");
            stmt.closeOnCompletion();
            ResultSet newmannschaft = stmt.executeQuery();
            mannschaftid =  newmannschaft.getInt(1);
        }
        //404
        stmt = connection.prepareStatement("SELECT ROWID FROM Fan WHERE ROWID = '"+ fanid +"'");
        stmt.closeOnCompletion();
        ResultSet resultSet= stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) throw new NotFoundException("404 Fan Not Found!");

        //Update Fan
        stmt = connection.prepareStatement("UPDATE Fan SET MannschaftsID = ?" +
                "WHERE ROWID = '"+ fanid +"'");
        stmt.closeOnCompletion();
        stmt.setObject(1, mannschaftid);

        stmt.closeOnCompletion();
        stmt.executeUpdate();

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("mitarbeiter")// GET http://localhost:8080/mitarbeiter
    public Response getMitarbeiter(@QueryParam("personalnummer") int personalnummer) throws SQLException {
        Connection connection = dataSource.getConnection();

        PreparedStatement stmt;

        if (personalnummer == 0) personalnummer = '%';

        stmt = connection.prepareStatement("SELECT N.ROWID,M.ROWID,Nutzeremail,Passwort,Personalnummer FROM Nutzer N INNER JOIN Mitarbeiter M on N.Email = M.Nutzeremail WHERE Personalnummer = '" + personalnummer + "'");

        stmt.closeOnCompletion();
        ResultSet resultset = stmt.executeQuery();

        List<Map<String, Object>> mitarbeitern = new ArrayList<>();
        Map<String, Object> mitarbeiter;
        while (resultset.next()) {
            mitarbeiter = new HashMap<>();
            mitarbeiter.put("nutzerid", resultset.getInt(1));
            mitarbeiter.put("mitarbeiterid", resultset.getInt(2));
            mitarbeiter.put("email", resultset.getString(3));
            mitarbeiter.put("passwort", resultset.getString(4));
            mitarbeiter.put("personalnummer", resultset.getInt(5));
            mitarbeitern.add(mitarbeiter);

        }
        return Response.status(Response.Status.OK).entity(mitarbeitern).build();
    }

    @POST
    @Path("mitarbeiter") // POST http://localhost:8080/mitarbeiter
    public Response createMitarbeiter(@FormDataParam("email") String email,@FormDataParam("passwort") String passwort,@FormDataParam("personalnummer") int personalnummer) throws SQLException {

        Connection connection = dataSource.getConnection();
        PreparedStatement stmt;
        stmt = connection.prepareStatement("SELECT Email FROM Nutzer WHERE Email= '"+ email +"'");
        stmt.closeOnCompletion();
        ResultSet resultSet = stmt.executeQuery();

        if(!resultSet.isBeforeFirst()) createNutzer(email,passwort);

        stmt = connection.prepareStatement("INSERT INTO Mitarbeiter VALUES('"+ email +"','"+ personalnummer +"')");
        stmt.closeOnCompletion();
        stmt.executeUpdate();

        String id = "";
        ResultSet key = stmt.getGeneratedKeys();
        if(key.next()) id = key.getString(1);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(id).build()).build();
    }


}