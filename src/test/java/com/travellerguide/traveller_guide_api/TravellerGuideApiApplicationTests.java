package com.travellerguide.traveller_guide_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("h2")
@SpringBootTest
class TravellerGuideApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void apiRoutesRejectMissingApiKey() throws Exception {
		mockMvc.perform(get("/v1/api/countries"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void apiRoutesAcceptConfiguredApiKey() throws Exception {
		mockMvc.perform(get("/v1/api/countries")
						.header("X-API-Key", "local-dev-api-key"))
				.andExpect(status().isOk());
	}
}
