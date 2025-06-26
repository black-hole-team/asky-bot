package team.blackhole.bot.asky.handling.command.message;

import org.jetbrains.annotations.NotNull;
import team.blackhole.bot.asky.channel.ChannelMessage;
import team.blackhole.bot.asky.db.jedis.domain.Stage;
import team.blackhole.bot.asky.handling.command.AbstractCommandHandler;
import team.blackhole.bot.asky.handling.command.CommandContext;

/**
 * Обработчик команд на основе сообщения полученного из канала
 */
public abstract class ChannelMessageCommandHandler extends AbstractCommandHandler<ChannelMessage> {

    @Override
    protected String getEntityContent(ChannelMessage entity) {
        return entity.content();
    }

    @NotNull
    @Override
    protected CommandContext getCommandContext(String command, Stage stage, ChannelMessage entity) {
        var context = super.getCommandContext(command, stage, entity);
        context.register(ChannelMessage.class, () -> entity);
        return context;
    }
}
