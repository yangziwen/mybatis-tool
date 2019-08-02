package io.github.yangziwen.mybatistool;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import io.github.yangziwen.mybatistool.command.Command;
import io.github.yangziwen.mybatistool.command.GenerateCommand;

public class Main {

    public static void main(String[] args) {

        JCommander commander = new JCommander();

        Command[] commands = { new GenerateCommand() };

        for (Command command : commands) {
            commander.addCommand(command.name(), command);
        }

        if (args == null || args.length == 0) {
            commander.usage();
            return;
        }

        String commandName = parseArgs(commander, args).getParsedCommand();

        for (Command command : commands) {
            if (commandName.equals(command.name())) {
                command.invoke(commander);
                return;
            }
        }
        System.err.println("Invalid command!");

    }

    private static JCommander parseArgs(JCommander commander, String[] args) {
        try {
            commander.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return commander;
    }

}
