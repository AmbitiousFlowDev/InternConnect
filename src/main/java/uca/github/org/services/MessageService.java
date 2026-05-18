package uca.github.org.services;

import uca.github.org.models.Message;
import uca.github.org.models.User;

import java.util.List;

public interface MessageService {
    Message sendMessage(User sender, Long recipientId, String content, Long internshipId);
    List<Message> getConversation(User user1, Long user2Id);
    List<User> getConversationPartners(User user);
    long countUnread(User user);
}