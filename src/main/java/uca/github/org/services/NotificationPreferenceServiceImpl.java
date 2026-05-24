package uca.github.org.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uca.github.org.models.NotificationPreference;
import uca.github.org.models.User;
import uca.github.org.repositories.NotificationPreferenceRepository;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Override
    @Transactional
    public NotificationPreference getOrCreatePreference(User user) {
        return notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> notificationPreferenceRepository.save(
                        NotificationPreference.builder()
                                .user(user)
                                .build()
                ));
    }

    @Override
    @Transactional
    public NotificationPreference updatePreference(
            User user,
            boolean applicationNotifications,
            boolean messageNotifications,
            boolean statusUpdateNotifications,
            boolean realtimeEnabled) {

        NotificationPreference preference = getOrCreatePreference(user);

        preference.setApplicationNotifications(applicationNotifications);
        preference.setMessageNotifications(messageNotifications);
        preference.setStatusUpdateNotifications(statusUpdateNotifications);
        preference.setRealtimeEnabled(realtimeEnabled);

        return notificationPreferenceRepository.save(preference);
    }
}