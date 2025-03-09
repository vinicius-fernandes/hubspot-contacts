# Hubspot contacts
Este repositório implementa uma API para integração com o HubSpot, utilizando autenticação via OAuth 2.0 (authorization code flow). 
A API também oferece um endpoint de integração com a API do HubSpot para a criação de contatos e permite o recebimento de notificações via webhooks.

## Índice

- [Requisitos](#requisitos)
- [Configuração no HubSpot](#configuração-no-hubspot)
  - [Configurações básicas](#configurações-básicas)
  - [Webhook](#webhook)
- [Execução do Projeto](#execução-do-projeto)
  - [Variáveis de ambiente](#variáveis-de-ambiente)
  - [Execução com Docker](#execução-com-docker)
- [Contribuindo com o Projeto](#contribuindo-com-o-projeto)

## Requisitos
- Uma conta no HubSpot e credenciais para utilizar a autenticação via OAuth 2.0
- [API Key do HubSpot](https://developers.hubspot.com/docs/api/keys) para configuração
- [Docker](https://docs.docker.com/desktop/) para execução do projeto
- [JDK 21](https://www.oracle.com/java/technologies/downloads/?er=221886#java21)
- [Maven](https://maven.apache.org/install.html)

## Configuração no Hubspot
### Configurações básicas
1. **Conta de desenvolvedor HubSpot**: Crie uma conta de desenvolvedor no HubSpot através do link [HubSpot Developer](https://developers.hubspot.com/).
2. **Criar aplicativo público**: Siga as instruções da documentação para criar um aplicativo público em [HubSpot Apps](https://developers.hubspot.com/docs/guides/apps/public-apps/overview).
3. **URL de redirecionamento**: Adicione a URL de redirecionamento `http://localhost:8080/auth/oauth-callback` (o padrão da aplicação é a porta 8080).
4. **Client ID e Client Secret**: Anote o **Client ID** e o **Client Secret** do seu aplicativo público, que serão necessários para a autenticação.

### Webhook
Nosso projeto processa eventos do tipo "contact.creation", siga a [documentação dos webhooks](https://developers.hubspot.com/docs/guides/api/app-management/webhooks#webhook-settings) para configurar seu webhook.

Para testar o webhook localmente não conseguimos utilizar diretamente o localhost como fizemos para a URL de redirecionamento. Nesse caso podemos utilizar o ngrok, ou ferramentas similares, para expor nosso servidor local à internet e com isso possibilitar o uso do webhook.

Configurando o ngrok:

1. A instalação do ngrok é relativamente simples : https://dashboard.ngrok.com/get-started/setup/windows.

2. Após a instalação basta executar ngrok http http://localhost:8080. Obtenha o endpoint gerado, por exemplo https://ab33-2804-14d-8483-404e-8c75-1389-58e1-5cf6.ngrok-free.app e utilize ele para configurar a URL de destino do webhook.

3. O endpoint do projeto responsável por lidar com o webhook é o /webhook, com isso utilizando a url de exemplo a URL de destino configurado seria: https://ab33-2804-14d-8483-404e-8c75-1389-58e1-5cf6.ngrok-free.app/webhook.

## Execução do projeto
### Variáveis de ambiente
As seguintes variáveis de ambiente devem estar definidas
```bash
HUBSPOT_CLIENT_ID=<CLIENT_ID_SEU_APP_PUBLICO>
HUBSPOT_CLIENT_SECRET=<CLIENT_SECRET_SEU_APP_PUBLICO>
HUBSPOT_TOKEN_URL=https://api.hubapi.com/oauth/v1/token
HUBSPOT_REDIRECT_URL=http://localhost:8080/auth/oauth-callback  # Alterar se necessário
HUBSPOT_AUTHORIZATION_URL=https://app.hubspot.com/oauth/authorize
```

### Execução com docker
1. Crie uma nova imagem 
```powershell
docker build -t contacts:1.0 .
```
2. Execute o container
```powershell
docker run -e "HUBSPOT_AUTHORIZATION_URL=https://app.hubspot.com/oauth/authorize" `
           -e "HUBSPOT_CLIENT_ID=<CLIENT_ID_SEU_APP_PUBLICO>" `
           -e "HUBSPOT_CLIENT_SECRET=<CLIENT_SECRET_SEU_APP_PUBLICO>" `
           -e "HUBSPOT_REDIRECT_URL=http://localhost:8080/auth/oauth-callback" `
           -e "HUBSPOT_TOKEN_URL=https://api.hubapi.com/oauth/v1/token" `
           -p 8080:8080 `
           contacts:1.0
```
3. Verifique o funcionamento
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.4.3)

2025-03-09T14:59:36.364Z  INFO 1 --- [contacts] [           main] c.v.h.contacts.ContactsApplication       : Starting ContactsApplication v0.0.1-SNAPSHOT using Java 21.0.6 with PID 1 (/contacts.jar started by root in /)
2025-03-09T14:59:36.367Z  INFO 1 --- [contacts] [           main] c.v.h.contacts.ContactsApplication       : No active profile set, falling back to 1 default profile: "default"
2025-03-09T14:59:37.316Z  INFO 1 --- [contacts] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-03-09T14:59:37.333Z  INFO 1 --- [contacts] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-03-09T14:59:37.333Z  INFO 1 --- [contacts] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.36]
2025-03-09T14:59:37.361Z  INFO 1 --- [contacts] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-03-09T14:59:37.362Z  INFO 1 --- [contacts] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 944 ms
2025-03-09T14:59:37.753Z  INFO 1 --- [contacts] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-03-09T14:59:37.775Z  INFO 1 --- [contacts] [           main] c.v.h.contacts.ContactsApplication       : Started ContactsApplication in 1.841 seconds (process running for 2.426)
```   
3. Para testar você pode enviar um GET request para http://localhost:8080/auth/url ou abrir esse link no navegador.
4. O fluxo de autenticação com o Hubspot é iniciado.
5. Resultado esperado:
```json
{
  "access_token": <ACCESS_TOKEN>,
  "token_type": "bearer",
  "expires_in": 1800,
  "refresh_token": <REFRESH_TOKEN>
}
```

## Contribuindo com o projeto
1. Verifique a instalação do JDK
```shell
java --version
```
```shell
java 21.0.6 2025-01-21 LTS
```
2. Verifique a instalação do Maven
```shell
mvn -v
```
```shell
Apache Maven 3.9.9 
Java version: 21.0.6
```
3. Defina as variáveis de ambiente

4. Execute a aplicação
```shell
mvn spring-boot:run
```
5. Execute testes
```shell
mvn test
```



