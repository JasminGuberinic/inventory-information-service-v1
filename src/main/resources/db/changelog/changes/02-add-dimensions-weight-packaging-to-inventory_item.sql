--liquibase formatted sql

-- changeset jasmin:2
-- alter inventory_item table

ALTER TABLE inventory_item
ADD COLUMN length DOUBLE NOT NULL,
ADD COLUMN width DOUBLE NOT NULL,
ADD COLUMN height DOUBLE NOT NULL,
ADD COLUMN weight_value DOUBLE NOT NULL,
ADD COLUMN weight_unit VARCHAR(10) NOT NULL DEFAULT 'kg',
ADD COLUMN is_sensitive BOOLEAN NOT NULL,
ADD COLUMN packaging_type VARCHAR(255) NOT NULL;