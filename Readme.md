# ⚔️ YggraDB - The Database Forged in the Fires of Yggdrasil ⚔️

**A mythic SQL-like database system where every command is a saga and every table a realm of power**

![Yggdrasil Banner](https://example.com/yggradb-banner.jpg) *(Optional: Add art later)*

## 🌌 Current Features (Asgard-Ready)

### **Database Operations**
```sql
CREATE DATABASE Valhalla;       -- Forge a new realm
USE Valhalla;                  -- Enter the realm
DROP DATABASE Valhalla;        -- Cast into the void
SHOW DATABASES;                -- Behold all realms

-- Create table
CREATE TABLE Warriors (
    id INT,
    name VARCHAR(50),
    strength FLOAT
);

-- Insert data
INSERT INTO Warriors VALUES (1, 'Kratos', 99.9);  -- Add a warrior

-- Delete table
DROP TABLE Warriors;           -- Shatter the table

-- List tables
SHOW TABLES;                   -- List all halls

⚔️ WARRIORS ⚔️
🛡️ | id      | name    | strength | is_immortal |
📜+---------+---------+----------+-------------+
| 1       | Kratos  | 99.9     | true        |
🌊+---------+---------+----------+-------------+
Rows: 1 | Forged in the fires of Nidavellir

CREATE TABLE '; DROP DATABASE Valhalla;--'; 
-- 🔥 [FLAMES OF KRATOS] Forbidden symbols detected!

INSERT INTO Warriors VALUES (1, 'Loki', 85.5); DROP TABLE Warriors;
-- 🌪️ [CHAOS STORM] Data values cannot contain semicolons!

git clone https://github.com/yourname/yggra-db.git
cd yggra-db
mvn package
java -jar target/yggra-db.jar

Command Reference
Command	Example	Output Theme
CREATE DATABASE <name>	CREATE DATABASE Midgard	⚒️ Forged
USE <database>	USE Valhalla	🛡️ Entered
INSERT INTO <table>	INSERT INTO Gods VALUES (...)	⚡ Bounty
Error Handling	CREATE DATABASE ''	💀 Catastrophe

🌠 Future Sagas (Ragnarök Roadmap)
v0.2 - SELECT Queries
SELECT * FROM Warriors WHERE strength > 90;
-- 🏆 [ODIN'S EYE] 3 mighty warriors revealed!

v0.3 - Table Alterations
ALTER TABLE Warriors RENAME TO Valkyries;  -- ✏️ [SKALD'S PEN]
ALTER TABLE Valkyries MODIFY strength INT; -- ⚖️ [NORN'S DECREE]

v0.4 - Advanced Features
Transactions (BEGIN/COMMIT)

JOIN operations

Backup/Restore commands

🐛 Known Limitations
No transaction support yet

Single-command parsing only

Unicode symbols in names untested

🤝 Join the Quest
Contribute as a code skald or mythic tester:

bash
git clone https://github.com/yourname/yggra-db.git
mvn test  # Run the sagas of testing
"Together we shall build a database worthy of the gods!" ⚡
