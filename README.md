# About ORM16
ORM16 is a library exploring code generation-based approach to ORM for Java 17 and focusing on records as persistent data model.

# Example

### Input Java model
```java
package com.example.model;

@Serialized(context = PERSISTENCE, as = "accounts") // generate JDBC mapping
@Serialized(context = INTEGRATION, format = JSON)   // generate JSON mapping
public record Account(@Id UUID uuid,                // primary key
                      Instant created,              // use standard mapping to TIMESTAMP
                      String username,
                      @Value Locale locale,         // serialize as string value
                      @Mapping(                       // unwrap object (only in database)
                              context = PERSISTENCE, 
                              serializeAs = EMBEDDED,
                              overrides = {
                                      @AttributeOverride(map = "currencyCode", to = "currency")
                              }) 
                      @Mapping(context = INTEGRATION, serializeAs = VALUE) // serialize to string e.g. "100 EUR" 
                      Money balance             
                     ) {
}

public record Money(BigDecimal amount,
                    String currencyCode) {
    public static Money fromString(String value) {
        // parse string representation 
    }
    public String toString() {
        // build string representation of this object
    }
}
```

### Database table in PostgreSQL
```sql
CREATE TABLE accounts (
    uuid             UUID NOT NULL PRIMARY KEY,  // account.uuid()
    created          TIMESTAMP NOT NULL,         // java.sql.Timestamp.from(account.created())
    username         VARCHAR(64) NOT NULL,       // account.username()
    locale           VARCHAR(5) NOT NULL,        // account.locale().toString()
    balance_amount   NUMERIC(10,2) DEFAULT NULL, // account.balance().amount()
    balance_currency CHAR(3) DEFAULT NULL        // account.balance().currencyCode()
)
```
### JSON model
```json
{
  "uuid"      : "92147655-226e-415e-8976-1844a8367ec6",
  "created"   : "2022-01-01T14:30:23.341Z",
  "username"  : "mustermann", 
  "locale"    : "en-US",
  "balance"   : "200 EUR"
}
```

### Generated code
```java
package com.example.model.jdbc;

public class AccountRepository implements Repository<Account, UUID> {
    
    public AccountRepository(JdbcDatabase jdbc) {
        // Inject connection pool / database wrapper
    }
    
    public void add(Account account) {
        // ...
    }
    
    public void update(Account account) {
        // ...
    }
    
    public Optional<Account> findById(UUID uuid) { 
        // ...
    }
    // other applicable methods
}

```

# Planned v1 features

1. Zero use of reflection at runtime
2. Flexible mapping of records with customizable table and column names
3. Separate mapping for persistence (JDBC/Postgres) and integration (JSON) contexts
4. Variety of standard Java type mappings (primitives, Java Time API, UUIDs, URLs, enums)
5. Arbitrary type conversions via factory methods or custom converter classes
6. Unwrapping embedded entities
7. Sensible defaults to reduce verbosity
8. Micronaut microservice demo

# Current open questions and challenges

1. How to **inject repository** in the user code? ServiceLoader, Guice, Spring?
2. **Custom finders**: findByXXX vs Criteria API convention, how to declare interface?
3. **Object tree traversal**: how to model foreign keys and corresponding relationships? How to fetch this data? 
4. **Plugin architecture**: How to support different database dialects?
5. **Caching**: does it make sense in microservice architectures? Should it be part of the solution? 