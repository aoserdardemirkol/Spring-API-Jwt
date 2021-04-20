# Spring-API-Jwt

jwt token ile authorize işlemi ve buna bağlı olarak diğer işlemler gerçekleştirilir

Kullanılanlar
- Spring
- - spring-jpa
- Postgresql
- liquibase
- swagger

DB Entity ve Liquibase ile oluşturulur

swagger
http://localhost:8080/swagger-ui/index.html#/
adresinde kullanılabilir

Araç Tipi, Araç Adı, Kapladığı Alan
- Tip : 1 - Bisiklet  - 1 Birim;
- Tip : 2 - Motorsiklet - 1 Birim;
- Tip : 3 - Araba - 2 Birim;
- Tip : 4 - Kamyonet - 3 Birim;
- Tip : 5 - Otobüs - 5 Birim;
- Tip : 6 - Tır -  8 Birim;	

Token oluşturulmadan önce unauthorized hatası verilir

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/unauthorized2.png)

jwt-create ile token oluşturulur

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/unauthorized.png)

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/unauthorized3.png)

Token authorize kısmından 

- Bearer {token} olarak yazılır.

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/bearer.png)

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/bearer2.png)

Token girişi yapıldıkdan sonra işlemler gerçekleştirilebilir.

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/authorized.png)

![github.small](https://raw.githubusercontent.com/aoserdardemirkol/Spring-API-Jwt/master/pictures/authorized2.png)
