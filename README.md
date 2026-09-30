# UFCD 0791 - Trabalho Prático 3 (TP3)
## Código Robusto com Tratamento de Exceções

**Formando:** Carlos Marques  
**Módulo:** UFCD 0791 – Programação em JAVA (avançada)  
**Data:** 16 de setembro de 2026  

---

### 1. Versão da Distribuição do JDK
- **JDK Utilizado:** OpenJDK versão `25.0.2` (build 25.0.2+1-b329.117 / Java SE standard environment).
- **Compatibilidade:** Código compatível com Java SE 8+ utilizando puramente APIs nativas da plataforma (`java.lang`, `java.util`, `java.io`).

---

### 2. Instruções de Compilação
A partir do diretório raiz do projeto (`UFCD0791_TP3_Carlos_Marques`), execute o comando `javac` para compilar o código fonte e gerar os ficheiros `.class` na pasta `bin/`:

```bash
javac -d bin src/excecoes/*.java src/*.java
```

---

### 3. Instruções de Execução
Para executar a aplicação via linha de comandos, forneça como parâmetros os caminhos relativos do ficheiro de entrada (movimentos bancários) e do ficheiro de relatório de saída:

```bash
java -cp bin Principal caminho/para/movimentos.txt caminho/para/relatorio.txt
```

#### Exemplo prático de execução:
```bash
java -cp bin Principal test_movimentos.txt test_relatorio.txt
```

---

### 4. Especificação do Formato de Entrada e Tratamento de Erros

#### Formato dos Registos de Entrada
O ficheiro de entrada deve conter registos estruturados em texto simples, com um registo por linha, delimitados por ponto e vírgula (`;`):
```text
CLIENTE;TIPO_OPERACAO;VALOR;DESCRICAO
```

**Exemplo de lote válido:**
```text
CLI001;TRANSFERENCIA;250.50;Pagamento de fornecedores
CLI002;DEPOSITO;1200.00;Deposito em conta poupanca
CLI003;LEVANTAMENTO;50.00;Levantamento em caixa automatico
```

#### Sintaxe de Suporte a Erros e Resiliência
A aplicação valida atomicamente cada registo, tratando de forma isolada as seguintes falhas:
1. **Erros Estruturais de Formato (Linha Corrompida):**
   - Linhas vazias, nulas, truncadas ou com menos/mais de 4 campos delimitados por `;`.
   - Campos obrigatórios (`CLIENTE`, `TIPO_OPERACAO`, `DESCRICAO`) em falta.
   - Capturados via exceção personalizada *checked* `FormatoLinhaInvalidaException`.
2. **Erros de Validação Monetária (Dados Monetários Inválidos):**
   - Montantes com caracteres alfabéticos/não numéricos (ex.: `100.00EUR` ou `ABC`).
   - Montantes nulos, vazios, zero (`0.00`) ou estritamente negativos (`-50.00`).
   - Capturados via exceção personalizada *unchecked* `ValorMovimentoInvalidoException` (ou `NumberFormatException`).
3. **Resiliência do Lote:**
   - Perante uma linha inválida, o erro é registado na lista de anomalias com a identificação da linha e o motivo da rejeição.
   - O processamento **nunca é abortado prematuramente**, prosseguindo continuamente para as linhas subsequentes.
   - Ficheiros inexistentes ou totalmente vazios são tratados de forma controlada sem gerar *crashes* na JVM.

---

### 5. Justificação Técnica Fundamentada (Dicotomia Checked vs Unchecked)

A opção arquitetural pela dicotomia entre a exceção *checked* (`FormatoLinhaInvalidaException`) e a exceção *unchecked* (`ValorMovimentoInvalidoException`) fundamenta-se nos princípios do design de APIs em Java. A exceção `FormatoLinhaInvalidaException` herda de `java.lang.Exception` (*checked*) porque representa uma condição de erro previsível e externa derivada de ficheiros de integração malformados, obrigando o contrato do método intermédio (`validarEConverter` / `ProcessadorMovimentos`) a explicitar a cláusula `throws` e garantindo que a camada de orquestração implementa obrigatoriamente o tratamento defensivo. Por contraste, `ValorMovimentoInvalidoException` herda de `java.lang.RuntimeException` (*unchecked*) por representar uma violação de pré-condição dos dados monetários durante a conversão de tipos (análoga a `NumberFormatException` ou `IllegalArgumentException`), permitindo flexibilidade na conversão e delegação uniforme da captura no ciclo de processamento do lote. O padrão de propagação adotado assegura que o lançamento via `throw` nos métodos internos de parsing (`LinhaMovimento.parse`) é propagado contratualmente pela camada intermédia e capturado de forma coordenada no gestor de lote, mantendo a integridade da aplicação e eliminando fugas de recursos através de blocos `try-catch-finally` determinísticos.
