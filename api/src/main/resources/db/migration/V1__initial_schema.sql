-- ============================================
-- 1. TABLA DE ROLES
-- ============================================

CREATE TABLE IF NOT EXISTS roles
(
    id          UUID PRIMARY KEY         DEFAULT uuidv7(),
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name, description)
VALUES ('CLIENT', 'Usuario que publica trabajos y contrata profesionales.'),
       ('PROFESSIONAL', 'Usuario que ofrece servicios y envía ofertas.'),
       ('ADMIN', 'Administrador del sistema con permisos de supervisión.')
ON CONFLICT (name) DO NOTHING;



-- ============================================
-- 2. TABLA DE USUARIOS
-- ============================================

CREATE TABLE IF NOT EXISTS users
(
    id            UUID PRIMARY KEY         DEFAULT uuidv7(),
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('CLIENT', 'PROFESSIONAL', 'ADMIN')),
    is_active     BOOLEAN                  DEFAULT TRUE,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);



-- ============================================
-- 3. TABLA DE RELACIÓN USUARIO - ROL (Many-to-Many)
-- ============================================
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);



-- ============================================
-- 4. TABLA DE CATEGORÍAS
-- ============================================

CREATE TABLE IF NOT EXISTS categories
(
    id          UUID PRIMARY KEY         DEFAULT uuidv7(),
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_name ON categories (name);

INSERT INTO categories (name, description)
VALUES ('PLOMERIA', 'Reparación e instalación de tuberías, grifos y sanitarios.'),
       ('ELECTRICIDAD', 'Instalación y reparación de sistemas eléctricos e iluminación.'),
       ('CARPINTERIA', 'Trabajos en madera, muebles y estructuras.'),
       ('PINTURA', 'Pintura de interiores, exteriores y acabados decorativos.'),
       ('JARDINERIA', 'Mantenimiento de jardines, poda y paisajismo.'),
       ('DESARROLLO WEB', 'Desarrollo de sitios web, aplicaciones y sistemas a medida.'),
       ('DESARROLLO MOVIL', 'Aplicaciones nativas e híbridas para iOS y Android.'),
       ('DISEÑO GRAFICO', 'Diseño de logos, branding, material publicitario.'),
       ('FOTOGRAFIA', 'Fotografía de eventos, productos o corporativa.')
ON CONFLICT (name) DO NOTHING;



-- ============================================
-- 5. TABLA DE TRABAJOS (JOBS)
-- ============================================

CREATE TABLE IF NOT EXISTS jobs
(
    id                UUID PRIMARY KEY         DEFAULT uuidv7(),
    title             VARCHAR(255)   NOT NULL,
    description       TEXT           NOT NULL,
    category_id       UUID           NOT NULL REFERENCES categories (id),
    budget_amount     NUMERIC(10, 2) NOT NULL,
    budget_currency   VARCHAR(3)               DEFAULT 'USD',
    street            VARCHAR(255),
    city              VARCHAR(100),
    latitude          DECIMAL(10, 8),
    longitude         DECIMAL(11, 8),
    client_id         UUID           NOT NULL REFERENCES users (id),
    selected_offer_id UUID           NULL, -- Se llena al aceptar una oferta
    status            VARCHAR(30)    NOT NULL  DEFAULT 'PUBLISHED'
        CHECK (status IN ('PUBLISHED', 'IN_PROGRESS', 'PENDING_CONFIRMATION', 'COMPLETED', 'CANCELLED', 'HIDDEN')),
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_jobs_client ON jobs (client_id);
CREATE INDEX idx_jobs_category ON jobs (category_id);
CREATE INDEX idx_jobs_status ON jobs (status);
CREATE INDEX idx_jobs_location ON jobs (latitude, longitude);



-- ============================================
-- 6. TABLA DE OFERTAS (OFFERS)
-- ============================================
CREATE TABLE IF NOT EXISTS offers
(
    id              UUID PRIMARY KEY         DEFAULT uuidv7(),
    job_id          UUID           NOT NULL REFERENCES jobs (id) ON DELETE CASCADE,
    professional_id UUID           NOT NULL REFERENCES users (id),
    offered_price   NUMERIC(10, 2) NOT NULL,
    message         TEXT,
    status          VARCHAR(20)    NOT NULL  DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'WITHDRAWN')),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (job_id, professional_id) -- Un profesional no puede ofertar 2 veces al mismo trabajo
);

CREATE INDEX idx_offers_job ON offers (job_id);
CREATE INDEX idx_offers_professional ON offers (professional_id);



-- ============================================
-- 7. TABLA DE TRANSACCIONES (TRANSACTIONS)
-- ============================================
CREATE TABLE IF NOT EXISTS transactions
(
    id                       UUID PRIMARY KEY         DEFAULT uuidv7(),
    job_id                   UUID           NOT NULL UNIQUE REFERENCES jobs (id),
    client_id                UUID           NOT NULL REFERENCES users (id),
    professional_id          UUID           NOT NULL REFERENCES users (id),
    amount                   NUMERIC(10, 2) NOT NULL,
    currency                 VARCHAR(3)               DEFAULT 'USD',
    stripe_payment_intent_id VARCHAR(255),
    status                   VARCHAR(20)    NOT NULL  DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'CAPTURED', 'RELEASED', 'REFUNDED', 'FAILED')),
    created_at               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_job ON transactions(job_id);
CREATE INDEX idx_transactions_stripe ON transactions(stripe_payment_intent_id);



-- ============================================
-- CLAVE FORÁNEA DIFERIDA: jobs.selected_offer_id
-- Se añade después para evitar dependencia circular
-- ============================================
ALTER TABLE jobs ADD CONSTRAINT fk_jobs_selected_offer
    FOREIGN KEY (selected_offer_id) REFERENCES offers(id) ON DELETE SET NULL;