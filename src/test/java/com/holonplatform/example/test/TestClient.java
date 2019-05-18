package com.holonplatform.example.test;

import static platform.Tenant.CEP;
import static platform.Tenant.TENANT;
import static platform.Tenant.PDV;

import java.net.URI;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.http.rest.RequestEntity;
import com.holonplatform.http.rest.RestClient;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestClient {

	@LocalServerPort
	private int serverPort;

	@Test
	public void testMultiTenancy() {

		RestClient client = RestClient.forTarget("http://localhost:" + serverPort + "/api/");

		final PropertyBox TENANT1 = PropertyBox.builder(TENANT).set(PDV, "TENANT 1")
				.set("tenant1-TENANT1").build();

		// [tenant1] add using POST
		URI location = client.request().path("TENANTs") //
				.header("X-TENANT-ID", "tenant1") // set custom tenant header
				.postForLocation(RequestEntity.json(TENANT1)).orElseThrow(() -> new RuntimeException("Missing URI"));

		// [tenant1] get the TENANT
		PropertyBox created = client.request().target(location) //
				.header("X-TENANT-ID", "tenant1") // set custom tenant header
				.propertySet(TENANT).getForEntity(PropertyBox.class)
				.orElseThrow(() -> new RuntimeException("Missing TENANT"));

		Assert.assertEquals("tenant1-TENANT1", created.getValue());

		// [tenant2] get all TENANTs
		List<PropertyBox> values = client.request().path("TENANTs") //
				.header("X-TENANT-ID", "tenant2") // set custom tenant header
				.propertySet(TENANT).getAsList(PropertyBox.class);

		Assert.assertEquals(1, values.size());

		final PropertyBox TENANT2 = PropertyBox.builder(TENANT).set(DESCRIPTION, "TENANT 1")
				.set("tenant2-TENANT1").build();

		// [tenant2] add using POST
		location = client.request().path("TENANTs") //
				.header("X-TENANT-ID", "tenant2") // set custom tenant header
				.postForLocation(RequestEntity.json(TENANT2)).orElseThrow(() -> new RuntimeException("Missing URI"));

		// [tenant2] get the TENANT
		created = client.request().target(location) //
				.header("X-TENANT-ID", "tenant2") // set custom tenant header
				.propertySet(TENANT).getForEntity(PropertyBox.class)
				.orElseThrow(() -> new RuntimeException("Missing TENANT"));

		Assert.assertEquals("tenant2-TENANT1", created.getValue());

	}

}
