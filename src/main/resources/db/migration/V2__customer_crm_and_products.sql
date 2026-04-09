ALTER TABLE enterprise
    ADD COLUMN brand_advantage VARCHAR(1000);

ALTER TABLE customer_lead
    ADD COLUMN gender VARCHAR(16),
    ADD COLUMN age INT,
    ADD COLUMN email VARCHAR(128),
    ADD COLUMN wechat_no VARCHAR(64),
    ADD COLUMN region VARCHAR(128),
    ADD COLUMN last_follow_up_at DATETIME,
    ADD COLUMN archived BIT NOT NULL DEFAULT b'0';

CREATE TABLE customer_follow_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lead_id BIGINT NOT NULL,
    follow_at DATETIME NOT NULL,
    contact_method VARCHAR(32),
    communication_summary TEXT,
    customer_need TEXT,
    our_feedback TEXT,
    customer_feedback TEXT,
    next_action TEXT,
    next_follow_up_at DATETIME,
    level_before VARCHAR(16),
    level_after VARCHAR(16),
    owner_name VARCHAR(64),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    enterprise_id BIGINT NOT NULL,
    enterprise_name VARCHAR(128),
    name VARCHAR(128) NOT NULL,
    specification VARCHAR(255),
    category VARCHAR(64),
    applicable_people VARCHAR(255),
    price DECIMAL(10, 2),
    image_url VARCHAR(255),
    service_process TEXT,
    regulatory_info TEXT,
    description TEXT,
    active BIT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE product_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    applicable_scene VARCHAR(255),
    price DECIMAL(10, 2),
    description TEXT,
    active BIT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE product_package_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(128),
    enterprise_name VARCHAR(128),
    quantity INT NOT NULL,
    item_note VARCHAR(255),
    sort_order INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE customer_recommendation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    lead_id BIGINT NOT NULL,
    recommendation_reason TEXT,
    remark TEXT,
    owner_name VARCHAR(64),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE customer_recommendation_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recommendation_id BIGINT NOT NULL,
    item_type VARCHAR(16) NOT NULL,
    item_id BIGINT NOT NULL,
    item_name VARCHAR(128),
    enterprise_name VARCHAR(128),
    priority_no INT,
    quoted_price DECIMAL(10, 2),
    note VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE INDEX idx_customer_follow_record_lead ON customer_follow_record (lead_id, follow_at);
CREATE INDEX idx_product_enterprise ON product (enterprise_id);
CREATE INDEX idx_product_package_item_package ON product_package_item (package_id);
CREATE INDEX idx_customer_recommendation_lead ON customer_recommendation (lead_id, created_at);
CREATE INDEX idx_customer_recommendation_item_recommendation ON customer_recommendation_item (recommendation_id);
