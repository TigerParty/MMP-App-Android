CREATE TABLE relation_project_belong_region (Id INTEGER PRIMARY KEY AUTOINCREMENT, project_id INTEGER, project_type TEXT, region_id INTEGER);
ALTER TABLE region ADD COLUMN parent_id INTEGER;
ALTER TABLE region ADD COLUMN region_order INTEGER;
ALTER TABLE region ADD COLUMN label_name TEXT;
UPDATE project SET district_id = 0;
UPDATE localproject SET district_id = 0;