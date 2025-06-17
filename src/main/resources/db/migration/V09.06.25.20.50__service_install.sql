-- ТАБЛИЦА ЧАТОВ
CREATE TABLE chat (
    -- УНИКАЛЬНЫЙ ИДЕНТИФИКАТОР (АВТОИНКРЕМЕНТ, НЕ МОЖЕТ БЫТЬ NULL)
    id BIGSERIAL NOT NULL,
    -- ИДЕНТИФИКАТОР ЧАТА НА СТОРОНЕ КАНАЛА (НЕ МОЖЕТ БЫТЬ NULL)
    channel_chat_id VARCHAR(255) NOT NULL,
    -- ИДЕНТИФИКАТОР КАНАЛА (НЕ МОЖЕТ БЫТЬ NULL)
    channel_id VARCHAR(255) NOT NULL,
    -- ДАТА И ВРЕМЯ СОЗДАНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ЗАПОЛНЯЕТСЯ)
    created_at TIMESTAMP(6) NOT NULL,
    -- ДАТА И ВРЕМЯ ОБНОВЛЕНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ОБНОВЛЯЕТСЯ)
    updated_at TIMESTAMP(6) NOT NULL,
    -- ДАТА И ВРЕМЯ БАНИРОВАНИЯ
    banned_at TIMESTAMP(6),
    -- ОПРЕДЕЛЕНИЕ ПЕРВИЧНОГО КЛЮЧА
    PRIMARY KEY (id)
);

-- СОЗДАНИЕ ИНДЕКСА ДЛЯ ПОИСКА ПО ИДЕНТИФИКАТОРАМ КАНАЛА И ЧАТА
CREATE UNIQUE INDEX idx_chat_channel_chat_id_channel_id ON chat (channel_chat_id, channel_id);

-- ТАБЛИЦА ХАБОВ
CREATE TABLE hub (
    -- УНИКАЛЬНЫЙ ИДЕНТИФИКАТОР (АВТОИНКРЕМЕНТ, НЕ МОЖЕТ БЫТЬ NULL)
    id BIGSERIAL NOT NULL,
    -- ИДЕНТИФИКАТОР ХАБА НА СТОРОНЕ КАНАЛА (НЕ МОЖЕТ БЫТЬ NULL)
    channel_hub_id VARCHAR(255) NOT NULL,
    -- ИДЕНТИФИКАТОР КАНАЛА (НЕ МОЖЕТ БЫТЬ NULL)
    channel_id VARCHAR(255) NOT NULL,
    -- НАИМЕНОВАНИЕ ХАБА (НЕ МОЖЕТ БЫТЬ NULL)
    name VARCHAR(255) NOT NULL,
    -- ДАТА И ВРЕМЯ СОЗДАНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ЗАПОЛНЯЕТСЯ)
    created_at TIMESTAMP(6) NOT NULL,
    -- ДАТА И ВРЕМЯ ОБНОВЛЕНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ОБНОВЛЯЕТСЯ)
    updated_at TIMESTAMP(6) NOT NULL,
    -- ОПРЕДЕЛЕНИЕ ПЕРВИЧНОГО КЛЮЧА
    PRIMARY KEY (id)
);

-- СОЗДАНИЕ ИНДЕКСА ДЛЯ ПОИСКА ПО ИДЕНТИФИКАТОРАМ КАНАЛА И ХАБА
CREATE UNIQUE INDEX idx_hub_channel_hub_id_channel_id ON hub (channel_hub_id, channel_id);

-- ТАБЛИЦА ОБРАЩЕНИЙ
CREATE TABLE ticket (
    -- УНИКАЛЬНЫЙ ИДЕНТИФИКАТОР (АВТОИНКРЕМЕНТ, НЕ МОЖЕТ БЫТЬ NULL)
    id BIGSERIAL NOT NULL,
    -- СУБЪЕКТ ОБРАЩЕНИЯ (НЕ МОЖЕТ БЫТЬ NULL, МАКСИМАЛЬНАЯ ДЛИНА 512 СИМВОЛОВ)
    subject VARCHAR(512) NOT NULL,
    -- ИДЕНТИФИКАТОР ЧАТА (ВНЕШНИЙ КЛЮЧ, НЕ МОЖЕТ БЫТЬ NULL)
    chat_id BIGINT NOT NULL,
    -- СТАТУС ОБРАЩЕНИЯ (НЕ МОЖЕТ БЫТЬ NULL)
    status VARCHAR(50) NOT NULL,
    -- ДАТА И ВРЕМЯ СОЗДАНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ЗАПОЛНЯЕТСЯ)
    created_at TIMESTAMP(6) NOT NULL,
    -- ДАТА И ВРЕМЯ ОБНОВЛЕНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ОБНОВЛЯЕТСЯ)
    updated_at TIMESTAMP(6) NOT NULL,
    -- ОПРЕДЕЛЕНИЕ ПЕРВИЧНОГО КЛЮЧА
    PRIMARY KEY (id),
    -- ОГРАНИЧЕНИЕ ВНЕШНЕГО КЛЮЧА ДЛЯ СВЯЗИ С ЧАТОМ
    CONSTRAINT fk_ticket_chat FOREIGN KEY (chat_id) REFERENCES chat (id)
);

-- СОЗДАНИЕ ИНДЕКСОВ ДЛЯ ТАБЛИЦЫ ОБРАЩЕНИЙ
CREATE INDEX idx_ticket_status ON ticket (status);
CREATE INDEX idx_ticket_chat_id ON ticket (chat_id);

-- ТАБЛИЦА ТЕМ ХАБА
CREATE TABLE hub_topic (
    -- УНИКАЛЬНЫЙ ИДЕНТИФИКАТОР (АВТОИНКРЕМЕНТ, НЕ МОЖЕТ БЫТЬ NULL)
    id BIGSERIAL NOT NULL,
    -- ИДЕНТИФИКАТОР ТЕМЫ НА СТОРОНЕ ХАБА (НЕ МОЖЕТ БЫТЬ NULL)
    hub_topic_id VARCHAR NOT NULL,
    -- ИДЕНТИФИКАТОР ХАБА (ВНЕШНИЙ КЛЮЧ, НЕ МОЖЕТ БЫТЬ NULL)
    hub_id BIGINT NOT NULL,
    -- ИДЕНТИФИКАТОР ОБРАЩЕНИЯ (ВНЕШНИЙ КЛЮЧ, НЕ МОЖЕТ БЫТЬ NULL)
    ticket_id BIGINT NOT NULL,
    -- ДАТА И ВРЕМЯ СОЗДАНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ЗАПОЛНЯЕТСЯ)
    created_at TIMESTAMP(6) NOT NULL,
    -- ДАТА И ВРЕМЯ ОБНОВЛЕНИЯ (НЕ МОЖЕТ БЫТЬ NULL, АВТОМАТИЧЕСКИ ОБНОВЛЯЕТСЯ)
    updated_at TIMESTAMP(6) NOT NULL,
    -- ДАТА И ВРЕМЯ ДЛЯ УДАЛЕНИЯ ТЕМЫ
    delete_topic_after TIMESTAMP(6),
    -- ОПРЕДЕЛЕНИЕ ПЕРВИЧНОГО КЛЮЧА
    PRIMARY KEY (id),
    -- ОГРАНИЧЕНИЯ ВНЕШНИХ КЛЮЧЕЙ
    CONSTRAINT fk_hub_topic_hub FOREIGN KEY (hub_id) REFERENCES hub (id),
    CONSTRAINT fk_hub_topic_ticket FOREIGN KEY (ticket_id) REFERENCES ticket (id)
);

-- СОЗДАНИЕ ИНДЕКСОВ ДЛЯ ТАБЛИЦЫ ТЕМ ХАБА
CREATE UNIQUE INDEX idx_hub_topic_ticket_id_hub_id ON hub_topic (ticket_id, hub_id);
CREATE UNIQUE INDEX idx_hub_topic_hub_topic_id_hub_id ON hub_topic (hub_topic_id, hub_id);
CREATE INDEX idx_hub_topic_ticket_id ON hub_topic (ticket_id);
CREATE INDEX idx_hub_topic_hub_id ON hub_topic (hub_id);
CREATE INDEX idx_hub_topic_hub_topic_id ON hub_topic (hub_topic_id);
