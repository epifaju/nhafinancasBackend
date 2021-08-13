package com.dsousa.minhasfinancas.api.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.dsousa.minhasfinancas.api.dto.UsuarioDTO;
import com.dsousa.minhasfinancas.exception.ErroAutenticacao;
import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.service.LancamentoService;
import com.dsousa.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAtenticarUmUsuario() throws Exception {
		
		
		//cenario
		String email = "usuario@email.com";
		String senha = "123";
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		//Usuario connecté
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
	
		Mockito.when( service.autenticar(email, senha)).thenReturn(usuario);
	
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execuçao e verificaçao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
														.post(API.concat("/autenticar"))
														.accept(JSON)
														.contentType(JSON)
														.content(json);
		
		mvc.perform(request)
					.andExpect( MockMvcResultMatchers.status().isOk())
					.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
					.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
					.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
					//.andExpect( MockMvcResultMatchers.jsonPath("senha").value(usuario.getSenha()));
	}
	
	@Test
	public void deveRetornarBadREquestAoObterErroDeAutenticacao() throws Exception {
		
		
		//cenario
		String email = "usuario@email.com";
		String senha = "123";
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		//Usuario connecté
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
	
		Mockito.when( service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		// Transforma um objecto en String Json
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execuçao e verificaçao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
														.post(API.concat("/autenticar"))
														.accept(JSON)
														.contentType(JSON)
														.content(json);
		mvc.perform(request)
					.andExpect( MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		
		//cenario
		String email = "usuario@email.com";
		String senha = "123";
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		//Usuario connecté
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
	
		Mockito.when( service.salvarUsuario(Mockito.any(Usuario.class)) ).thenReturn(usuario);
	
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execuçao e verificaçao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
														.post(API)
														.accept(JSON)
														.contentType(JSON)
														.content(json);
		
		mvc.perform(request)
					.andExpect( MockMvcResultMatchers.status().isCreated())
					.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
					.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
					.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}
	
	@Test
	public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
		
		//cenario
		String email = "usuario@email.com";
		String senha = "123";
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		Mockito.when( service.salvarUsuario(Mockito.any(Usuario.class)) ).thenThrow(RegraNegocioException.class);
	
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execuçao e verificaçao
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
														.post(API)
														.accept(JSON)
														.contentType(JSON)
														.content(json);
		
		mvc.perform(request)
					.andExpect( MockMvcResultMatchers.status().isBadRequest());
	}
}

