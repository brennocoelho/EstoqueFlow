package br.com.estoqueflow.view;

import br.com.estoqueflow.controller.EstoqueController;
import br.com.estoqueflow.model.Movimentacao;
import br.com.estoqueflow.model.Produto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TelaPrincipal extends JFrame {
    private static final Color BG = new Color(15, 23, 42);
    private static final Color PANEL = new Color(30, 41, 59);
    private static final Color PANEL2 = new Color(45, 58, 79);
    private static final Color FG = new Color(226, 232, 240);
    private static final Color MUTED = new Color(148, 163, 184);
    private static final Color BLUE = new Color(56, 189, 248);
    private static final Color GREEN = new Color(52, 211, 153);
    private static final Color RED = new Color(251, 113, 133);
    private static final NumberFormat MONEY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final EstoqueController controller;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private final JLabel pageTitle = label("Visão geral", 26, FG);
    private final JTextField search = field("");
    private final DefaultTableModel productModel = model("ID", "Código", "Produto", "Categoria", "Preço", "Saldo", "Mínimo", "Status");
    private final JTable productTable = table(productModel);
    private final DefaultTableModel movementModel = model("Data", "Produto", "Tipo", "Quantidade", "Observação");
    private final JTable movementTable = table(movementModel);
    private final DefaultTableModel alertModel = model("Código", "Produto", "Categoria", "Saldo", "Mínimo");
    private final JTable alertTable = table(alertModel);

    public TelaPrincipal() {
        this(new EstoqueController());
    }

    public TelaPrincipal(EstoqueController controller) {
        super("EstoqueFlow • Gestão de estoque");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        buildUI();
        refreshAll();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(sidebar(), BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(26, 28, 22, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel headings = new JPanel();
        headings.setOpaque(false);
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));
        headings.add(pageTitle);
        headings.add(Box.createVerticalStrut(5));
        headings.add(label("Controle inteligente do seu estoque", 13, MUTED));
        header.add(headings, BorderLayout.WEST);
        JButton refresh = button("Atualizar dados", BLUE);
        refresh.addActionListener(e -> refreshAll());
        header.add(refresh, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        pages.setOpaque(false);
        pages.add(dashboard(), "home");
        pages.add(productsPage(), "products");
        pages.add(movementsPage(), "movements");
        pages.add(alertsPage(), "alerts");
        content.add(pages, BorderLayout.CENTER);
        content.add(label("EstoqueFlow • Banco de dados H2 local • Java 21", 11, MUTED), BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
    }

    private JPanel sidebar() {
        JPanel side = new JPanel();
        side.setBackground(new Color(10, 16, 30));
        side.setPreferredSize(new Dimension(230, 0));
        side.setBorder(new EmptyBorder(26, 17, 22, 17));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("EstoqueFlow", new BrandIcon(), SwingConstants.LEFT);
        brand.setFont(new Font("SansSerif", Font.BOLD, 20));
        brand.setForeground(FG);
        brand.setIconTextGap(11);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        side.add(brand);
        side.add(Box.createVerticalStrut(8));
        side.add(label("GESTÃO DE ESTOQUE", 10, MUTED));
        side.add(Box.createVerticalStrut(38));
        side.add(label("MENU PRINCIPAL", 10, MUTED));
        side.add(Box.createVerticalStrut(12));
        nav(side, "home", "Visão geral");
        nav(side, "products", "Produtos");
        nav(side, "movements", "Movimentações");
        nav(side, "alerts", "Estoque baixo");
        side.add(Box.createVerticalGlue());
        side.add(label("VERSÃO 1.0 • LOCAL", 10, BLUE));
        return side;
    }

    private void nav(JPanel side, String key, String title) {
        JButton b = new JButton(title, new FlowIcon(key, BLUE));
        b.setMaximumSize(new Dimension(210, 46));
        b.setPreferredSize(new Dimension(196, 46));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setIconTextGap(14);
        b.setForeground(FG);
        b.setBackground(new Color(10, 16, 30));
        b.setFont(new Font("SansSerif", Font.PLAIN, 14));
        b.setBorder(new EmptyBorder(12, 12, 12, 8));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setToolTipText(title);
        b.getAccessibleContext().setAccessibleName(title);
        b.addActionListener(e -> {
            cardLayout.show(pages, key);
            pageTitle.setText(title);
            if (key.equals("movements")) loadMovements();
            if (key.equals("alerts")) loadAlerts();
        });
        side.add(b);
        side.add(Box.createVerticalStrut(5));
    }

    /**
     * Ícones vetoriais Java2D, sem fontes externas ou conexão com a internet.
     */
    private static final class FlowIcon implements Icon {
        private final String type;
        private final Color color;

        private FlowIcon(String type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override public int getIconWidth() { return 20; }
        @Override public int getIconHeight() { return 20; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            switch (type) {
                case "home" -> {
                    g.drawRoundRect(2, 2, 7, 7, 2, 2);
                    g.drawRoundRect(12, 2, 6, 7, 2, 2);
                    g.drawRoundRect(2, 12, 7, 6, 2, 2);
                    g.drawRoundRect(12, 12, 6, 6, 2, 2);
                }
                case "products" -> {
                    g.drawLine(3, 6, 10, 2);
                    g.drawLine(10, 2, 17, 6);
                    g.drawLine(3, 6, 10, 10);
                    g.drawLine(17, 6, 10, 10);
                    g.drawLine(3, 6, 3, 15);
                    g.drawLine(17, 6, 17, 15);
                    g.drawLine(3, 15, 10, 19);
                    g.drawLine(17, 15, 10, 19);
                    g.drawLine(10, 10, 10, 19);
                }
                case "movements" -> {
                    g.drawLine(2, 6, 17, 6);
                    g.drawLine(13, 2, 17, 6);
                    g.drawLine(13, 10, 17, 6);
                    g.drawLine(18, 14, 3, 14);
                    g.drawLine(7, 10, 3, 14);
                    g.drawLine(7, 18, 3, 14);
                }
                case "alerts" -> {
                    g.drawLine(10, 2, 19, 18);
                    g.drawLine(19, 18, 1, 18);
                    g.drawLine(1, 18, 10, 2);
                    g.drawLine(10, 7, 10, 12);
                    g.fillOval(9, 15, 2, 2);
                }
                default -> g.drawOval(3, 3, 14, 14);
            }
            g.dispose();
        }
    }

    /**
     * Ícone vetorial da identidade visual do EstoqueFlow.
     */
    private static final class BrandIcon implements Icon {
        @Override public int getIconWidth() { return 30; }
        @Override public int getIconHeight() { return 30; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g.setPaint(new GradientPaint(2, 2, new Color(56, 189, 248),
                    28, 28, new Color(14, 165, 233)));
            g.fillRoundRect(1, 1, 28, 28, 9, 9);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.7f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g.drawLine(7, 11, 15, 7);
            g.drawLine(15, 7, 23, 11);
            g.drawLine(7, 11, 15, 15);
            g.drawLine(23, 11, 15, 15);
            g.drawLine(7, 11, 7, 20);
            g.drawLine(23, 11, 23, 20);
            g.drawLine(7, 20, 15, 24);
            g.drawLine(23, 20, 15, 24);
            g.drawLine(15, 15, 15, 24);

            g.dispose();
        }
    }

    private JPanel dashboard() {
        JPanel p = transparent();
        p.setLayout(new BorderLayout(0, 18));
        JPanel metrics = new JPanel(new GridLayout(1, 4, 13, 0));
        metrics.setOpaque(false);
        metrics.add(metric("PRODUTOS CADASTRADOS", "0", BLUE));
        metrics.add(metric("UNIDADES EM ESTOQUE", "0", GREEN));
        metrics.add(metric("VALOR EM ESTOQUE", "R$ 0,00", BLUE));
        metrics.add(metric("ESTOQUE BAIXO", "0", RED));
        p.add(metrics, BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 13, 0));
        lower.setOpaque(false);
        JPanel recent = panel();
        recent.setLayout(new BorderLayout(0, 12));
        recent.add(label("Movimentações recentes", 16, FG), BorderLayout.NORTH);
        DefaultTableModel recentModel = model("Data", "Produto", "Tipo", "Qtd.");
        JTable recentTable = table(recentModel);
        recent.add(new JScrollPane(recentTable), BorderLayout.CENTER);
        recent.putClientProperty("recentModel", recentModel);

        JPanel attention = panel();
        attention.setLayout(new BorderLayout(0, 12));
        attention.add(label("Atenção ao estoque", 16, FG), BorderLayout.NORTH);
        attention.add(label("<html>Produtos no limite ou abaixo do estoque mínimo aparecem na área de alertas.<br><br>Registre entradas para recompor o saldo e evitar rupturas.</html>", 13, MUTED), BorderLayout.CENTER);
        JButton alerts = button("Ver alertas de estoque", RED);
        alerts.addActionListener(e -> showPage("alerts", "Estoque baixo"));
        attention.add(alerts, BorderLayout.SOUTH);
        lower.add(recent);
        lower.add(attention);
        p.add(lower, BorderLayout.CENTER);
        p.putClientProperty("metrics", metrics);
        return p;
    }

    private JPanel productsPage() {
        JPanel p = transparent();
        p.setLayout(new BorderLayout(0, 14));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        search.setPreferredSize(new Dimension(250, 38));
        search.setToolTipText("Pesquisar por código, nome ou categoria");
        JButton find = button("Pesquisar", BLUE);
        find.addActionListener(e -> loadProducts());
        search.addActionListener(e -> loadProducts());
        JButton add = button("+ Novo produto", GREEN);
        add.addActionListener(e -> productDialog(null));
        JButton edit = button("Editar", PANEL2);
        edit.addActionListener(e -> editSelected());
        JButton toggle = button("Ativar/desativar", PANEL2);
        toggle.addActionListener(e -> toggleSelected());
        JButton in = button("Entrada", GREEN);
        in.addActionListener(e -> movementDialog(true));
        JButton out = button("Saída", RED);
        out.addActionListener(e -> movementDialog(false));
        actions.add(search); actions.add(find); actions.add(add);
        actions.add(edit); actions.add(toggle); actions.add(in); actions.add(out);
        p.add(actions, BorderLayout.NORTH);
        p.add(scroll(productTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel movementsPage() {
        JPanel p = transparent();
        p.setLayout(new BorderLayout(0, 12));
        p.add(label("Histórico de entradas e saídas registradas.", 13, MUTED), BorderLayout.NORTH);
        p.add(scroll(movementTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel alertsPage() {
        JPanel p = transparent();
        p.setLayout(new BorderLayout(0, 12));
        p.add(label("Produtos ativos com saldo menor ou igual ao estoque mínimo.", 13, MUTED), BorderLayout.NORTH);
        p.add(scroll(alertTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel metric(String title, String value, Color accent) {
        JPanel p = panel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(label(title, 10, MUTED));
        p.add(Box.createVerticalStrut(13));
        JLabel v = label(value, 24, accent);
        v.setFont(new Font("SansSerif", Font.BOLD, 24));
        p.add(v);
        p.putClientProperty("value", v);
        return p;
    }

    private JPanel transparent() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }

    private JPanel panel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        return p;
    }

    private static JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("SansSerif", Font.PLAIN, size));
        return l;
    }

    private JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setBackground(color);
        b.setForeground(new Color(8, 15, 28));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBorder(new EmptyBorder(10, 13, 10, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTextField field(String text) {
        JTextField f = new JTextField(text);
        f.setBackground(PANEL2);
        f.setForeground(FG);
        f.setCaretColor(FG);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105)),
                new EmptyBorder(8, 10, 8, 10)));
        return f;
    }

    private static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    private static JTable table(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(34);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.setForeground(FG);
        t.setBackground(PANEL);
        t.setGridColor(new Color(51, 65, 85));
        t.setSelectionBackground(new Color(30, 64, 91));
        t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setBackground(PANEL2);
        t.getTableHeader().setForeground(FG);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        return t;
    }

    private JScrollPane scroll(JTable t) {
        JScrollPane s = new JScrollPane(t);
        s.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        s.getViewport().setBackground(PANEL);
        return s;
    }

    private void showPage(String key, String title) {
        cardLayout.show(pages, key);
        pageTitle.setText(title);
        if (key.equals("alerts")) loadAlerts();
    }

    private void refreshAll() {
        loadProducts();
        loadMovements();
        loadAlerts();
        updateDashboard();
    }

    private List<Produto> allProducts() throws Exception {
        return controller.listarProdutos();
    }

    private void loadProducts() {
        try {
            productModel.setRowCount(0);
            for (Produto p : controller.pesquisarProdutos(search.getText())) {
                productModel.addRow(new Object[]{p.getId(), p.getCodigo(), p.getNome(),
                        p.getCategoria(), MONEY.format(p.getPreco()), p.getQuantidade(),
                        p.getEstoqueMinimo(), p.isAtivo() ? "Ativo" : "Inativo"});
            }
        } catch (Exception e) { error("Não foi possível carregar os produtos.", e); }
    }

    private void loadMovements() {
        try {
            movementModel.setRowCount(0);
            for (EstoqueController.MovimentacaoDetalhe item : controller.listarHistoricoCompleto()) {
                Movimentacao m = item.movimentacao();
                movementModel.addRow(new Object[]{m.getDataHora().format(DATE),
                        item.nomeProduto(), m.getTipo(), m.getQuantidade(), m.getObservacao()});
            }
        } catch (Exception e) { error("Não foi possível carregar o histórico.", e); }
    }

    private void loadAlerts() {
        try {
            alertModel.setRowCount(0);
            for (Produto p : allProducts()) {
                if (p.isAtivo() && p.estoqueBaixo()) {
                    alertModel.addRow(new Object[]{p.getCodigo(), p.getNome(),
                            p.getCategoria(), p.getQuantidade(), p.getEstoqueMinimo()});
                }
            }
        } catch (Exception e) { error("Não foi possível carregar os alertas.", e); }
    }

    private void updateDashboard() {
        try {
            List<Produto> list = allProducts();
            int units = 0, low = 0;
            BigDecimal total = BigDecimal.ZERO;
            for (Produto p : list) if (p.isAtivo()) {
                units += p.getQuantidade();
                total = total.add(p.getValorTotal());
                if (p.estoqueBaixo()) low++;
            }
            JPanel dash = (JPanel) pages.getComponent(0);
            JPanel metrics = (JPanel) dash.getClientProperty("metrics");
            setMetric(metrics, 0, String.valueOf(list.size()));
            setMetric(metrics, 1, String.valueOf(units));
            setMetric(metrics, 2, MONEY.format(total));
            setMetric(metrics, 3, String.valueOf(low));
            JPanel lower = (JPanel) dash.getComponent(1);
            JPanel recentPanel = (JPanel) lower.getComponent(0);
            DefaultTableModel recent = (DefaultTableModel) recentPanel.getClientProperty("recentModel");
            recent.setRowCount(0);
            List<EstoqueController.MovimentacaoDetalhe> rows =
                    controller.listarHistoricoCompleto();
            for (int i = 0; i < Math.min(6, rows.size()); i++) {
                EstoqueController.MovimentacaoDetalhe item = rows.get(i);
                Movimentacao m = item.movimentacao();
                recent.addRow(new Object[]{m.getDataHora().format(DATE),
                        item.nomeProduto(), m.getTipo(), m.getQuantidade()});
            }
        } catch (Exception e) { error("Não foi possível atualizar o painel.", e); }
    }

    private void setMetric(JPanel metrics, int index, String value) {
        JLabel l = (JLabel) ((JPanel) metrics.getComponent(index)).getClientProperty("value");
        l.setText(value);
    }

    private Produto selectedProduct() throws Exception {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return null;
        }
        long id = ((Number) productModel.getValueAt(row, 0)).longValue();
        for (Produto p : allProducts()) if (p.getId() == id) return p;
        return null;
    }

    private void editSelected() {
        try {
            Produto p = selectedProduct();
            if (p != null) productDialog(p);
        } catch (Exception e) { error("Falha ao selecionar produto.", e); }
    }

    private void toggleSelected() {
        try {
            Produto p = selectedProduct();
            if (p == null) return;
            controller.alterarStatusProduto(p.getId(), !p.isAtivo());
            refreshAll();
        } catch (Exception e) { error("Não foi possível alterar o status.", e); }
    }

    private void productDialog(Produto old) {
        JTextField code = field(old == null ? "" : old.getCodigo());
        JTextField name = field(old == null ? "" : old.getNome());
        JTextField category = field(old == null ? "" : old.getCategoria());
        JTextField price = field(old == null ? "" : old.getPreco().toPlainString());
        JTextField initial = field("0");
        JTextField minimum = field(old == null ? "0" : String.valueOf(old.getEstoqueMinimo()));
        JCheckBox active = new JCheckBox("Produto ativo", old == null || old.isAtivo());
        active.setOpaque(false); active.setForeground(FG);

        JPanel form = new JPanel(new GridLayout(0, 2, 9, 9));
        form.setBackground(PANEL); form.setBorder(new EmptyBorder(12, 12, 12, 12));
        addField(form, "Código", code); addField(form, "Nome", name);
        addField(form, "Categoria", category); addField(form, "Preço", price);
        if (old == null) addField(form, "Estoque inicial", initial);
        else addField(form, "Estoque atual (somente movimentações)", label(String.valueOf(old.getQuantidade()), 13, FG));
        addField(form, "Estoque mínimo", minimum);
        form.add(new JLabel("")); form.add(active);

        int answer = JOptionPane.showConfirmDialog(this, form,
                old == null ? "Novo produto" : "Editar produto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;

        try {
            String priceText = price.getText().trim();
            if (priceText.contains(",")) {
                priceText = priceText.replace(".", "").replace(",", ".");
            }
            BigDecimal value = new BigDecimal(priceText);
            int qty = old == null ? Integer.parseInt(initial.getText().trim()) : old.getQuantidade();
            Produto p = new Produto(old == null ? null : old.getId(), code.getText(),
                    name.getText(), category.getText(), value, qty,
                    Integer.parseInt(minimum.getText().trim()), active.isSelected());
            controller.salvarProduto(p);
            refreshAll();
        } catch (Exception e) { error("Verifique os dados informados.", e); }
    }

    private void movementDialog(boolean isEntry) {
        try {
            List<Produto> activeProducts = allProducts().stream().filter(Produto::isAtivo).toList();
            if (activeProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cadastre ou ative um produto antes de movimentar.");
                return;
            }
            JComboBox<ProductOption> choice = new JComboBox<>();
            for (Produto p : activeProducts) choice.addItem(new ProductOption(p));
            JTextField qty = field("1");
            JTextField note = field("");
            JPanel form = new JPanel(new GridLayout(0, 2, 9, 9));
            form.setBackground(PANEL); form.setBorder(new EmptyBorder(12, 12, 12, 12));
            addField(form, "Produto", choice); addField(form, "Quantidade", qty);
            addField(form, "Observação", note);
            int answer = JOptionPane.showConfirmDialog(this, form,
                    isEntry ? "Registrar entrada" : "Registrar saída",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (answer != JOptionPane.OK_OPTION) return;
            Produto p = ((ProductOption) choice.getSelectedItem()).product;
            int amount = Integer.parseInt(qty.getText().trim());
            if (isEntry) controller.registrarEntrada(p.getId(), amount, note.getText());
            else controller.registrarSaida(p.getId(), amount, note.getText());
            refreshAll();
            JOptionPane.showMessageDialog(this, "Movimentação registrada com sucesso.");
        } catch (Exception e) { error("Não foi possível registrar a movimentação.", e); }
    }

    private record ProductOption(Produto product) {
        @Override public String toString() {
            return product.getCodigo() + " — " + product.getNome()
                    + " (saldo: " + product.getQuantidade() + ")";
        }
    }

    private void addField(JPanel panel, String title, Component component) {
        panel.add(label(title, 12, MUTED));
        panel.add(component);
    }

    private void error(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + "\n" + e.getMessage(),
                "EstoqueFlow • Erro", JOptionPane.ERROR_MESSAGE);
    }
}
