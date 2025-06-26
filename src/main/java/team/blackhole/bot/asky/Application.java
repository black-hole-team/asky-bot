/*
 * Copyright (c) 2025 Plekhanov Aleksey
 * SPDX-License-Identifier: PolyForm-Noncommercial-1.0.0
 */
package team.blackhole.bot.asky;

import com.google.inject.*;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import lombok.extern.log4j.Log4j2;
import team.blackhole.bot.asky.channel.ChannelModule;
import team.blackhole.bot.asky.channel.ChannelPool;
import team.blackhole.bot.asky.channel.webhook.WebhookServer;
import team.blackhole.bot.asky.config.AskyChannelsConfiguration;
import team.blackhole.bot.asky.config.AskyConfigurationModule;
import team.blackhole.bot.asky.db.DatabaseModule;
import team.blackhole.bot.asky.db.jedis.JedisModule;
import team.blackhole.bot.asky.events.EventsModule;
import team.blackhole.bot.asky.handling.HandlersModule;
import team.blackhole.bot.asky.providers.ProvidersModule;
import team.blackhole.bot.asky.queue.QueueModule;
import team.blackhole.bot.asky.scheduling.SchedulingModule;
import team.blackhole.bot.asky.scheduling.SchedulingService;
import team.blackhole.bot.asky.service.ServiceModule;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Класс точки входа в приложение
 */
@Log4j2
public class Application {

    /**
     * Точка входа в приложение
     * @param args аргументы запуска
     */
    public static void main(String[] args) {
        var injector = Guice.createInjector(Stage.PRODUCTION, new AskyConfigurationModule(), new ProvidersModule(), new JedisModule(),
                new DatabaseModule(), new QueueModule(), new ChannelModule(), new ServiceModule(), new EventsModule(), new HandlersModule(),
                new SchedulingModule());

        // Регистрируем хуки завершения работы приложения
        registerShutdownHook(injector);

        // Запускаем обработку ботов
        log.info("Запуск пула ботов");
        injector.getInstance(ChannelPool.class).start();

        // Запуск задач выполняемых по расписанию
        log.info("Запуск задач выполняемых по расписанию");
        injector.getInstance(SchedulingService.class).run();

        // Запускаем сервер для обработки вебхуков (если это необходимо)
        if (injector.getInstance(AskyChannelsConfiguration.class).hasWebhookChannel()) {
            log.info("Запуск webhook сервера");
            injector.getInstance(WebhookServer.class).start();
        }
    }

    /**
     * Регистрирует хук завершения работы jwm
     * @param injector инжектор
     */
    private static void registerShutdownHook(Injector injector) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeAll(injector)));
    }

    /**
     * Закрывает все закрываемые singletons в инжекторе
     * @param injector инжектор
     */
    private static void closeAll(Injector injector) {
        doWithSingletons(injector, instance -> {
            if (instance instanceof AutoCloseable ac) {
                try {
                    ac.close();
                } catch (Exception e) {
                    log.error("Ошибка при закрытии объекта", e);
                }
            }
        });
    }

    /**
     * Выполнить действие со всеми singleton элементами в инжекторе
     * @param injector инжектор
     * @param action  действие
     */
    private static void doWithSingletons(Injector injector, Consumer<Object> action) {
        for(var entry : injector.getAllBindings().entrySet()) {
            entry.getValue().acceptScopingVisitor(new ActionDefaultBindingScopingVisitor(entry, action));
        }
    }

    /**
     * Визитор
     */
    private static class ActionDefaultBindingScopingVisitor extends DefaultBindingScopingVisitor<Void> {

        /** Биндинг */
        private final Map.Entry<Key<?>, Binding<?>> entry;

        /** Действие выполняемое с синглтоном */
        private final Consumer<Object> action;

        public ActionDefaultBindingScopingVisitor(Map.Entry<Key<?>, Binding<?>> entry, Consumer<Object> action) {
            this.entry = entry;
            this.action = action;
        }

        @Override public Void visitEagerSingleton() {
            var instance = entry.getValue().getProvider().get();
            try {
                action.accept(instance);
            } catch (Exception e) {
                log.error("Ошибка при обработке {}", entry.getKey().getTypeLiteral(), e);
            }
            return null;
        }
    }
}
