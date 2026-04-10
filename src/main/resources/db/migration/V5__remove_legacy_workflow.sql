ALTER TABLE customer_lead
    DROP COLUMN city_area,
    DROP COLUMN age_range,
    DROP COLUMN family_structure,
    DROP COLUMN channel_trust_level,
    DROP COLUMN service_object,
    DROP COLUMN urgency,
    DROP COLUMN clear_pain_point,
    DROP COLUMN strong_trust_demand,
    DROP COLUMN decision_maker,
    DROP COLUMN decision_speed,
    DROP COLUMN price_sensitivity,
    DROP COLUMN certification_interest,
    DROP COLUMN workflow_stage;

DROP TABLE IF EXISTS lead_identification;
DROP TABLE IF EXISTS need_assessment;
DROP TABLE IF EXISTS enterprise_match_candidate;
DROP TABLE IF EXISTS enterprise_match;
DROP TABLE IF EXISTS delivery_supervision;
DROP TABLE IF EXISTS after_sales_followup;
DROP TABLE IF EXISTS public_form_token;

DELETE FROM dict_item
WHERE dict_type IN (
    'channel_trust_level',
    'service_object',
    'need_type',
    'service_preference',
    'urgency',
    'current_status',
    'decision_speed',
    'price_sensitivity',
    'certification_interest',
    'goal_type',
    'risk_concern',
    'decision_mode',
    'start_mode',
    'service_mode',
    'certification_status',
    'satisfaction_score',
    'repurchase_possibility',
    'customer_lifecycle_status',
    'enterprise_performance_level',
    'contact_method',
    'product_category',
    'applicable_people'
);

UPDATE sys_role
SET description = CASE role_code
    WHEN 'ROLE_ADMIN' THEN '系统全量管理'
    WHEN 'ROLE_SALES' THEN '客户管理与推荐跟进'
    WHEN 'ROLE_OPERATIONS' THEN '供给维护与业务协同'
    ELSE description
END
WHERE role_code IN ('ROLE_ADMIN', 'ROLE_SALES', 'ROLE_OPERATIONS');
