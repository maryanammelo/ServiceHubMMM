package com.servicehub.servicehub_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.servicehub.servicehub_api.model.Servico;
import com.servicehub.servicehub_api.repository.ServicoRepository;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public Servico criar(Servico servico) {
        servico.setId(null);
        return servicoRepository.save(servico);
    }

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Serviço não encontrado"
                ));
    }

    public Servico atualizar(Long id, Servico dadosAtualizados) {
        Servico servicoExistente = buscarPorId(id);

        servicoExistente.setNome(dadosAtualizados.getNome());
        servicoExistente.setDescricao(dadosAtualizados.getDescricao());
        servicoExistente.setCategoria(dadosAtualizados.getCategoria());
        servicoExistente.setPreco(dadosAtualizados.getPreco());
        servicoExistente.setDisponivel(dadosAtualizados.getDisponivel());

        return servicoRepository.save(servicoExistente);
    }

    public void excluir(Long id) {
        Servico servicoExistente = buscarPorId(id);
        servicoRepository.delete(servicoExistente);
    }
}