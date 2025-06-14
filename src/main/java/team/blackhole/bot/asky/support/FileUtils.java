package team.blackhole.bot.asky.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Утильный класс для работы с файлами
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    /** Регулярное выражение для разбора строки с размером файла. */
    private static final Pattern SIZE_PATTERN = Pattern.compile("(?i)(\\d+(?:\\.\\d+)?)\\s*([KMGTP]?B?)");

    /** Базовый множитель для байтов (1) */
    private static final long BYTE_MULTIPLIER = 1L;

    /** Множитель для килобайтов (1024 байт) */
    private static final long KB_MULTIPLIER = 1024L;

    /** Множитель для мегабайтов (1024 килобайт) */
    private static final long MB_MULTIPLIER = KB_MULTIPLIER * 1024;

    /** Множитель для гигабайтов (1024 мегабайт) */
    private static final long GB_MULTIPLIER = MB_MULTIPLIER * 1024;

    /** Множитель для терабайтов (1024 гигабайт) */
    private static final long TB_MULTIPLIER = GB_MULTIPLIER * 1024;

    /** Множитель для петабайтов (1024 терабайт) */
    private static final long PB_MULTIPLIER = TB_MULTIPLIER * 1024;

    /**
     * Разбирает человеко-читаемое значение размера файла
     * @param size размер для разбора
     * @return количество байтов
     */
    public static long parseFileSize(String size) {
        final var matcher = SIZE_PATTERN.matcher(size.trim());

        if (!matcher.find()) {
            throw new IllegalArgumentException("Неправильный формат размера файла: " + size);
        }

        final double value = Double.parseDouble(matcher.group(1));
        final String unit = matcher.group(2).toUpperCase();

        return (long) (value * getMultiplier(unit));
    }

    /**
     * Возвращает множитель для получения размера файла
     * @param unit единица измерения
     * @return множитель размера файла
     */
    private static long getMultiplier(String unit) {
        return switch (unit) {
            case "B" -> 1L;
            case "KB", "K" -> KB_MULTIPLIER;
            case "MB", "M" -> MB_MULTIPLIER;
            case "GB", "G" -> GB_MULTIPLIER;
            case "TB", "T" -> TB_MULTIPLIER;
            case "PB", "P" -> PB_MULTIPLIER;
            default -> throw new IllegalArgumentException(
                    "Неподдерживаемая единица измерения: " + unit
            );
        };
    }
}
