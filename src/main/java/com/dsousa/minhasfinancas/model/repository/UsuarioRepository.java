package com.dsousa.minhasfinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dsousa.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	// ici tout est generé par jpa. Pas besoin d'écrire une requête sql
	//comme l'objet Usuario a un attribut email il saura recupérer cette donnée en base de donnés
	//Optional<Usuario> findByEmail (String email);
	//Autres exemples
	//Optional<Usuario> findByEmailAndNome (String email, String nome);
	
	//Pour verifier si en email existe déjà en utilisant la convention Spring data
	boolean existsByEmail(String email);
	
	Optional<Usuario> findByEmail(String email);

}
