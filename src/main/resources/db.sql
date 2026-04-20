create table currencies
(
	id        INTEGER PRIMARY KEY AUTOINCREMENT,
	code      VARCHAR(3) UNIQUE,
	full_name VARCHAR(20) UNIQUE,
	sign      VARCHAR(4) UNIQUE
);

insert into currencies(code, full_name, sign)
values ("AUD", "Australian dollar", "A$");

insert into currencies(code, full_name, sign)
values ("RUB", "Russian Ruble", "₽");

create table exchange_rates
(
	id                 INTEGER PRIMARY KEY AUTOINCREMENT,
	base_currency_id   INTEGER,
	target_currency_id INTEGER,
	rate               DECIMAL(6),
	CONSTRAINT UC_exchange_rates UNIQUE (base_currency_id, target_currency_id),
	FOREIGN KEY (base_currency_id) REFERENCES currencies (id),
	FOREIGN KEY (target_currency_id) REFERENCES currencies (id)
);

