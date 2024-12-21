# SPRING BOOT APPLICATION FOR INVOICE API #
## Prerequisites ##
* Jdk 21
* gradle 8+
* postgres

## Steps ##
#### 1. Set Up Database ###
##### Pull postgres Image #####
```
docker pull postgres
```
###### Run Container ######
```
docker run --name some-postgres -e POSTGRES_PASSWORD=<databasePassword> -d postgres
```
###### Create Table ######
* Invoice Table
```postgresql

create table public.invoice
(
    invoice_id               varchar not null
        constraint invoice_pk
            primary key,
    invoice_num              varchar not null,
    gross_amount             real    not null,
    gst_amount               real    not null,
    net_amount               real    not null,
    receipt_date             date    not null,
    payment_due_date         date    not null,
    total_number_transaction numeric not null
);
```
* Transaction Table
```postgresql
create table public.transaction
(
    transaction_id         varchar not null
        constraint transaction_pk
            primary key,
    date_received          date    not null,
    transaction_date       date    not null,
    invoice_id             varchar not null
        constraint transaction_invoice_invoice_id_fk
            references public.invoice,
    invoice_number         varchar not null,
    billing_period_start   date,
    billing_period_end     date,
    net_transaction_amount real    not null,
    gst_amount             real    not null
);
```
#### 2. Run Invoice API Application ####
Run Below Command to start the application
* Change ```< jdbcUrl> ```, ```<databaseusername>``` and ```<databasepassword>```
* Change Basic Credential ```<username>``` and ```<password>```
```
./gradlew bootRun --args='--spring.datasource.url="<jdbcUrl>" --spring.datasource.username=<databaseusername> --spring.datasource.password=<databasepassword> --basic.auth.username=<userName> --basic.auth.password=<password>' 
```
For current sample one:
```
./gradlew bootRun --args='--spring.datasource.password=mysecretpassword --spring.datasource.username=postgres --spring.datasource.url="jdbc:postgresql://localhost:5432/invoice" --basic.auth.username=user --basic.auth.password=password' 
```
#### 3. Use Invoice API #####
After the application is running, you can API Definition through ```http://<hostUrl>:8080/swagger-ui/index.html``` eg: ```http://localhost:8080/swagger-ui/index.html```

Also, there is postman_collection.json as sample under apiCollections folder.

#### 4. Run Unit Test #####
Run Below Command
```
./gradlew test
```