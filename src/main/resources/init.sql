CREATE TABLE IF NOT EXISTS products (
   uuid UUID PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   price NUMERIC(10, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
   uuid UUID PRIMARY KEY,
   name TEXT NOT NULL,
   email TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS sales (
   id UUID PRIMARY KEY,
   user_id UUID NOT NULL,
   payment_method TEXT NOT NULL,
   FOREIGN KEY (user_id) REFERENCES users(uuid)
);

CREATE TABLE IF NOT EXISTS sale_products (
   sale_id UUID NOT NULL,
   product_id UUID NOT NULL,
   PRIMARY KEY (sale_id, product_id),
   FOREIGN KEY (sale_id) REFERENCES sales(id),
   FOREIGN KEY (product_id) REFERENCES products(uuid)
);