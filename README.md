# Sistemas Distribuídos

Projeto de Programação - Gossip<br />
Alunos: 
Felipe Oliveira Silva<br />
RA: 11201822479<br />
<br />
Felipe de Souza Tiozo<br />
RA: 11201822483

## Demo
https://rebrand.ly/urm90tm

## Rodar o código

```
cd src
javac Peer.java && java Peer
```

<b>1. Definição do Sistema</b>
Crie um sistema P2P não-estruturado que permita encontrar o peer que possui uma
determinada informação a ser procurada, utilizando UDP como protocolo da camada de
transporte e Gossip como protocolo de busca.

<b>2. Recomendação Inicial</b>
Se nunca programou com TCP ou UDP, recomendo assistir o vídeo do link
https://www.youtube.com/watch?v=nysfXweTI7o e implementar os exemplos mostrados. Só
assistir o vídeo não lhe será de utilidade quando tiver que implementar funcionalidades mais
complexas.

<b>3. Visão geral do sistema</b>
O sistema será composto por muitos peers (com IP e porta conhecidas). Cada peer atua
tanto como provedor de informações (neste caso dos nomes dos arquivos que possui)
quanto como buscador destas.
Considere que quatro peers peer1, peer2, peer3 e peer4 fazem parte do sistema P2P. Um
peer, por exemplo o peer1, poderá procurar pelo nome de um arquivo e enviar essa
requisição a outro (e.g., peer4), escolhido de forma aleatória. Caso o peer4 tenha o
arquivo, responderá ao peer1 diretamente com seus dados (e.g., IP:porta de peer4). Caso o
peer4 não tenha o arquivo, escolherá outro peer (e.g., peer3) e repetirá o processo.

<b>4. Funcionalidades do Peer X</b>

a) Recebe e responde simultaneamente (obrigatório com threads) requisições dos peers.
Por ‘simultaneamente’ entenda-se que o peer deverá poder realizar outras
funcionalidades enquanto está fazendo requisições ou esperando por elas.

b) Inicialização: captura do teclado o IP e porta do peer X, a pasta onde estão localizados
seus arquivos, e o IP e porta de outros dois peers.

c) Monitoramento da pasta: cada 30 segundos o peer verificará se na pasta (capturada na
inicialização) houveram modificações, ou seja se foram inseridos ou removidos
arquivos. A lista de arquivos deverá estar armazenada em alguma estrutura na
memória, por exemplo, uma lista ou um hash.

d) Envio de uma mensagem SEARCH: escolhe de forma aleatória um peer (obtidos na
inicialização) a quem enviar a requisição, contendo o nome do arquivo procurado.

e) Recebe uma mensagem SEARCH: 1º importante, descarte requisições já processadas
(i.e., já respondidas ou encaminhadas). 2º Caso o peer X tenha o arquivo, responderá
diretamente a quem inicialmente realizou o SEARCH com uma mensagem
RESPONSE (que contém o IP:porta do peer X). 3º Caso X não tenha o arquivo,
encaminhará a requisição, repetindo o processo de envio do SEARCH (item d).

f) Recebe uma mensagem RESPONSE: descarte requisições já processadas.

g) Monitoramento do envio do SEARCH. No item d) quando o peer realiza um envio, pode
que nunca receba a mensagem RESPONSE (pois ninguém possui o arquivo). Assim, o
peer deve ter algum mecanismo para tratar esse caso (por exemplo, usando timeouts).
Observações:

 Toda comunicação entre peer  peer será por UDP e deverá obrigatoriamente
transferir uma classe Mensagem criada por você.

<b>5. Mensagens (prints) apresentadas na console</b>
Na console do peer deverão ser apresentadas “exatamente” (nem mais nem menos) as
seguintes informações

 Menu interativo (por console) que permita realizar a escolha somente das funções
INICIALIZA e SEARCH.

o No caso do INICIALIZA, deve capturar do teclado o IP:porta, a pasta onde se
encontram os arquivos (e.g., c:\temp\peer1\, c:\temp\peer2\, etc.) e mais
outros dois IP:porta. Faça print “arquivos da pasta: [só nomes dos arquivos]”.

o No caso do SEARCH, deve capturar do teclado só o nome do arquivo com
sua extensão (e.g., aula.mp4). A busca será exatamente por esse nome.
Note que não deve capturar a pasta.

 Cada 30 segundos, print “Sou peer [IP]:[porta] com arquivos [só nomes dos
arquivos]”. Substitua a informação entre os parênteses com as reais. Por exemplo:
Sou peer 127.0.0.1:8776 com arquivos aula1.mp4 aula2.mp4
 Quando receber a mensagem RESPONSE (caso não tenha sido processada antes),
print “peer com arquivo procurado: [IP:porta] [arquivo_procurado]”.
 Se nunca receber a mensagem RESPONSE, print “ninguém no sistema possui o
arquivo [arquivo_procurado]”.
 Quando receber uma mensagem SEARCH:

o Caso já tenha processado a requisição, print “requisição já processada para
[arquivo_procurado]”.

o Caso não tenha processado a requisição:
 Se tiver o arquivo, print “tenho [arquivo_procurado] respondendo para
[IP:porta]. Note que esse IP:porta é de quem inicialmente fez a busca.
 Se não tiver o arquivo, print “não tenho [arquivo_procurado],
encaminhando para [IP:porta]. Note que esse IP:porta é aleatório.

<b>6. Teste realizado pelo professor</b>
O professor compilará o código usando o javac da JDK 1.8.
Após a compilação, o professor abrirá 4 consoles (no Windows, seria o CMD.EXE, também
conhecido como prompt) correspondendo a 4 peers. A partir das consoles, o professor
realizará os testes do funcionamento do sistema. Exemplo das consoles pode ser
observado no link: https://www.youtube.com/watch?v=FOwKxw9VYqI
Cabe destacar que:
 Seu código não deve estar limitado a 4 peers, suportando mais do que 4.
 Você não precisará de 4 computadores para realizar a atividade. Basta abrir as 4
consoles.
