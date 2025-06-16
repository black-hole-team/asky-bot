/**
 * Обрабатывает получение нового сообщения
 * @param event событие получения нового сообщения
 */
function handle(event) {
    // Выводим событие получения сообщения в лог
    logger.info(getLogMessage(event.getMessage()));
}

/**
 * Возвращает сообщение для логгирования
 * @param message поступившее сообщение
 */
function getLogMessage(message) {
    const result = ["Получено"];
    const content = message.content();
    if (content == null || content.length === 0) {
        result.push("пустое сообщение");
    } else {
        result.push(`сообщение '${content}'`);
    }
    const attachments = message.attachments();
    if (attachments == null || attachments.isEmpty()) {
        result.push("без вложений.");
    } else {
        result.push(`с ${attachments.size()} ${plural(attachments.size(), 'вложением', 'вложениями', 'вложениями')}`);
    }
    return result.join(" ");
}

/**
 * Возвращает правильную форму существительного для русского языка в зависимости от числа.
 * Соответствует правилам склонения для количественных числительных (1 яблоко, 2 яблока, 5 яблок).
 * @param number число, для которого определяется форма существительного
 * @param one    форма для единственного числа (1) (пример: "яблоко")
 * @param few    форма для чисел 2-4 (пример: "яблока")
 * @param many   форма для чисел 5-0 и исключений (пример: "яблок")
 * @returns соответствующая форма существительного
 */
function plural(number, one, few, many) {
    const n = Math.abs(number) % 100;
    if (n > 10 && n < 20) {
        return many;
    }
    const n1 = number % 10;
    if (n1 > 1 && n1 < 5) {
        return few;
    }
    if (n1 === 1) {
        return one;
    }
    return many;
}