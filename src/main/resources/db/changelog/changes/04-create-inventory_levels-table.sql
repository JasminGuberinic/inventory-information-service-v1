
--liquibase formatted sql

-- changeset jasmin:4
-- create inventory_levels table

CREATE TABLE inventory_levels (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    location_code VARCHAR(255) NOT NULL,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL,
    last_updated VARCHAR(255) NOT NULL,
    UNIQUE(item_id, location_code)
);