/**
 * Возвращает субъект обращения
 * @param message  сообщение
 * @param ticketId идентификатор обращения
 * @returns субъект обращения
 */
function getSubject(message, ticketId) {
    return `${ticket}`.padStart(8, "0");
}