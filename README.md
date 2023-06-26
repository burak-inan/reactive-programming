# reactive-backend-project

**Author:**  
_Burak Inan_   

---
## Before Starting
### Creating Reactive Project,

1. We should add webFlux, r2dbc and reactive dependency of database
* The other dependencies are optional
---
2. We should use reactive syntax to define our database in properties file
---
3. Reactive library couldn't create schemas automatically. So that we have 2 choices;
* We can create bean in application main class. In this case;
  1. we should create sql file in resources and we write a query manually
  2. we can use serial data type for id column. Because it is create id automatically
  3. we will define our sql query location inside the bean
* Or we can create our table directly using database in DBMS
* WARNING: In both case we should be careful to use same name with entity class. Otherwise, r2dbc couldn't find the schema
---
4. We have to import @Id annotation from springframework.data.annotation for Id field in the entity class
---
5. Repositories should be extended to r2dbc or sub interfaces of it
---
6. Reactive programming is still evolving. That's why we don't have every ready-made method in JPA. So it needs a good knowledge of SQL
---
7. We can create our endpoint with 2 way;
* With Bean;
  1. We can create a router bean
  2. In this bean, we can define multiple endpoint
  3. Those endpoints don't use class path
* With traditional method;
  1. We can create separated method for every end point
  2. Those endpoints use class endpoint as we know



---
## Good To Know
### Reactive Programming Tutorials,

1. https://www.youtube.com/watch?v=bXcFCgQsvAE can be watched before starting to the project. It explains logic well without database connection
2. https://www.youtube.com/watch?v=3J_X1srMk3s&t=934s example with MongoDB database

