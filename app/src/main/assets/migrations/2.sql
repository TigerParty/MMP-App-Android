ALTER TABLE project ADD COLUMN server_project_id INTEGER;
UPDATE project SET server_project_id = project_id;