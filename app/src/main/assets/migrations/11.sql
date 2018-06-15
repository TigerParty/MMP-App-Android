ALTER TABLE project ADD COLUMN parent_id INTEGER;
ALTER TABLE project ADD COLUMN container_id INTEGER;
ALTER TABLE localproject ADD COLUMN parent_id INTEGER;
ALTER TABLE localproject ADD COLUMN container_id INTEGER;
CREATE TABLE container (Id INTEGER PRIMARY KEY AUTOINCREMENT,'container_id' INTEGER, 'name' TEXT, 'parent_id' INTEGER, 'form_id' INTEGER, 'reportable' INTEGER);