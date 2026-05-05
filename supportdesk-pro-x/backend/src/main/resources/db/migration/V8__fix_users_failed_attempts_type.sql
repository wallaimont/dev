-- V8: Align users.failed_attempts type with JPA mapping

ALTER TABLE users
    ALTER COLUMN failed_attempts TYPE INTEGER USING failed_attempts::integer;
