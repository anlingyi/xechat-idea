package cn.xeblog.plugin.factory;

import cn.xeblog.commons.factory.AbstractSingletonFactory;
import cn.xeblog.plugin.action.handler.command.CommandHandler;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2021/8/21 8:51 下午
 */
public class CommandHandlerFactory extends AbstractSingletonFactory<Command, CommandHandler> {

    public static final CommandHandlerFactory INSTANCE = new CommandHandlerFactory();

    private CommandHandlerFactory() {
    }

    @Override
    protected void registration(Registry<Command, CommandHandler> registry) {
        for (Command command : Command.values()) {
            registry.add(command, command.getHandlerClass());
        }
    }

}
