CREATE UNIQUE INDEX IF NOT EXISTS uq_batch_product_lot
    ON batches (product_id, lot_number);