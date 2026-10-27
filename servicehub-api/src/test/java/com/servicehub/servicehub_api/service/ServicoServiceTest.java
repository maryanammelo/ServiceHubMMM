package com.servicehub.servicehub_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import com.servicehub.servicehub_api.model.Servico;
import com.servicehub.servicehub_api.repository.ServicoRepository;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ServicoServiceTest {
    @Mock ServicoRepository repository;
    @InjectMocks ServicoService service;
    @Test void deveCriarZerandoId() { var s=servico(); s.setId(99L); when(repository.save(s)).thenReturn(s); service.criar(s); assertNull(s.getId()); verify(repository).save(s); }
    @Test void deveAtualizarSemTrocarRegistro() { var atual=servico(); atual.setId(1L); var dados=servico(); dados.setNome("Novo nome"); when(repository.findById(1L)).thenReturn(Optional.of(atual)); when(repository.save(atual)).thenReturn(atual); assertSame(atual,service.atualizar(1L,dados)); assertEquals("Novo nome",atual.getNome()); }
    @Test void deveLancar404QuandoNaoExiste() { when(repository.findById(1L)).thenReturn(Optional.empty()); assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.buscarPorId(1L)); }
    @Test void deveExcluirRegistroEncontrado() {
        var s = servico(); when(repository.findById(1L)).thenReturn(Optional.of(s));
        service.excluir(1L); verify(repository).delete(s);
    }
    @Test void naoDeveSalvarAtualizacaoDeRegistroInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        var error = assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> service.atualizar(1L, servico()));
        assertEquals(404, error.getStatusCode().value()); verify(repository, never()).save(any());
    }
    @Test void naoDeveExcluirRegistroInexistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> service.excluir(1L));
        verify(repository, never()).delete(any());
    }
    private Servico servico() { var s=new Servico(); s.setNome("Serviço"); s.setDescricao("Descrição"); s.setCategoria("Categoria"); s.setPreco(new BigDecimal("10.00")); s.setDisponivel(true); return s; }
}
