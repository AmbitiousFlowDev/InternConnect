package uca.github.org.services;

import uca.github.org.models.NotificationPreference;
import uca.github.org.models.User;

public interface NotificationPreferenceService {

    NotificationPreference getOrCreatePreference(User user);

    NotificationPreference updatePreference(
            User user,
            boolean applicationNotifications,
            boolean messageNotifications,
            boolean statusUpdateNotifications,
            boolean realtimeEnabled
    );
}