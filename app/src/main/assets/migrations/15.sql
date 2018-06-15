CREATE TABLE tracker (Id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, path TEXT, created_by INTEGER, created_at TEXT, pushed INTEGER);
ALTER TABLE attachment ADD COLUMN tracker_id INTEGER DEFAULT 0;
UPDATE attachment SET tracker_id = 0;