
package br.com.estoqueflow.model;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public class Produto {

    private final Long id;
    private final String codigo;
    private final String nome;
    private final String categoria;
    private final BigDecimal preco;
    private final int quantidade;
    private final int estoqueMinimo;
    private final boolean ativo;

    public Produto(
            Long id,
            String codigo,
            String nome,
            String categoria,
            BigDecimal preco,
            int quantidade,
            int estoqueMinimo,
            boolean ativo
    ) {
        if (quantidade < 0 || estoqueMinimo < 0) {
            throw new IllegalArgumentException(
                    "Quantidades não podem ser negativas."
            );
        }

        Objects.requireNonNull(preco, "Preço obrigatório.");

        if (preco.signum() < 0) {
            throw new IllegalArgumentException(
                    "O preço não pode ser negativo."
            );
        }

        this.id = id;
        this.codigo = validarTexto(codigo, "Código")
                .toUpperCase(Locale.ROOT);
        this.nome = validarTexto(nome, "Nome");
        this.categoria = validarTexto(categoria, "Categoria");
        this.preco = preco;
        this.quantidade = quantidade;
        this.estoqueMinimo = estoqueMinimo;
        this.ativo = ativo;
    }

    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " é obrigatório."
            );
        }
        return texto.strip();
    }

    public boolean estoqueBaixo() {
        return quantidade <= estoqueMinimo;
    }

    public BigDecimal getValorTotal() {
        return preco.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public int getEstoqueMinimo() { return estoqueMinimo; }
    public boolean isAtivo() { return ativo; }
}
