# becommerce inventory

Esse projeto foi construído com maven, vá até a pasta raiz do projeto e digite no terminal:  
  
-> mvn clean install  

Nesse momento, seu projeto deve compilar e rodar todos os testes    
Vá até a pasta raiz/target e digite no terminal:    
  
-> java -jar revolut-account-0.0.1-SNAPSHOT-jar-with-dependencies.jar

** Atenção, você deve ter as variáveis JAVA e MAVEN exportadas corretamente para o SO Path    
  
  
Swagger: http://localhost:4567/echo  
  
  
# racional
Embora o teste tenha citado pescaria, criamos com o nome BecommerceInventory que pode prover a API para alguma pescaria  
Foi criado 2 dominios: Inventory e Product  
As agregações foram feitas com String ID, para diminuir acomplamento do InventoryItem à entidade Product (para caso futuramente faça sentido criar um microservico de Products)  

# estrutura
A aplicação foi dividida em 2 pacotes principais:  
  
  - core (nosso domain model, a ideia é ter somente classes de negocio e abstrações para nao dependermos de framework)  
  - infrastructure (criada para implementar a parte de infraestrutura do nosso domain model, as dependencias com framework aqui serão explicitas)  
  
# idioma
  
A aplicacao foi inteira desenhada em ingles, para seguir um unico padrao de idioma  
E não ficarmos aportuguesando alguns verbos em ingles  
  