-- Добавляем в таблицу хабов столбец типа
ALTER TABLE hub ADD COLUMN type VARCHAR NOT NULL DEFAULT 'GROUP';

-- Очищаем таблицу тем обращений
DELETE FROM hub_topic;

-- Очищаем таблицу обращений
DELETE FROM ticket;

-- Добавляем в таблицу обращений столбец идентификатора пользователя
ALTER TABLE ticket ADD COLUMN user_id BIGINT NOT NULL;