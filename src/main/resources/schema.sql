CREATE TABLE IF NOT EXISTS receipts (
  id INT UNSIGNED AUTO_INCREMENT,
  uploaded TIME DEFAULT CURRENT_TIME(),
  merchant VARCHAR(255),
  amount DECIMAL(12,2),
  receipt_type INT UNSIGNED,

  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tags (
  id INT UNSIGNED AUTO_INCREMENT,
  label VARCHAR(255),
  receipt_id INT UNSIGNED,
  uploaded TIME DEFAULT CURRENT_TIME(),

  PRIMARY KEY (id)
);