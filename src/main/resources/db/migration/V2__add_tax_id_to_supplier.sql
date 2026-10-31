ALTER TABLE supplier ADD COLUMN IF NOT EXISTS tax_id VARCHAR(20);
ALTER TABLE supplier ADD CONSTRAINT uq_supplier_tax_id UNIQUE (tax_id);