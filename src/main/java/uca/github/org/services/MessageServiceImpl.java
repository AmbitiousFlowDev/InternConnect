package uca.github.org.services;

import jakarta.persistence.EntityNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.Internship;
import uca.github.org.models.Message;
import uca.github.org.models.User;
import uca.github.org.repositories.InternshipRepository;
import uca.github.org.repositories.MessageRepository;
import uca.github.org.repositories.UserRepository;
import uca.github.org.models.Notification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public Message sendMessage(User sender, Long recipientId, String content, Long internshipId) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        Internship internship = null;
        if (internshipId != null) {
            internship = internshipRepository.findById(internshipId).orElse(null);
        }

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .content(content)
                .internship(internship)
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);

        notificationService.createNotification(
                recipient,
                Notification.NotificationType.MESSAGE,
                "Nouveau message",
                sender.getFirstName() + " " + sender.getLastName()
                        + " vous a envoyé un message.",
                "/messages/" + sender.getId()
        );

        return savedMessage;
    }

    @Override
    public List<Message> getConversation(User user1, Long user2Id) {
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        return messageRepository
                .findBySenderAndRecipientOrRecipientAndSenderOrderBySentAtAsc(
                        user1, user2, user1, user2);
    }

    @Override
public List<User> getConversationPartners(User user) {
    Set<User> partners = new HashSet<>();
    partners.addAll(messageRepository.findRecipientsByUserId(user.getId()));
    partners.addAll(messageRepository.findSendersByUserId(user.getId()));
    return new ArrayList<>(partners);
}

@Override
public long countUnread(User user) {
    return messageRepository.countByRecipientAndIsReadFalse(user);
}
}