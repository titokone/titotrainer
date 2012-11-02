BEGIN TRANSACTION;
ALTER TABLE task ADD COLUMN modificationtime timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP;
UPDATE task SET modificationtime = creationtime;
ALTER TABLE task ALTER COLUMN modificationtime DROP DEFAULT;
COMMIT;
