package com.dsousa.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import com.dsousa.minhasfinancas.exception.RegraNegocioException;
import com.dsousa.minhasfinancas.model.entity.Lancamento;
import com.dsousa.minhasfinancas.model.entity.Usuario;
import com.dsousa.minhasfinancas.model.enums.StatusLancamento;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepository;
import com.dsousa.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.dsousa.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test 
	public void deveSalvarUmLancamento () {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		//execuçao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificaçao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		
		// execuçao e verificaçao
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
	
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test 
	public void deveAtualizarUmLancamento () {
		
		//cenario
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execuçao
		service.atualizar(lancamentoSalvo);
		//verificaçao que a metodo foi chamado pelo menos uma vez
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamento);
		
		// execuçao e verificaçao
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamento), NullPointerException.class);
	
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenario
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1l);
	    
	    //execuçao
	    service.deletar(lancamento);
	    
	    //verificaçao
	    Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//cenario
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    
	    //execuçao
	    Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);
	    //verificaçao que nunca chamou o metodo delete
	    Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamento() {
		//cenario
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1l);
	    
	    List<Lancamento> lista = Arrays.asList(lancamento);
	    //voir pourquoi ça ne marche pas 
	    Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
	    
	    //execuçao 
	    List<Lancamento> resultado =  service.buscar(lancamento);
	    
	    //verificaçao
	    Assertions.assertThat(resultado)
	    .isNotEmpty()
	    .hasSize(1)
	    .contains(lancamento);
	    
	    
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		
		//cenario
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(1l);
	    lancamento.setStatus(StatusLancamento.PENDENTE);
	    
	    StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
	    Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
	    
	    //execuçao
	    service.atualizarStatus(lancamento, novoStatus);
	    
	    //verificaçoes
	    Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
	    Mockito.verify(service).atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		//cenario
		Long id = 1l;
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(id);
	    
	    Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
	    
	    //execuçao
	    Optional<Lancamento> resultado = service.obterPorId(id);
	    
	    //verificaçoes
	    Assertions.assertThat(resultado.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		//cenario
		Long id = 1l;
	    Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
	    lancamento.setId(id);
	    
	    Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
	    
	    //execuçao
	    Optional<Lancamento> resultado = service.obterPorId(id);
	    
	    //verificaçoes
	    Assertions.assertThat(resultado.isPresent()).isFalse();
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descriçao valida.");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descriçao valida.");
		
		lancamento.setDescricao("Salario");
		
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês valido.");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês valido.");
		
		lancamento.setMes(13);
		
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês valido.");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");
		
		lancamento.setAno(0);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");
		
		lancamento.setAno(202);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");
		
		lancamento.setAno(2020);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario.");
		
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario.");
		
		lancamento.getUsuario().setId(1l);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor valido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor valido.");
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Tipo de Lancamento.");
		
		
	}
}
