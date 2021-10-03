package cn.xeblog.plugin.factory;


import cn.xeblog.plugin.action.handler.command.CommandHandler;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;

/**
 * @author anlingyi
 * @date 2021/8/21 8:51 下午
 */
public class CommandHandlerFactory extends AbstractIdeaSingletonFactory<Command, CommandHandler> {

    public static final CommandHandlerFactory INSTANCE = new CommandHandlerFactory();

    private CommandHandlerFactory() {
    }

    @Override
    protected void registration(Registry<Command, CommandHandler> registry) {
        registry.addByAnnotation(DoCommand.class);
    }

}
