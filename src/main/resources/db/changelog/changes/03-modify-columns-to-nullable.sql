--liquibase formatted sql

-- changeset jasmin:3
ALTER TABLE inventory_item
ALTER COLUMN length DROP NOT NULL,
ALTER COLUMN width DROP NOT NULL,
ALTER COLUMN height DROP NOT NULL,
ALTER COLUMN weight_value DROP NOT NULL,
ALTER COLUMN weight_unit DROP NOT NULL,
ALTER COLUMN is_sensitive DROP NOT NULL,
ALTER COLUMN packaging_type DROP NOT NULL;

-- rollback
ALTER TABLE inventory_item
ALTER COLUMN length SET NOT NULL,
ALTER COLUMN width SET NOT NULL,
ALTER COLUMN height SET NOT NULL,
ALTER COLUMN weight_value SET NOT NULL,
ALTER COLUMN weight_unit SET NOT NULL,
ALTER COLUMN is_sensitive SET NOT NULL,
ALTER COLUMN packaging_type SET NOT NULL;