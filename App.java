import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner entrada = new Scanner(System.in);
        int opcao = 0;
        do {
            System.out.println("----------------------------");
            System.out.println("      CONTAS E TRIBUTOS");
            System.out.println("(Água, Luz, Telefone, IPTU, ISS)");
            System.out.println("----------------------------");
            System.out.println("1) Pagamento c/Código de Barras");
            System.out.println("2) Imprimir 2ª Via de Boleto");
            System.out.println("3) Sair");
            System.out.println("----------------------------");
            System.out.print("Digite a opção: ");
            
            try {
                opcao = Integer.parseInt(entrada.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida! Por favor, insira uma das opções númericas.");
                continue;
            }
          
            switch (opcao) {
                case 1:
                    realizarPagamento(entrada);
                    aguardarEnter(entrada);
                    break;
                case 2:
                    imprimirSegundaVia(entrada);
                    aguardarEnter(entrada);
                    break;
                case 3:
                    System.out.println("Fim do Programa!!!");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        } while (opcao != 3);

        entrada.close();
    }
    public static void aguardarEnter(Scanner entrada) {
        System.out.println("\nPressione Enter para voltar ao menu...");
        entrada.nextLine();}

    // Realizar o pagamento com código de barras
    public static void realizarPagamento(Scanner entrada) {
        System.out.println("Digite os 4 blocos do código de barras (12 dígitos cada):");
        for (int i = 1; i <= 4; i++) {
            System.out.print("Bloco " + i + ": ");
            String bloco = entrada.nextLine();
            if (!validarBloco(bloco)) {
                System.out.println("Dígito verificador inválido no bloco " + i + "!");
                return; 
            }
        }
        System.out.println("PAGAMENTO EFETUADO COM SUCESSO!");
    }

    // Função para validar o bloco do código de barras pelo módulo 10
    public static boolean validarBloco(String bloco) {
        if (bloco.length() != 12) {
            System.out.println("O bloco deve ter 12 dígitos.");
            return false;
        }
     
        int soma = 0;
        int multiplicador = 2;
        for (int i = bloco.length() - 2; i >= 0; i--) { 
            int digito = Character.getNumericValue(bloco.charAt(i));
            int resultado = digito * multiplicador;

            // Soma dos dígitos do produto
            if (resultado > 9) {
                soma += (resultado / 10) + (resultado % 10);
            } else {
                soma += resultado;
            }
            
            if (multiplicador == 2) {
                multiplicador = 1;
            } else {
                multiplicador = 2;
            }
        }
        int resto = soma % 10;
        int dacEsperado;
        if (resto == 0) {
            dacEsperado = 0;
        } else {
            dacEsperado = 10 - resto;
        }
        
        int dacInformado = Character.getNumericValue(bloco.charAt(bloco.length() - 1)); 
        
        return dacEsperado == dacInformado;
    }

    // Imprimir a 2ª via do boleto
    public static void imprimirSegundaVia(Scanner entrada) {
        String codigo;

        // Solicita o código de 3 dígitos e valida se é "836"
        do {
            System.out.print("Digite o código: ");
            codigo = entrada.nextLine();
            if (!codigo.equals("836")) {
                System.out.println("Código inválido! O código deve ser 836.");
            }
        } while (!codigo.equals("836"));
        System.out.print("Digite o valor da fatura: ");
        String valorString = entrada.nextLine().replace(",", ".");
        Double valorReal = Double.parseDouble(valorString);
        String valorFatura = formatarValorFatura(valorReal);
        System.out.print("Digite a identificação da empresa: ");
        String idEmpresa = String.format("%07d", Integer.parseInt(entrada.nextLine()));
        System.out.print("Digite a unidade consumidora: ");
        String unidadeConsumidora = String.format("%07d", Integer.parseInt(entrada.nextLine()));
        System.out.print("Digite o Ano-Mês (AAAAMM): ");
        String anoMes = String.format("%06d", Integer.parseInt(entrada.nextLine()));
        System.out.print("Digite o código sequencial: ");
        String sequencial = String.format("%07d", Integer.parseInt(entrada.nextLine()));
        String digitoSeparador = "9"; // Dígito separador sempre igual a 9
        String linhaDigitavel = codigo + valorFatura + idEmpresa + unidadeConsumidora + anoMes + digitoSeparador + sequencial + digitoSeparador;

        // Validação do comprimento da linha digitável
        if (linhaDigitavel.length() != 43) {
            System.out.println("Erro na formatação dos dados.");
            return;
        }

        // Cálculo do DAC geral usando módulo 10
        int soma = 0;
        int multiplicador = 2;
        for (int i = linhaDigitavel.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(linhaDigitavel.charAt(i));
            int resultado = digito * multiplicador;

            // Soma os dígitos individualmente se o resultado for maior que 9
            if (resultado > 9) {
                soma += (resultado / 10) + (resultado % 10);
            } else {
                soma += resultado;
            }

            // Alterna o multiplicador entre 2 e 1
            if (multiplicador == 2) {
                multiplicador = 1;
            } else {
                multiplicador = 2;
            }
        }

        int resto = soma % 10;
        int dacGeral;
        if (resto == 0) {
            dacGeral = 0;
        } else {
            dacGeral = 10 - resto;
        }
        
        String codigoDeBarras = linhaDigitavel.substring(0, 3) + dacGeral + linhaDigitavel.substring(3);

        // Exibição do resultado final
        System.out.println("Código de barras: " + codigoDeBarras);
    }

    public static String formatarValorFatura(double valor) {
        long valorCentavos = (long) (valor * 100); 
        return String.format("%011d", valorCentavos); 
    }
}
