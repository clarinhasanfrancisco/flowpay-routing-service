# FlowPay - Routing Service API
> **Sistema de roteamento, distribuição de carga e gestão de filas FIFO para atendimento ao 
> cliente.**

---

## Sobre o Projeto & Contexto de Negócio
Este projeto foi desenvolvido como um **desafio técnico de aprendizagem durante a fase inicial
do programa de estágio na Ubots**. O objetivo principal foi desenvolver um MVP, colocando em 
prática conceitos fundamentais do desenvolvimento backend através da construção de um sistema 
de roteamento de tickets de atendimento.

No cenário da Ubots e de plataformas de comunicação/atendimento ao cliente, a distribuição 
eficiente de mensagens é um fator crítico. Em momentos de alta demanda, a atribuição manual de 
chamados gera gargalos, sobrecarrega atendentes e aumenta o tempo de resposta aos usuários.

### Objetivos de Aprendizagem & Desafios Abordados:
* **Entendimento de Regras de Negócio Reais:** Construção de um orquestrador que conecta mensagens de entrada à disponibilidade dos times de suporte.
* **Gestão de Carga e Concorrência:** Evitar a sobrecarga de atendentes respeitando o limite máximo de chamados simultâneos por profissional.
* **Filas e Ordenação (FIFO):** Implementação de gerenciamento transacional de filas (*First In, First Out*) para garantir o atendimento justo e ordenado de chamados em espera.

---

## Regras de Negócio & Arquitetura (RNs)

O motor do sistema baseia-se em quatro pilares fundamentais:

1. **Roteamento Especializado por Assunto (`Subject`):**
    * Cada chamado recebido é classificado por um assunto (*Cartões, Empréstimos ou Outros*).
    * O sistema busca automaticamente a equipe (`Team`) habilitada para atender aquela categoria.

2. **Controle de Carga de Trabalho (`Workload`):**
    * Cada atendente (`Agent`) possui uma capacidade máxima configurada de tickets em aberto.
    * Se um atendente tiver disponibilidade, o ticket é atribuído a ele imediatamente e ganha o status **`IN_PROGRESS`**.

3. **Fila de Espera FIFO (`First In, First Out`):**
    * Se todos os atendentes do time estiverem com a capacidade máxima atingida, o ticket não é rejeitado.
    * Ele entra em uma fila de espera ordenada por ordem de chegada com o status **`QUEUED`**.

4. **Puxada Automática da Fila (Resgate em Loop Fechado):**
    * Quando qualquer ticket é finalizado via endpoint de encerramento (`POST /finish`), a carga do atendente cai em -1.
    * O sistema aciona um evento transacional que busca o ticket mais antigo daquela mesma equipe na fila (`QUEUED`) e o atribui imediatamente ao atendente recém-liberado, alterando seu status para **`IN_PROGRESS`**.

---

## Tech Stack & Decisões Tecnológicas

* **Java 21:** Utilização de recursos modernos da linguagem.
* **Spring Boot 4:** Framework base para construção da API REST.
* **Spring Data JPA & Hibernate:** Mapeamento objeto-relacional e persistência de dados.
* **PostgreSQL:** Banco de dados relacional hospedado na nuvem (Neon.tech).
* **Docker:** Containerização da aplicação via Dockerfile para deploy no Render.
* **Bean Validation:** Validação de contratos dos DTOs na entrada das requisições.

---

## Estrutura das Entidades Principais

* **`Ticket`:** Representa o chamado do cliente (contém id, referência do chat, assunto, status e vínculos com time e agente).
* **`Team`:** Equipe responsável por uma categoria de atendimento, podendo ser CARDS, LOANS ou OTHERS.
* **`Agent`:** Atendente vinculado a um time, com controle de carga atual (`currentWorkload`).
* **`TicketStatus`:** Enum com os estados do ciclo de vida: `QUEUED` (Na Fila), `IN_PROGRESS` (Em Atendimento), `FINISHED` (Finalizado), `REJECTED` (Rejeitado).

---

## Como Executar a Aplicação

### Deploy em Produção (Nuvem)
A API está no ar e pronta para uso. Não é necessário clonar o projeto ou instalar dependências para testar as requisições:
* **Base URL:** [https://flowpay-routing-service.onrender.com/api/v1](https://flowpay-routing-service.onrender.com/api/v1)

Você pode utilizar clientes HTTP como Talend API Tester, Postman ou Insomnia apontando diretamente para as URLs listadas na seção Endpoints da API.

---

## Endpoints da API

**1. Criar e Rotear Ticket**
* **HTTP Method:** `POST`
* **Path:** `/tickets`
* **Headers:** `Content-Type: application/json`
* **Payload (Body):**
   ```json
   {
     "chatReference": 100200300,
     "subject": "Empréstimo"
   }
   ```
**2. Finalizar Ticket**
* **HTTP Method:** `POST`
* **Path:** `/tickets/{chatReference}/finish`