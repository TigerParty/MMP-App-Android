CREATE TABLE permission_level (Id INTEGER PRIMARY KEY AUTOINCREMENT, 'permission_id' INTEGER, 'name' TEXT, 'priority' INTEGER);
CREATE TABLE relation_user_own_project (Id INTEGER PRIMARY KEY AUTOINCREMENT, project_id INTEGER, project_type TEXT, user_id INTEGER);
ALTER TABLE project ADD COLUMN edit_level_id INTEGER;