package com.unicesumar;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.Sale;
import com.unicesumar.entities.User;
import com.unicesumar.paymentMethods.PaymentMethod;
import com.unicesumar.paymentMethods.PaymentType;
import com.unicesumar.repository.ProductRepository;
import com.unicesumar.repository.SaleRepository;
import com.unicesumar.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.unicesumar.utils.DataBaseConnection.getConnection;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:database.sqlite";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            iniciarBancoDeDados();
            if (connection == null) {
                System.out.println("Não foi possível estabelecer conexão com o banco de dados.");
                return;
            }

            ProductRepository productRepo = new ProductRepository(connection);
            UserRepository userRepo = new UserRepository(connection);
            SaleRepository saleRepo = new SaleRepository(connection);

            boolean running = true;
            while (running) {
                exibirMenu();
                int opcao = obterOpcao();

                switch (opcao) {
                    case 1 -> cadastrarProduto(productRepo);
                    case 2 -> listarProdutos(productRepo);
                    case 3 -> cadastrarUsuario(userRepo);
                    case 4 -> listarUsuarios(userRepo);
                    case 5 -> registrarVenda(userRepo, productRepo, saleRepo);
                    case 6 -> {
                        System.out.println("Encerrando aplicação...");
                        running = false;
                    }
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            scanner.close();
        }
    }

    private static void exibirMenu() {
        System.out.println("""
                \n=== MENU PRINCIPAL ===
                1 - Cadastrar Produto
                2 - Listar Produtos
                3 - Cadastrar Usuário
                4 - Listar Usuários
                5 - Registrar Venda
                6 - Sair
                """);
        System.out.print("Escolha uma opção: ");
    }

    private static int obterOpcao() {
        while (!scanner.hasNextInt()) {
            System.out.print("Digite uma opção válida: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void cadastrarProduto(ProductRepository repo) {
        scanner.nextLine(); // limpar buffer
        System.out.println("\n=== Cadastro de Produto ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();

        Product produto = new Product(nome, preco);
        repo.save(produto);
        System.out.println("Produto cadastrado com sucesso!");
    }

    private static void listarProdutos(ProductRepository repo) {
        int cont = 0;
        System.out.println("\n=== Lista de Produtos ===");
        List<Product> produtos = repo.findAll();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }
        for (Product produto : produtos) {
            cont += 1;
            System.out.println("ID: " + cont + ", Nome: " + produto.getName() + ", Preco: " + produto.getPrice());
        }
    }

    private static void cadastrarUsuario(UserRepository repo) {
        scanner.nextLine(); // limpar buffer
        System.out.println("\n=== Cadastro de Usuário ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        User usuario = new User(nome, email);
        repo.save(usuario);
        System.out.println("Usuário cadastrado com sucesso!");
    }

    private static void listarUsuarios(UserRepository repo) {
        int cont = 0;
        System.out.println("\n=== Lista de Usuários ===");
        List<User> usuarios = repo.findAll();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário encontrado.");
            return;
        }

        for(User usuario : usuarios) {
            cont++;
            System.out.println("ID: " + cont + " Nome: " + usuario.getName() + ", Email: " + usuario.getEmail());
        }
    }

    private static void registrarVenda(UserRepository userRepo, ProductRepository productRepo, SaleRepository saleRepo) {
        scanner.nextLine();
        System.out.println("\n=== Registrar Venda ===");
        System.out.print("Digite o email do usuário: ");
        String email = scanner.nextLine();

        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        User usuario = userOpt.get();
        System.out.println("Usuário: " + usuario.getName() + "\n");

        List<Product> produtos = productRepo.findAll();
        for (int i = 0; i < produtos.size(); i++) {
            Product produto = produtos.get(i);
            System.out.println("ID: " + (i + 1) + ", nome: " + produto.getName() + ", preço: " + produto.getPrice());
        }

        System.out.print("Digite os IDs dos produtos (separados por vírgula): ");
        String[] ids = scanner.nextLine().split(",");
        List<Product> produtosSelecionados = new ArrayList<>();
        double valorTotal = 0.0;

        for (String idStr : ids) {
            try {
                int index = Integer.parseInt(idStr.trim()) - 1;
                if (index >= 0 && index < produtos.size()) {
                    Product produto = produtos.get(index);
                    produtosSelecionados.add(produto);
                    valorTotal += produto.getPrice();
                    System.out.printf("->  %s ($ %.2f)%n", produto.getName(), produto.getPrice());
                } else {
                    System.out.println("ID fora do intervalo da lista: " + (index + 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("ID inválido: " + idStr);
            }
        }

        if (produtosSelecionados.isEmpty()) {
            System.out.println("Nenhum produto válido selecionado.");
            return;
        }

        PaymentType paymentType = selecionarFormaDePagamento();
        PaymentMethod metodoPagamento = PaymentMethodFactory.create(paymentType);

        PaymentManager paymentManager = new PaymentManager();
        paymentManager.setPaymentMethod(metodoPagamento);

        System.out.println("\nProcessando pagamento...");
        paymentManager.pay();

        Sale venda = new Sale(usuario, produtosSelecionados, valorTotal, paymentType);
        saleRepo.save(venda);

        System.out.println("Venda registrada com sucesso! \n");
        System.out.println("==== Dados da venda =====");
        System.out.println("Nome do comprador: " + venda.getClient().getName());
        System.out.println("Email do comprador: " + venda.getClient().getEmail());
        System.out.println("Produtos: ");
        for (Product produto : venda.getProducts()) {
            System.out.println("->" + produto.getName() + " $" + produto.getPrice());
        }
        System.out.println("Total da compra: $" + venda.getTotalValue());
        System.out.println("Forma de pagamento: " + venda.getPaymentType().toString());
        System.out.println("===========================");
    }

    private static PaymentType selecionarFormaDePagamento() {
        System.out.println("\nFormas de Pagamento:");
        System.out.println("1 - Cartão de Crédito");
        System.out.println("2 - Boleto");
        System.out.println("3 - PIX");
        System.out.print("Escolha a opção: ");

        int opcao = scanner.nextInt();
        return switch (opcao) {
            case 1 -> PaymentType.CARTAO;
            case 2 -> PaymentType.BOLETO;
            case 3 -> PaymentType.PIX;
            default -> {
                System.out.println("Opção inválida. Default: PIX");
                yield PaymentType.PIX;
            }
        };
    }

    public static void iniciarBancoDeDados() throws IOException, SQLException {
        String sqlFilePath = "src/main/resources/init.sql";
        String sql = new String(Files.readAllBytes(Paths.get(sqlFilePath)));

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String command : sql.split(";")) {
                if (!command.trim().isEmpty()) {
                    stmt.executeUpdate(command.trim());
                }
            }
        }
    }
}