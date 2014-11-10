package org.springframework.data.rest.webmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.data.rest.webmvc.jpa.JpaRepositoryConfig;
import org.springframework.data.rest.webmvc.jpa.Person;
import org.springframework.data.rest.webmvc.jpa.PersonRepository;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.hateoas.core.JsonPathLinkDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Verify existence of more than one "links" link with classic format.
 * @author Greg Turnquist
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {JpaRepositoryConfig.class, DuplicateLinkListingTests.ClassicConfiguration.class,
		DuplicateLinkListingTests.Config.class })
public class DuplicateLinkListingTests {

	@Autowired WebApplicationContext context;
	@Autowired LinkDiscoverers discoverers;
	@Autowired PersonRepository personRepository;

	private static MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

	protected TestMvcClient testMvcClient;
	protected MockMvc mvc;

	@Configuration
	static class Config {

		@Bean
		public LinkDiscoverer classicLinkDiscover() {
			return new JsonPathLinkDiscoverer("$.links[?(@.rel == '%s')].href",
					MEDIA_TYPE);
		}
	}

	@Configuration
	static class ClassicConfiguration extends RepositoryRestMvcConfiguration {

		@Override
		protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
			config.setDefaultMediaType(MEDIA_TYPE).useHalAsDefaultJsonMediaType(false);
		}
	}

	@Before
	public void setUp() {

		mvc = MockMvcBuilders.webAppContextSetup(context).//
				defaultRequest(get("/")).build();
		testMvcClient = new TestMvcClient(mvc, discoverers);

		personRepository.save(new Person("Frodo", "Baggins"));
	}

	@Test
	public void testBasics() throws Exception {

//		Link peopleLink = testMvcClient.discoverUnique("people");
//		ResultActions peopleActions = testMvcClient.follow(peopleLink);
//
//		System.out.println(peopleActions.andReturn().getResponse().getContentAsString());

		ResultActions frodoActions = testMvcClient.follow("/people/1");

		System.out.println(frodoActions.andReturn().getResponse().getContentAsString());
	}
}
