package com.dsousa.minhasfinancas.model.repository;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dsousa.minhasfinancas.model.entity.Usuario;

import junit.framework.Assert;


@RunWith(SpringRunner.class)
//Active le fichier application-test.properties comme ficher de test
@ActiveProfiles("test")
@DataJpaTest //crée une instance de base de donnée en debut de test et est supprimé à la fin
			 //crée une transaction ensuite fait un rollback à la fin du test

@AutoConfigureTestDatabase (replace = Replace.NONE) // pour ne pas superposer à mes configurations
/**
 * 
 * @author epifa
 * Tout ce qui est fait ici il y a un rollback ensuite
 */
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//açao/execuçao
		boolean  result = repository.existsByEmail("usuario@email.com");
		
		//verificaçao
		Assertions.assertThat(result).isTrue();
	}
	
	@Test 
	public void deveRetornarFalseQuandoNaoHouverUsuarioCadastradoComOEmail() {
		
		//cenario
		//Nao é necessario un cenario
		 //açao 
		 boolean result = repository.existsByEmail("usuario@email.com");
		 
		 //verificaçao
		 Assertions.assertThat(result).isFalse();
	}
    
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados () {
		
		//cenario
		Usuario usuario = criarUsuario();
		
		//açao
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verificaçao
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenario
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verificaçao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		//cenario
		//Nao necessario
		
		//verificaçao
		Optional<Usuario> result = repository.findByEmail("usuario@email.com");
		
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	public static Usuario criarUsuario () {
		
		return Usuario
				.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("senha")
				.build();
	}
}
