package com.dsousa.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dsousa.minhasfinancas.exception.ErroAutenticacao;
import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.repository.UsuarioRepository;
import com.dsousa.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
    @Before
    public void setUp() {
    	//service = new UsuarioServiceImpl(repository);
    	//Mockito.spy(UsuarioServiceImpl.class);
    }
    
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		//açao
		service.validarEmail("email@email.com");
	}

	@Test( expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//açao
		service.validarEmail("email@email.com");
		
	}
	 @Test (expected = Test.None.class)
	    //expected =  Test.None.class : pour dire que on attend pas une exception
	    public void deveAutenticarUmUsuarioComSucesso() {
	    	
	    	//cenario
	    	String email ="email@email.com";
	    	String senha = "senha";
	    	
	    	Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
	    	Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
	    	//açao
	    	Usuario result = service.autenticar(email, senha);
	    	
	    	//Verificaçao
	    	Assertions.assertThat(result).isNotNull();
	    }
	    
	 @Test
	 public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		 
		 //cenario
		 Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		 //açao 
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha")); 
		 //verificaçao
		 Assertions.assertThat(exception)
		 .isInstanceOf(ErroAutenticacao.class)
		 .hasMessage("Usuario nao encontrado para o email informado.");
	 }
	 
	 @Test
	 public void deveLancerErroQuandoSenhaNaoBater() {
		 //cenario
		 String email ="email@email.com";
	     String senha = "senha";
	    	
	     Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		 Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		 //açao
		 Throwable exception = Assertions.catchThrowable( () -> service.autenticar(email, "123"));
		 //verificaçao
		 Assertions.assertThat(exception)
		 .isInstanceOf(ErroAutenticacao.class)
		 .hasMessage("Senha invalida.");
		
	 }
	 
	 @Test (expected = Test.None.class)
	 public void deveSalvarUmUsuario() {
		 //cenario
		 Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		 
		 Usuario usuario = Usuario.builder()
				 	.id(1l)
				 	.nome("nome")
				 	.email("email@email.com")
				 	.senha("senha").build();
		 Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		 //açao
		 Usuario usuarioSalvo =  repository.save(new Usuario());
		 //verificaçao
		 Assertions.assertThat(usuarioSalvo).isNotNull();
		 Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		 Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		 Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		 Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	 }
	 
	 @Test( expected = RegraNegocioException.class)
	 public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		 //cenario
		 String email = "email@email.com";
		 
		 Usuario usuario = Usuario.builder().email(email).build();
		 Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		 //açao
		 service.salvarUsuario(usuario);
		 //verificaçao 
		 //Pour vérifier le nombre de fois que la méthode Mockito a été appelé
		 Mockito.verify(repository, Mockito.never()).save(usuario);
	 }
}
