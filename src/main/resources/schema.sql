DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS categories;

CREATE TABLE categories (
    id serial PRIMARY KEY,
    name varchar(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE items (
    id serial PRIMARY KEY,
    name varchar(50) NOT NULL,
    category_id integer,
    amount varchar(20),
    deadline date,
    purchase_date date,
    others varchar(100),
    status int,
    favorite_flg boolean DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);
