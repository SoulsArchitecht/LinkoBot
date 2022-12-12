package ru.sshibko.LinkoBot.service;

import ru.sshibko.LinkoBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import ru.sshibko.LinkoBot.model.repository.MessageRepository;
import ru.sshibko.LinkoBot.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LinkoBotService extends TelegramLongPollingBot {
    @Autowired
    private final BotConfig config;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    public LinkoBotService(BotConfig config) {
        this.config = config;
        List<BotCommand> commandList = applyCommandsList();
        try {
            this.execute(new SetMyCommands(commandList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot command list: " + e.getMessage());
        }

    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

    }

    private List<BotCommand> applyCommandsList() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "get a welcome message"));
        commandList.add(new BotCommand("/userdata", "get user information"));
        commandList.add(new BotCommand("/deleteuserdata", "delete user information"));
        commandList.add(new BotCommand("/help", "help info"));
        commandList.add(new BotCommand("/settings", "set your preferences"));
        commandList.add(new BotCommand("/register", "for registration"));

        return commandList;
    }
}
