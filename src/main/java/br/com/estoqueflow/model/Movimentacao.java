
package br.com.estoqueflow.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Movimentacao {

    private final Long id;
    private final long produtoId;
    private final TipoMovimentacao tipo;
    private final int quantidade;
    private final LocalDateTime dataHora;
    private final String observacao;

    public Movimentacao(
            Long id,
            long produtoId,
            TipoMovimentacao tipo,
            int quantidade,
            LocalDateTime dataHora,
            String observacao
    ) {
        if (produtoId <= 0) {
            throw new IllegalArgumentException(
                    "Produto inválido."
            );
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }

        this.id = id;
        this.produtoId = produtoId;
        this.tipo = Objects.requireNonNull(tipo);
        this.quantidade = quantidade;
        this.dataHora = Objects.requireNonNull(dataHora);
        this.observacao = observacao == null
                ? ""
                : observacao.strip();
    }

    public Long getId() { return id; }
    public long getProdutoId() { return produtoId; }
    public TipoMovimentacao getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getObservacao() { return observacao; }
}
