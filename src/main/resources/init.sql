CREATE TABLE IF NOT EXISTS exchange_rates
(
	id                 INTEGER PRIMARY KEY AUTOINCREMENT,
	base_currency_id   INTEGER,
	target_currency_id INTEGER,
	rate               DECIMAL(6),
	CONSTRAINT UC_exchange_rates UNIQUE (base_currency_id, target_currency_id),
	FOREIGN KEY (base_currency_id) REFERENCES currencies (id),
	FOREIGN KEY (target_currency_id) REFERENCES currencies (id)
);

CREATE TABLE IF NOT EXISTS currencies
(
	id        INTEGER PRIMARY KEY AUTOINCREMENT,
	code      VARCHAR(3) UNIQUE,
	full_name VARCHAR(20) NOT NULL,
	sign      VARCHAR(3)  NOT NULL
);

INSERT INTO currencies(code, full_name, sign)
VALUES('USD', 'US Dollar', '$');

INSERT INTO currencies(code, full_name, sign)
VALUES('RUB', 'Russian ruble', '₽');

INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
VALUES (1, 2, 72.97);