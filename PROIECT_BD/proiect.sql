-- Pasul 1: Identificarea celei mai mari tabele din baza de date Sakila
USE sakila;

SELECT 
    TABLE_NAME AS Tabel,
    (DATA_LENGTH + INDEX_LENGTH) AS Dimensiune_Bytes,
    DATA_LENGTH AS Dimensiune_Date,
    INDEX_LENGTH AS Dimensiune_Index
FROM 
    information_schema.tables
WHERE 
    TABLE_SCHEMA = 'sakila'
ORDER BY 
    Dimensiune_Bytes DESC
LIMIT 1;

-- Pasul 2: Listarea indecșilor pentru tabela "rental"
SELECT 
    TABLE_NAME AS Tabel,
    INDEX_NAME AS Nume_Index,
    CASE NON_UNIQUE 
        WHEN 0 THEN 'Unique' 
        ELSE 'Non-Unique' 
    END AS Tip_Index,
    INDEX_TYPE AS Tip_Strucura,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS Coloane
FROM 
    information_schema.STATISTICS
WHERE 
    TABLE_SCHEMA = 'sakila' 
    AND TABLE_NAME = 'rental'
GROUP BY 
    INDEX_NAME
ORDER BY 
    INDEX_NAME;
