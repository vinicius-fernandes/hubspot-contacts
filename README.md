# Hubspot contacts
Este repositório implementa uma API para integração com o HubSpot, utilizando autenticação via OAuth 2.0 (authorization code flow). 
A API também oferece um endpoint de integração com a API do HubSpot para a criação de contatos e permite o recebimento de notificações via webhooks.

## Requisitos
### Hubspot
#### Configurações básicas
Para prosseguir é necessário um conta de desenvolvedor no hubspot, você pode criar uma através do link https://developers.hubspot.com/.

Crie um aplicativo público, você pode seguir as instruções da documentação: https://developers.hubspot.com/docs/guides/apps/public-apps/overview.

Na seção de autenticação adicione a seguinte url de redirecionamento: http://localhost:8080/auth/oauth-callback (Por padrão a aplicação é exposta através da porta 8080).

Aproveite também para anotar o seu client id e client secret.

#### Webhook
Nosso projeto processa eventos do tipo "contact.creation"

Para isso devemos configurar o webhook para eventos desse tipo. Você seguir os passos da documentação para isso: https://developers.hubspot.com/docs/guides/api/app-management/webhooks#webhook-settings.

Para testar o webhook localmente não conseguimos utilizar diretamente o localhost como fizemos para a URL de redirecionamento. Nesse caso podemos utilizar o ngrok, ou ferramentas similares, para expor nosso servidor local à internet e com isso possibilitar o uso do webhook.

A instalação do ngrok é relativamente simples : https://dashboard.ngrok.com/get-started/setup/windows.

Após a instalação basta executar ngrok http http://localhost:8080. Obtenha o endpoint gerado, por exemplo https://ab33-2804-14d-8483-404e-8c75-1389-58e1-5cf6.ngrok-free.app e utilize ele para configurar a URL de destino do webhook.

O endpoint do projeto responsável por lidar com o webhook é o /webhook, com isso utilizando a url de exemplo a URL de destino configurado seria: https://ab33-2804-14d-8483-404e-8c75-1389-58e1-5cf6.ngrok-free.app/webhook.

### Execução local do projeto
#### Variáveis de ambiente
As seguintes variáveis de ambiente devem estar definidas:

- HUBSPOT_CLIENT_ID= <CLIENT_ID_SEU_APP_PUBLICO>
- HUBSPOT_CLIENT_SECRET= <CLIENT_SECRET_SEU_APP_PUBLICO>
- HUBSPOT_TOKEN_URL=https://api.hubapi.com/oauth/v1/token
- HUBSPOT_REDIRECT_URL=http://localhost:8080/auth/oauth-callback (Deve ser alterada caso exista alguma modificação como por exemplo porta ou dominio.)
- HUBSPOT_AUTHORIZATION_URL=https://app.hubspot.com/oauth/authorize

#### Execução com docker
Você pode criar uma nova imagem com o comando:
```powershell
docker build -t contacts:1.0 .
```
E em seguida rodar o container com:
```powershell
docker run -e "HUBSPOT_AUTHORIZATION_URL=https://app.hubspot.com/oauth/authorize" `
           -e "HUBSPOT_CLIENT_ID=<CLIENT_ID_SEU_APP_PUBLICO>" `
           -e "HUBSPOT_CLIENT_SECRET=<CLIENT_SECRET_SEU_APP_PUBLICO>" `
           -e "HUBSPOT_REDIRECT_URL=http://localhost:8080/auth/oauth-callback" `
           -e "HUBSPOT_TOKEN_URL=https://api.hubapi.com/oauth/v1/token" `
           -p 8080:8080 `
           contacts:1.0
```
Os comandos acima foram executados no windows com o powershell, pode ser necessário adaptar para seu caso.
Se tudo ocorreu bem você deve visualizar algo como:
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
Para testar você pode enviar um GET request para http://localhost:8080/auth/url ou abrir esse link no navegador.
Ele deve iniciar o fluxo de autenticação com o hubspot.
Como resultado algo 
```json
{
  "access_token": <ACCESS_TOKEN>,
  "token_type": "bearer",
  "expires_in": 1800,
  "refresh_token": <REFRESH_TOKEN>
}
```


