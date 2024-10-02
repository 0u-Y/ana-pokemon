package kr.anacnu.pokemonbe;

import kr.anacnu.pokemonbe.jwt.JwtToken;
import kr.anacnu.pokemonbe.jwt.LoginDto;
import kr.anacnu.pokemonbe.pokemon.PokemonDto;
import kr.anacnu.pokemonbe.pokemon.PokemonService;
import kr.anacnu.pokemonbe.pokemon_type.PokemonTypeService;
import kr.anacnu.pokemonbe.utils.CsvUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import kr.anacnu.pokemonbe.jwt.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;


import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@SpringBootTest
@AutoConfigureMockMvc
class PokemonBeApplicationTests {
	@Autowired
	PokemonService pokemonService;
	@Autowired
	PokemonTypeService pokemonTypeService;

	private final String pathPokemonData = "../pokemon_data.csv";
	private final String pathPokemonTypeData = "../pokemon_type_data.csv";

	@Test
	void addTypes() {
		var typesData = CsvUtil.readCsv(pathPokemonTypeData);
		if (typesData.size() < 2) {
			System.out.println("No data found in " + pathPokemonTypeData);
			return;
		}

		for (int i = 1; i < typesData.size(); i++) {
			var typeData = typesData.get(i);
			System.out.println(pokemonTypeService.addOrUpdatePokemonType(typeData[0], typeData[1]));
		}

		System.out.println(pokemonTypeService.getPokemonsByTypeName("풀"));
	}

	@Test
	void addPokemons() {
		var pokemonsData = CsvUtil.readCsv(pathPokemonData);
		if (pokemonsData.size() < 2) {
			System.out.println("No data found in " + pathPokemonData);
			return;
		}

		for (int i = 1; i < pokemonsData.size(); i++) {
			var pokemonData = pokemonsData.get(i);

			var types = Arrays.stream(pokemonData[4].split("\\|")).toList();
			var dto = PokemonDto.builder()
					.pokedexNum(Long.parseLong(pokemonData[0]))
					.name(pokemonData[1])
					.types(types)
					.height(Float.parseFloat(pokemonData[2]))
					.weight(Float.parseFloat(pokemonData[3]))
					.imageUrl(pokemonData[5])
					.build();

			System.out.println(pokemonService.addPokemon(dto));
		}
	}



	@Autowired
	private WebApplicationContext context;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;


	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}


	@Test
	@DisplayName("로그인 성공 시 jwt 토큰 발급")
	void testLogin() throws Exception {
		LoginDto loginDto = new LoginDto("ana202302576", "202302576");
		/**
		 * Member DB에 Id : "ana202302576" Password : "202302576"을 PasswordEncoderExample 클래스에서 인코딩을 해 저장 후 테스트
		 */

		String response = mockMvc.perform(post("/sign-in")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(loginDto)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken", notNullValue()))
				.andReturn()
				.getResponse()
				.getContentAsString();


		JwtToken jwtToken = new com.fasterxml.jackson.databind.ObjectMapper().readValue(response, JwtToken.class);

		boolean isValid = jwtTokenProvider.validateToken(jwtToken.getAccessToken());
		assert isValid;
	}

	private static String asJsonString(final Object obj) {
		try {
			return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}












}

