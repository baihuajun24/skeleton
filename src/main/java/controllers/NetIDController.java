package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

// For a Java class to be eligible to receive ANY requests
// it must be annotated with at least @Path
@Path("/netid")
public class NetIDController {

    // You can specify additional @Path steps; they will be relative
    // to the @Path defined at the class level
    @GET
    public String netid() {
        return "hb364";
    }
}