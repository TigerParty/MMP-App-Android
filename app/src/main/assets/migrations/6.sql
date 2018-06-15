CREATE TABLE localproject (title TEXT, Id INTEGER PRIMARY KEY AUTOINCREMENT, created_by INTEGER, description TEXT, project_id INTEGER, default_form_id INTEGER, district_id INTEGER, deleted_at TEXT, created_at TEXT, project_type TEXT);
ALTER TABLE project ADD COLUMN project_type CHAR(255);
ALTER TABLE report ADD COLUMN project_type CHAR(255);
UPDATE project SET server_project_id = NULL;
UPDATE report SET server_project_id = NULL;
UPDATE report SET project_type = "server" WHERE pushed = 1;
UPDATE report SET project_type = "new" WHERE pushed = 0;
