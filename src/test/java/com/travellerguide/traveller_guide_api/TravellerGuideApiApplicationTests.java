package com.travellerguide.traveller_guide_api;

import com.travellerguide.traveller_guide_api.domain.country.Country;
import com.travellerguide.traveller_guide_api.domain.country.CountryTranslation;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryRepository;
import com.travellerguide.traveller_guide_api.infrastructure.persistence.country.CountryTranslationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("h2")
@SpringBootTest
class TravellerGuideApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private CountryTranslationRepository countryTranslationRepository;

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

	@Test
	void openApiDocsAllowMissingApiKey() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk());
	}

	@Test
	void localizedCountryRouteReturnsTranslatedPayload() throws Exception {
		Country country = new Country();
		country.setName("Austria");
		country.setSlug("austria");
		country = countryRepository.save(country);

		CountryTranslation translation = new CountryTranslation();
		translation.setCountry(country);
		translation.setLocale("de");
		translation.setName("Osterreich");
		translation.setSlug("oesterreich");
		translation.setDescription("Deutschsprachige Beschreibung");
		countryTranslationRepository.save(translation);

		mockMvc.perform(get("/v1/api/de/countries/oesterreich")
						.header("X-API-Key", "local-dev-api-key"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Osterreich"))
				.andExpect(jsonPath("$.slug").value("oesterreich"))
				.andExpect(jsonPath("$.description").value("Deutschsprachige Beschreibung"));
	}
}
