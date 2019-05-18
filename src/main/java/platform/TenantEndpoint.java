package platform;

import static platform.Tenant.PDV;
import static platform.Tenant.TENANT;
import static platform.Tenant.TARGET;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySetRef;

@Component
@Path("/api")
public class TenantEndpoint {

	private static final String CEP = null;
	@Autowired
	private Datastore datastore;

	/*
	 * Get a list of tenant PropertyBox in JSON.
	 */
	@GET
	@Path("/tenant")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PropertyBox> gettenant() {
		return datastore.query().target(TARGET).list(TENANT);
	}

	/*
	 * Get a TENANT PropertyBox in JSON.
	 */
	@GET
	@Path("/tenant/{CEP}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTENANT(@PathParam("CEP") Long CEP) {
		return datastore.query().target(TARGET).filter(CEP.eq(CEP)).findOne(TENANT).map(p -> Response.ok(p).build())
				.orElse(Response.status(Status.NOT_FOUND).build());
	}

	/*
	 * Create a TENANT. The @PropertySetRef must be used to declare the request
	 * PropertyBox property set.
	 */
	@POST
	@Path("/tenant")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addTENANT(@PropertySetRef(Tenant.class) PropertyBox TENANT) {
		// set CEP
		long nextCEP = datastore.query().target(TARGET).findOne(CEP.max()).orElse(0L) + 1;
		TENANT.setValue(CEP, nextCEP);
		// save
		datastore.save(TARGET, TENANT);
		return Response.created(URI.create("/api/tenant/" + nextCEP)).build();
	}

	/*
	 * Update a TENANT. The @PropertySetRef must be used to declare the request
	 * PropertyBox property set.
	 */
	@PUT
	@Path("/tenant/{CEP}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTENANT(@PropertySetRef(Tenant.class) PropertyBox TENANT) {
		return datastore.query().target(TARGET).filter(CEP.eq(TENANT.getValue(CEP))).findOne(TENANT).map(p -> {
			datastore.save(TARGET, TENANT);
			return Response.noContent().build();
		}).orElse(Response.status(Status.NOT_FOUND).build());
	}

	/*
	 * Delete a TENANT by CEP.
	 */
	@DELETE
	@Path("/tenant/{CEP}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteTENANT(@PathParam("CEP") Long CEP) {
		datastore.bulkDelete(TARGET).filter(CEP.eq(CEP)).execute();
		return Response.noContent().build();
	}

}
