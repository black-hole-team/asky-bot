package team.blackhole.bot.asky.service.chat;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

/**
 * Модуль сервиса хабов
 */
public class ChatServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ChatService.class).to(ChatServiceImpl.class).in(Scopes.SINGLETON);
    }
}
