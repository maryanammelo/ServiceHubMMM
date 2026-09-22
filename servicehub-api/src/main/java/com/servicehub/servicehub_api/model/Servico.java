package com.servicehub.servicehub_api.model;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "servicos")
@Schema(description = "Representa um serviço oferecido na plataforma ServiceHub")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do serviço", example = "1")
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve possuir entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(description = "Nome do serviço", example = "Manutenção de computadores")
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 500, message = "A descrição deve possuir no máximo 500 caracteres")
    @Column(nullable = false, length = 500)
    @Schema(
        description = "Descrição detalhada do serviço",
        example = "Formatação, limpeza e manutenção de computadores"
    )
    private String descricao;

    @NotBlank(message = "A categoria é obrigatória")
    @Column(nullable = false, length = 80)
    @Schema(description = "Categoria do serviço", example = "Tecnologia")
    private String categoria;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Preço do serviço", example = "150.00")
    private BigDecimal preco;

    @NotNull(message = "A disponibilidade é obrigatória")
    @Column(nullable = false)
    @Schema(description = "Indica se o serviço está disponível", example = "true")
    private Boolean disponivel;

    public Servico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }
}