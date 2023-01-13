package ru.sshibko.LinkoBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.sshibko.LinkoBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import ru.sshibko.LinkoBot.model.entity.Message;
import ru.sshibko.LinkoBot.model.entity.User;
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


import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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

    private static final String LOG_ERROR_TEXT = "Error occurred: ";

    @Value("${success.report.switch}")
    private static byte successReport;

    private final static String INVITE_TEXT = ", hello! I’ll help you store and sort the links you need. \n" +
            " Please, register before you start sending your links with command `/register` \n" +
            " Or get help with command `/help`";

    private static final String HELP_TEXT = "This bot is designed for retrieving, storing and sorting user links to internet resources\n\n"
            + "You can execute commands from the main menu at the left angle or by typing command:\n\n"
            + "Type /start to see a welcome message\n\n"
            + "Type /help to see help information\n\n"
            + "Type /register for registration\n\n"
            + "Type /userdata to get your personal information\n\n"
            + "Type /deletedata to delete your personal information\n\n"
            + "Type /settings to set your preferences\n\n"
            + "Type /getall to get all links (not recommended)\n\n"
            + "Type /gettimed to get links sorted by received time\n\n"
            + "Type /getab to get links sorted by alphabet\n\n"
            + "Type any other words or letters to find link by entered keywords";

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (messageText.contains("http")) {
                saveMessage(chatId, messageText);
                deleteMessage(chatId, update);

                log.info("user " + messageText + " saved");
            } else {
                List<Message> messages = messageRepository.findAllChatIdMessages(chatId);
                switch (messageText) {
                    case "/start":
                        sendMessage(chatId, userRepository.findById(chatId).orElseThrow().getFirstName() + INVITE_TEXT);
                        break;
                    case "/help":
                        sendMessage(chatId, HELP_TEXT);
                        break;
                    case "/register":
                        registerUser(update.getMessage());
                        break;
                    case "/userdata":
                        viewUserData(chatId);
                        break;
                    case "/getall":
                        for (Message message : messages) {
                            getMyLinks(chatId, message.getLink(), message.getDescription());
                        }
                        break;
                    case "/gettimed":
                        messages.sort(Comparator.comparing(Message::getReceivedAt));
                        for (Message message : messages) {
                            getMyLinks(chatId, message.getLink(), message.getDescription());
                        }
                        break;
                    case "/getab":
                        messages.sort(Comparator.comparing(Message::getLink));
                        for (Message message : messages) {
                            getMyLinks(chatId, message.getLink(), message.getDescription());
                        }
                        break;
                    case "/find":
                        sendMessage(chatId, "Please, enter keyword to find");
                        List<Message> messageFinds = findMessages(update, messages);
                        if (messageFinds == null) {
                            sendMessage(chatId, "No results found");
                        } else {
                            for (Message message : messageFinds) {
                                getMyLinks(chatId, message.getLink(), message.getDescription());
                            }
                        }
                        break;
                    default:
                        //sendMessage(chatId, "Sorry, command was not recognized");
                        //For find case:
                        List<Message> messageList = findMessages(update, messages);
                        if (messageList == null) {
                            sendMessage(chatId, "No results found");
                        } else {
                            for (Message message : messageList) {
                                getMyLinks(chatId, message.getLink(), message.getDescription());
                            }
                        }
                        break;
                }
            }
        }
    }

    private void getMyLinks(Long chatId, String link, String description) {
        sendMessage(chatId, link + " - " + description);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = prepareMessage(chatId, textToSend);
        executeSendMessage(message);
    }

    private void executeSendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(LOG_ERROR_TEXT + e.getMessage());
        }
    }

    private SendMessage prepareMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        return message;
    }

    private List<BotCommand> applyCommandsList() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(new BotCommand("/start", "get a welcome message"));
        commandList.add(new BotCommand("/userdata", "get user information"));
        commandList.add(new BotCommand("/deleteuserdata", "delete user information"));
        commandList.add(new BotCommand("/help", "help info"));
        commandList.add(new BotCommand("/settings", "set your preferences"));
        commandList.add(new BotCommand("/register", "for registration"));
        commandList.add(new BotCommand("/gettimed", "for links sorted by time"));
        commandList.add(new BotCommand("/getab", "for links sorted by alphabet"));
        commandList.add(new BotCommand("/getall", "for all links BE CAREFULL!"));

        return commandList;
    }

    private void registerUser(org.telegram.telegrambots.meta.api.objects.Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            Long chatId = message.getChatId();
            Chat chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            userRepository.save(user);
            log.info("user " + user + " saved");
        }
    }

    private void viewUserData(Long chatId) {
        User user = userRepository.findById(chatId).orElse(null);
        String userData = "login: " + user.getUserName() + "\n"
                + "first name: " + user.getFirstName() + "\n"
                + "last name: " + user.getLastName() + "\n"
                + "chat Id: " + user.getChatId();
        sendMessage(chatId, userData);
    }

    private void saveMessage(Long chatId, String messageText) {
        Message messageToSave = new Message();
        try {
            String[] parts = messageText.split("http");
            messageToSave.setLink("http" + parts[1]);
            messageToSave.setDescription(parts[0]);
            messageToSave.setReceivedAt(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            User messageOwner = userRepository.findById(chatId).orElse(null);
            messageToSave.setUser(messageOwner);
            messageRepository.save(messageToSave);
            if (successReport == 1) {
                sendMessage(chatId, "your message successfully saved");
            }
        } catch (Exception e) {
            log.error(LOG_ERROR_TEXT + e.getMessage());
        }
    }

    private void deleteMessage(Long chatId, Update update) {
        Integer messageId = update.getMessage().getMessageId();
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(LOG_ERROR_TEXT + e.getMessage());
        }
    }

    private List<Message> findMessages(Update update, List<Message> messages) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            messages = messageRepository.search(messageText);
            return messages;
        }
        return null;
    }
}
