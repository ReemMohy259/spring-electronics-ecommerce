-- ============================================================
-- PostgreSQL Schema generated from ER Diagram
-- ============================================================

-- ------------------------------------------------------------
-- Category
-- ------------------------------------------------------------
CREATE TABLE category
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- ------------------------------------------------------------
-- Merchant
-- ------------------------------------------------------------
CREATE TABLE merchant
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(150) NOT NULL,
    about TEXT
);

-- ------------------------------------------------------------
-- User
-- ------------------------------------------------------------
CREATE TABLE "user"
(
    id              SERIAL PRIMARY KEY,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    username        VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    role            VARCHAR(50) DEFAULT 'customer',
    job             VARCHAR(100),
    birth_date      DATE,
    profile_pic_url TEXT,
    deleted         BOOLEAN     DEFAULT FALSE
);

-- ------------------------------------------------------------
-- Address
-- ------------------------------------------------------------
CREATE TABLE address
(
    id          SERIAL PRIMARY KEY,
    user_id     INT NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    government  VARCHAR(100),
    city        VARCHAR(100),
    street      VARCHAR(150),
    building_no VARCHAR(20),
    description TEXT
);

-- ------------------------------------------------------------
-- Order
-- ------------------------------------------------------------
CREATE TABLE "order"
(
    id          SERIAL PRIMARY KEY,
    user_id     INT            NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    total_price NUMERIC(12, 2) NOT NULL DEFAULT 0,
    status      VARCHAR(50)    NOT NULL DEFAULT 'pending',
    timestamp   TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

-- ------------------------------------------------------------
-- Cart  (1-to-1 with User)
-- ------------------------------------------------------------
CREATE TABLE cart
(
    id             SERIAL PRIMARY KEY,
    user_id        INT            NOT NULL UNIQUE REFERENCES "user" (id) ON DELETE CASCADE,
    total_quantity INT            NOT NULL DEFAULT 0,
    total_price    NUMERIC(12, 2) NOT NULL DEFAULT 0
);

-- ------------------------------------------------------------
-- Product
-- ------------------------------------------------------------
CREATE TABLE product
(
    id              SERIAL PRIMARY KEY,
    merchant_id     INT            NOT NULL REFERENCES merchant (id) ON DELETE CASCADE,
    name            VARCHAR(255)   NOT NULL,
    description     TEXT,
    price           NUMERIC(12, 2) NOT NULL,
    stock_quantity  INT            NOT NULL DEFAULT 0,
    sold_units      INT            NOT NULL DEFAULT 0,
    sku             VARCHAR(100) UNIQUE,
    image_url       TEXT,
    additional_info TEXT,
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    deleted         BOOLEAN        NOT NULL DEFAULT FALSE
);

-- Order ↔ Product
CREATE TABLE order_item
(
    id            SERIAL PRIMARY KEY,
    order_id      INT            NOT NULL REFERENCES "order" (id) ON DELETE CASCADE,
    product_id    INT            NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    quantity      INT            NOT NULL DEFAULT 1,
    current_price NUMERIC(12, 2) NOT NULL,
    UNIQUE (order_id, product_id)
);

-- Cart ↔ Product
CREATE TABLE cart_item
(
    id         SERIAL PRIMARY KEY,
    cart_id    INT NOT NULL REFERENCES cart (id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    quantity   INT NOT NULL DEFAULT 1,
    UNIQUE (cart_id, product_id)
);

-- User ↔ Product (reviews)
CREATE TABLE review
(
    id         SERIAL   PRIMARY KEY,
    user_id    INT      NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    product_id INT      NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    comment    TEXT,
    rating     SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    UNIQUE (user_id, product_id)
);

-- User ↔ Product (wishlist)
CREATE TABLE wishlist
(
    id         SERIAL PRIMARY KEY,
    user_id    INT NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    UNIQUE (user_id, product_id)
);

-- Product ↔ Category
CREATE TABLE product_category
(
    id          SERIAL PRIMARY KEY,
    product_id  INT NOT NULL REFERENCES product (id) ON DELETE CASCADE,
    category_id INT NOT NULL REFERENCES category (id) ON DELETE CASCADE,
    UNIQUE (product_id, category_id)
);
