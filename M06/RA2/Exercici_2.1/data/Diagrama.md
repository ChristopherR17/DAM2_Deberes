## Para visualizar se necesita la siguiente extension
```text
Markdown Preview Mermaid Support 
```

```mermaid
erDiagram
    Faccio ||--o{ Personatge : contiene
    Faccio {
        int id PK
        varchar(15) nom
        varchar(500) resum
    }
    Personatge {
        int id PK
        varchar(15) nom
        float atac
        float defensa
        int idFaccio FK
    }
```