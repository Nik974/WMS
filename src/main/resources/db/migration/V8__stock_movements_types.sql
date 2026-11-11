ALTER TABLE stock_movements
    ADD COLUMN IF NOT EXISTS batch_id BIGINT REFERENCES batches (batch_id);
ALTER TABLE stock_movements
    ADD COLUMN IF NOT EXISTS reference_type VARCHAR(20);
ALTER TABLE stock_movements
    ADD COLUMN IF NOT EXISTS reference_id BIGINT;
ALTER TABLE stock_movements
    ADD COLUMN IF NOT EXISTS comment VARCHAR(500);

CREATE INDEX IF NOT EXISTS ix_movements_reference ON stock_movements (reference_type, reference_id);

ALTER TABLE stock_movements
    DROP CONSTRAINT IF EXISTS chk_movement_type;
ALTER TABLE stock_movements
    ADD CONSTRAINT chk_movement_type
        CHECK (movement_type IN ('RECEIPT', 'SHIPMENT', 'ADJUSTMENT', 'TRANSFER')) NOT VALID;

ALTER TABLE stock_movements
    DROP CONSTRAINT IF EXISTS chk_movement_location;
ALTER TABLE stock_movements
    ADD CONSTRAINT chk_movement_location
        CHECK (from_location_id IS NOT NULL OR to_location_id IS NOT NULL) NOT VALID;