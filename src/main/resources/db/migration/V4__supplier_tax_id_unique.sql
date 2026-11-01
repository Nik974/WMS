CREATE UNIQUE INDEX IF NOT EXISTS uq_supplier_tax_id
    ON supplier (tax_id)
    WHERE tax_id IS NOT NULL;