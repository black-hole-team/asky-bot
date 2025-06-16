package team.blackhole.bot.asky.config;

import com.typesafe.config.Config;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import team.blackhole.bot.asky.support.ApplicationHelper;

import java.nio.file.Path;
import java.util.List;

/**
 * Класс конфигурации вебхуков для SSL
 */
@Getter
public class AskyWebhookSSLConfiguration {

    /** Порт для приёма вебхуков */
    private final int port;

    /** Протоколы */
    private final List<String> protocols;

    /** Тип SSL: X.509, PKCS12 */
    private final String type;

    /** Путь до сертификата */
    private final Path certPath;

    /** Пароль от сертификата */
    private final String certPassword;

    /** Путь до доверительного сертификата */
    private final Path trustCertPath;

    /** Пароль от доверительного сертификата */
    private final String trustCertPassword;

    /** Путь до приватного ключа */
    private final Path keyPath;

    /** Пароль от доверительного сертификата */
    private final String keyPassword;

    /**
     * Конструктор
     * @param config свойства
     */
    public AskyWebhookSSLConfiguration(Config config) {
        port = config.getInt("port");
        type = config.getString("type");
        protocols = config.getStringList("protocols");
        certPath = getPath(config, "cert_path");
        certPassword = getPropOrNull(config, "cert_password");
        trustCertPath = getPath(config, "trust_cert_path");
        trustCertPassword = getPropOrNull(config, "trust_cert_password");
        keyPath = getPath(config, "key_path");
        keyPassword = getPropOrNull(config, "key_password");
    }

    /**
     * Возвращает значение свойства или null если оно не задано
     * @param config конфигурация
     * @param prop   свойство
     * @return значение свойства или {@code null}, если оно не задано
     */
    @Nullable
    private static String getPropOrNull(Config config, String prop) {
        return config.hasPath(prop) ? config.getString(prop) : null;
    }

    /**
     * Возвращает путь либо от домашней директории приложения, либо от абсолютного пути
     * @param config конфигурация
     * @param prop   свойство
     * @return путь или {@code null}, если свойство не задано
     */
    private static Path getPath(Config config, String prop) {
        if (!config.hasPath("")) {
            return null;
        }
        var path = config.getString(prop);
        return path.startsWith("/") ? Path.of(path) : ApplicationHelper.getHomePath().resolve(path);
    }
}
