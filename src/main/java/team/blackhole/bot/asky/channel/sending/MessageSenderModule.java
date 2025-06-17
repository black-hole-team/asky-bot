package team.blackhole.bot.asky.channel.sending;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Модуль для регистрации отправителя сообщений
 */
public class MessageSenderModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(MessageSender.class).to(MessageSenderImpl.class);
    }
}
