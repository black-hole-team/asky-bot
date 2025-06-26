package team.blackhole.bot.asky.handling.command.callback;

import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelCallback;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.command.AbstractCommandHandler;
import team.blackhole.bot.asky.handling.command.CommandContext;

/**
 * Обработчик команд на основе данных обратного вызова
 */
public abstract class ChannelCallbackCommandHandler extends AbstractCommandHandler<ChannelCallback> {

    @Override
    protected String getEntityContent(ChannelCallback entity) {
        return entity.payload();
    }

    @NotNull
    @Override
    protected CommandContext getCommandContext(String command, Stage stage, ChannelCallback entity) {
        var context = super.getCommandContext(command, stage, entity);
        context.register(ChannelCallback.class, () -> entity);
        return context;
    }
}
