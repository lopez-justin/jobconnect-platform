-- Añadir la columna para el ID del profesional seleccionado
ALTER TABLE jobs ADD COLUMN selected_professional_id UUID NULL;

-- Añadir la clave foránea para mantener la integridad referencial
ALTER TABLE jobs ADD CONSTRAINT fk_jobs_selected_professional
    FOREIGN KEY (selected_professional_id) REFERENCES users(id) ON DELETE SET NULL;

-- Índice para mejorar las consultas que filtren por este campo
CREATE INDEX idx_jobs_selected_professional ON jobs(selected_professional_id);