ALTER TABLE customer_lead
    ADD COLUMN customer_value_level VARCHAR(32) NULL AFTER customer_level;

UPDATE dict_item
SET item_label = 'A级高意向',
    sort_order = 1,
    updated_at = NOW()
WHERE dict_type = 'customer_level' AND item_key = 'A';

UPDATE dict_item
SET item_label = 'B级潜力',
    sort_order = 2,
    updated_at = NOW()
WHERE dict_type = 'customer_level' AND item_key = 'B';

UPDATE dict_item
SET item_label = 'C级普通',
    sort_order = 3,
    updated_at = NOW()
WHERE dict_type = 'customer_level' AND item_key = 'C';

UPDATE dict_item
SET item_label = 'D级沉默',
    sort_order = 4,
    updated_at = NOW()
WHERE dict_type = 'customer_level' AND item_key = 'D';

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_level', 'A', 'A级高意向', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_level' AND item_key = 'A'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_level', 'B', 'B级潜力', 2, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_level' AND item_key = 'B'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_level', 'C', 'C级普通', 3, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_level' AND item_key = 'C'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_level', 'D', 'D级沉默', 4, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_level' AND item_key = 'D'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_value_level', 'A', '私定', 1, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_value_level' AND item_key = 'A'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_value_level', 'B', '尊享', 2, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_value_level' AND item_key = 'B'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_value_level', 'C', '基础', 3, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_value_level' AND item_key = 'C'
);

INSERT INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'customer_value_level', 'D', '会员', 4, 1, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM dict_item WHERE dict_type = 'customer_value_level' AND item_key = 'D'
);
