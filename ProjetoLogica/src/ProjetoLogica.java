import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProjetoLogica {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        int quantExpr = 0;
        String[] expressoes = null;
        String nomeArquivo = "Entrada.in";
        FileWriter arquivo = new FileWriter("Saida.in");
        PrintWriter gravarArquivo = new PrintWriter(arquivo);
        try {
            FileReader abrirArquivo = new FileReader(nomeArquivo);
            BufferedReader lerArquivo = new BufferedReader(abrirArquivo);
            try {
                String linha = lerArquivo.readLine();
                quantExpr = Integer.parseInt(linha);
                expressoes = new String[quantExpr];
                for(int i = 0; i < quantExpr; i++)
                {
                    linha = lerArquivo.readLine();
                    expressoes[i] = linha;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println();
        String expressao = "";
        int indexValoracao = 0;
        Map<Character,Boolean> variaveis;
        for(int i = 0; i < quantExpr; i++)
        {
            variaveis = new HashMap<>();
            String[] valores = getExpressao(expressoes[i]);
            boolean conjuntoValido;
            boolean conjuntoSatisfativel;
            //Armazeno a expressao ou cojunto
            expressao = valores[0];
            //Armazena o index onde comeca os valores-verdades
            indexValoracao = Integer.parseInt(valores[1]);
            //Verifica se é um conjunto
            if(expressao.charAt(0) == '{' && expressao.charAt(expressao.length()-1) == '}')
            {
                conjuntoValido = true;
                conjuntoSatisfativel = true;
                expressao = expressao.substring(1, expressao.length()-1);
                //Verifica se é um conjunto com mais de uma expressao
                if(expressao.contains(", "))
                {
                    //Armazena todas as expressoes do conjunto
                    String[] expressoesConjunto = expressao.split(", ");
                    //Percorre cada expressao do conjunto
                    for(int j = 0; j < expressoesConjunto.length && conjuntoValido; j++)
                    {
                        expressao = expressoesConjunto[j];
                        if(indexValoracao != -1 && valida(expressao))
                        {
                            armazenarValoracao(variaveis,expressoes[i],indexValoracao);
                            conjuntoValido = true;
                            //Se as expressoes do conjunto ainda tao satisfativeis, verifica se a atual é satisfativel tambem
                            if(conjuntoSatisfativel && satisfativel(variaveis,expressao))
                            {
                                conjuntoSatisfativel = true;
                            }
                            else
                            {
                                conjuntoSatisfativel = false;
                            }
                        }
                        else
                        {
                            conjuntoValido = false;
                        }
                    }
                }
                //Entra aqui se for um conjunto com apenas uma expressao
                else
                {
                    if(indexValoracao != -1 && valida(expressao))
                    {
                        armazenarValoracao(variaveis,expressoes[i],indexValoracao);
                        conjuntoValido = true;
                        if(satisfativel(variaveis,expressao))
                        {
                            conjuntoSatisfativel = true;
                        }
                        else
                        {
                            conjuntoSatisfativel = false;
                        }
                    }
                    else
                    {
                        conjuntoValido = false;
                    }
                }
                if(conjuntoValido)
                {
                    if(conjuntoSatisfativel)
                    {
                        gravarArquivo.printf("Problema #%d\n",i+1);
                        gravarArquivo.println("A valoracao-verdade satisfaz o conjunto.");
                    }
                    else
                    {
                        gravarArquivo.printf("Problema #%d\n",i+1);
                        gravarArquivo.println("A valoracao-verdade nao satisfaz o conjunto.");
                    }
                }
                else
                {
                    gravarArquivo.printf("Problema #%d\n",i+1);
                    gravarArquivo.println("Ha uma palavra nao legitima no conjunto.");
                }
            }
            else
            {
                //Verifica se tem algum valor verdade da expressao, e verifica se a expressao é valida
                if(indexValoracao != -1 && valida(expressao))
                {
                    armazenarValoracao(variaveis,expressoes[i],indexValoracao);
                    if(satisfativel(variaveis, expressao))
                    {
                        gravarArquivo.printf("Problema #%d\n",i+1);
                        gravarArquivo.println("A valoracao-verdade satisfaz a proposicao.");
                    }
                    else
                    {
                        gravarArquivo.printf("Problema #%d\n",i+1);
                        gravarArquivo.println("A valoracao-verdade nao satisfaz a proposicao.");
                    }

                }
                else
                {
                    gravarArquivo.printf("Problema #%d\n",i+1);
                    gravarArquivo.println("A palavra nao e legitima.");
                }
            }
            if(i < quantExpr-1)
            {
                gravarArquivo.println();
            }
        }
        arquivo.close();
    }
    //----------------------------------------------------------------------------------------------
    public static boolean valida(String expressao)
    {
        int inicio = 0;
        int fim = expressao.length() - 1;
        if(expressao.length() == 1 && expressao.charAt(0) >= 'A' && expressao.charAt(0) <= 'Z')
        {
            return true;
        }
        int indexSimbolo = getIndexSimbolo(expressao);
        //Se o operador for ~
        if(expressao.charAt(inicio) == '(' && expressao.charAt(fim) == ')' && expressao.charAt(inicio + 1) == '~' && inicio + 2 < fim)
        {
            return valida(expressao.substring(inicio + 2, fim));
        }
        //Se o operador for & ou v ou >
        else if(expressao.charAt(inicio) == '(' && expressao.charAt(fim) == ')' && indexSimbolo != -1)
        {
            //Verifica se antes e depois do simbolo tem espaço
            if(expressao.charAt(indexSimbolo-1) == expressao.charAt(indexSimbolo+1) && expressao.charAt(indexSimbolo-1) == ' ')
            {
                return valida(expressao.substring(inicio+1,indexSimbolo - 1)) && valida(expressao.substring(indexSimbolo+2,fim));
            }
        }
        return false;
    }
    //------------------------------------------------------------------------------------------------------
    public static int getIndexSimbolo(String expressao)
    {
        int quantParenteses = 0;
        for(int i = 0; i < expressao.length(); i++)
        {
            if(expressao.charAt(i) == '(')
            {
                quantParenteses++;
            }
            else if(expressao.charAt(i) == ')')
            {
                quantParenteses--;
            }
            // o i > 3 verifica se nao esta em nenhum dos primeiros index
            if((expressao.charAt(i) == '&' || expressao.charAt(i) == 'v' || expressao.charAt(i) == '>') && quantParenteses == 1 && i >= 3 )
            {
                return i;
            }
        }
        return -1;
    }
    //--------------------------------------------------------------------------------------
    public static void armazenarValoracao(Map<Character, Boolean> variaveis, String expressaoCompleta, int indexValorcao)
    {
        boolean valor = false;
        for(int i = 0, j = indexValorcao; i< indexValorcao-1 && j < expressaoCompleta.length(); i++)
        {
            if(expressaoCompleta.charAt(i) >= 'A' && expressaoCompleta.charAt(i) <= 'Z' && !variaveis.containsKey(expressaoCompleta.charAt(i)))
            {
                if(expressaoCompleta.charAt(j) == '1')
                {
                    valor = true;
                }
                else
                {
                    valor = false;
                }
                variaveis.put(expressaoCompleta.charAt(i),valor);
                j+=2;
            }
        }
    }
    //--------------------------------------------------------------------------------------
    public static String[] getExpressao(String expressaoCompleta)
    {
        //Valores que serao retornados se nao tiver valores verdades após a expressao
        String[] valores = {expressaoCompleta,"-1"};
        String expressao = "";
        //Pega a expressao sem os valores verdades e pega o index onde comeca os valores verdades
        for (int i = 0; i < expressaoCompleta.length(); i++)
        {
            expressao += expressaoCompleta.charAt(i);
            //Verifica sechegou no ultimo caracter da expressao(sem contar os valores verdades das variaveis)
            if(i + 2 <= expressaoCompleta.length()-1 && (expressaoCompleta.charAt(i+2) == '0' || expressaoCompleta.charAt(i+2) == '1'))
            {
                //Armazeno a expressao
                valores[0] = expressao;
                //Armazeno o index do inicio dos valores verdades das variaveis
                valores[1] = Integer.toString(i+2);
                break;
            }
        }
        return valores;
    }
    //--------------------------------------------------------------------------------------------

    public static boolean satisfativel(Map<Character, Boolean> variaveis, String expressao)
    {
        int inicio = 0;
        int fim = expressao.length()-1;
        if(expressao.length() == 1)
        {
            return variaveis.get(expressao.charAt(0));
        }
        int indexSimbolo = getIndexSimbolo(expressao);
        if(expressao.charAt(inicio+1) == '~')
        {
            return !satisfativel(variaveis,expressao.substring(inicio+2,fim));
        }
        else if(expressao.charAt(indexSimbolo) == '&')
        {
            return satisfativel(variaveis, expressao.substring(inicio + 1, indexSimbolo - 1)) && satisfativel(variaveis, expressao.substring(indexSimbolo+2,fim));
        }
        else if(expressao.charAt(indexSimbolo) == 'v')
        {
            return satisfativel(variaveis, expressao.substring(inicio + 1, indexSimbolo - 1)) || satisfativel(variaveis, expressao.substring(indexSimbolo+2,fim));
        }
        else if(expressao.charAt(indexSimbolo) == '>')
        {
            return !satisfativel(variaveis, expressao.substring(inicio + 1, indexSimbolo - 1)) || satisfativel(variaveis, expressao.substring(indexSimbolo + 2,fim));
        }
        else
        {
            return false;
        }
    }
}
