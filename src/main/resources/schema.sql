DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS shopping_list;

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

CREATE TABLE IF NOT EXISTS shopping_list (
    id serial PRIMARY KEY,
    item_id integer REFERENCES items(id),
    user_id integer,  -- ユーザー管理している場合
    status boolean DEFAULT true,
    added_at timestamp DEFAULT CURRENT_TIMESTAMP,
    purchased_flg boolean DEFAULT false,
    name varchar(50),
    amount varchar(20),
    category_id integer,
    category_name varchar(50),
    purchase_date date
);

