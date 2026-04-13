INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at) VALUES
('product_category', '康复护理', '康复护理', 1, b'1', NOW(), NOW()),
('product_category', '长者照护', '长者照护', 2, b'1', NOW(), NOW()),
('product_category', '母婴护理', '母婴护理', 3, b'1', NOW(), NOW()),
('product_category', '慢病管理', '慢病管理', 4, b'1', NOW(), NOW()),
('product_category', '居家支持', '居家支持', 5, b'1', NOW(), NOW()),
('product_category', '术后恢复', '术后恢复', 6, b'1', NOW(), NOW()),
('product_applicable_people', '长者', '长者', 1, b'1', NOW(), NOW()),
('product_applicable_people', '术后人群', '术后人群', 2, b'1', NOW(), NOW()),
('product_applicable_people', '慢病人群', '慢病人群', 3, b'1', NOW(), NOW()),
('product_applicable_people', '母婴家庭', '母婴家庭', 4, b'1', NOW(), NOW()),
('product_applicable_people', '康复期人群', '康复期人群', 5, b'1', NOW(), NOW()),
('product_applicable_people', '高需求家庭', '高需求家庭', 6, b'1', NOW(), NOW()),
('package_applicable_scene', '居家照护', '居家照护', 1, b'1', NOW(), NOW()),
('package_applicable_scene', '术后恢复', '术后恢复', 2, b'1', NOW(), NOW()),
('package_applicable_scene', '长者陪护', '长者陪护', 3, b'1', NOW(), NOW()),
('package_applicable_scene', '母婴陪护', '母婴陪护', 4, b'1', NOW(), NOW()),
('package_applicable_scene', '慢病随访', '慢病随访', 5, b'1', NOW(), NOW()),
('package_applicable_scene', '节日关怀', '节日关怀', 6, b'1', NOW(), NOW()),
('enterprise_service_mode', '上门服务', '上门服务', 1, b'1', NOW(), NOW()),
('enterprise_service_mode', '到店服务', '到店服务', 2, b'1', NOW(), NOW()),
('enterprise_service_mode', '远程咨询', '远程咨询', 3, b'1', NOW(), NOW()),
('enterprise_service_mode', '驻点服务', '驻点服务', 4, b'1', NOW(), NOW()),
('enterprise_response_speed', '2小时内', '2小时内', 1, b'1', NOW(), NOW()),
('enterprise_response_speed', '当日响应', '当日响应', 2, b'1', NOW(), NOW()),
('enterprise_response_speed', '24小时内', '24小时内', 3, b'1', NOW(), NOW()),
('enterprise_response_speed', '预约制', '预约制', 4, b'1', NOW(), NOW()),
('enterprise_certification_status', '已认证', '已认证', 1, b'1', NOW(), NOW()),
('enterprise_certification_status', '审核中', '审核中', 2, b'1', NOW(), NOW()),
('enterprise_certification_status', '未认证', '未认证', 3, b'1', NOW(), NOW()),
('enterprise_certification_level', '基础认证', '基础认证', 1, b'1', NOW(), NOW()),
('enterprise_certification_level', '标准认证', '标准认证', 2, b'1', NOW(), NOW()),
('enterprise_certification_level', '高级认证', '高级认证', 3, b'1', NOW(), NOW()),
('enterprise_certification_level', '旗舰认证', '旗舰认证', 4, b'1', NOW(), NOW()),
('enterprise_expertise', '康复护理', '康复护理', 1, b'1', NOW(), NOW()),
('enterprise_expertise', '长者照护', '长者照护', 2, b'1', NOW(), NOW()),
('enterprise_expertise', '母婴护理', '母婴护理', 3, b'1', NOW(), NOW()),
('enterprise_expertise', '慢病管理', '慢病管理', 4, b'1', NOW(), NOW()),
('enterprise_expertise', '营养支持', '营养支持', 5, b'1', NOW(), NOW()),
('enterprise_expertise', '心理支持', '心理支持', 6, b'1', NOW(), NOW());

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'product_category', TRIM(category), TRIM(category), 100, b'1', NOW(), NOW()
FROM product
WHERE category IS NOT NULL AND TRIM(category) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'product_applicable_people', TRIM(applicable_people), TRIM(applicable_people), 100, b'1', NOW(), NOW()
FROM product
WHERE applicable_people IS NOT NULL AND TRIM(applicable_people) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'package_applicable_scene', TRIM(applicable_scene), TRIM(applicable_scene), 100, b'1', NOW(), NOW()
FROM product_package
WHERE applicable_scene IS NOT NULL AND TRIM(applicable_scene) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'enterprise_service_mode', TRIM(service_modes), TRIM(service_modes), 100, b'1', NOW(), NOW()
FROM enterprise
WHERE service_modes IS NOT NULL AND TRIM(service_modes) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'enterprise_response_speed', TRIM(response_speed), TRIM(response_speed), 100, b'1', NOW(), NOW()
FROM enterprise
WHERE response_speed IS NOT NULL AND TRIM(response_speed) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'enterprise_certification_status', TRIM(certification_status), TRIM(certification_status), 100, b'1', NOW(), NOW()
FROM enterprise
WHERE certification_status IS NOT NULL AND TRIM(certification_status) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'enterprise_certification_level', TRIM(certification_level), TRIM(certification_level), 100, b'1', NOW(), NOW()
FROM enterprise
WHERE certification_level IS NOT NULL AND TRIM(certification_level) <> '';

INSERT IGNORE INTO dict_item (dict_type, item_key, item_label, sort_order, enabled, created_at, updated_at)
SELECT 'enterprise_expertise', TRIM(expertise), TRIM(expertise), 100, b'1', NOW(), NOW()
FROM enterprise
WHERE expertise IS NOT NULL AND TRIM(expertise) <> '';
