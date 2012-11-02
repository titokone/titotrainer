BEGIN TRANSACTION;
UPDATE task SET modificationtime = NOW() WHERE id IN (SELECT id FROM task WHERE id NOT IN (SELECT taskid FROM input));
INSERT INTO "input" (id, "input", secret, taskid)
  (SELECT nextval('hibernate_sequence'), '', false, id FROM task WHERE id NOT IN (SELECT taskid FROM input));
COMMIT;
