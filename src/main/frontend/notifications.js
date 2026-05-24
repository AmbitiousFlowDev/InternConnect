const badgeSelector = "[data-notification-badge]";
const toastContainerSelector = "[data-notification-toasts]";

function setBadgeCount(count) {
  const value = Number.isFinite(Number(count)) ? Number(count) : 0;

  document.querySelectorAll(badgeSelector).forEach((badge) => {
    badge.textContent = String(value);
    badge.classList.toggle("d-none", value === 0);
  });
}

async function refreshUnreadCount() {
  try {
    const response = await fetch("/notifications/api/unread-count", {
      headers: { Accept: "application/json" },
      credentials: "same-origin"
    });

    if (!response.ok) {
      return;
    }

    const payload = await response.json();
    setBadgeCount(payload.count);
  } catch {
    // Notification updates should not block the rest of the page.
  }
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function escapeAttribute(value) {
  return escapeHtml(value).replaceAll("`", "&#096;");
}

function showNotificationToast(notification) {
  const container = document.querySelector(toastContainerSelector);

  if (!container || !window.bootstrap?.Toast) {
    return;
  }

  const toast = document.createElement("div");
  toast.className = "toast";
  toast.setAttribute("role", "status");
  toast.setAttribute("aria-live", "polite");
  toast.setAttribute("aria-atomic", "true");
  toast.innerHTML = `
    <div class="toast-header">
      <i class="bi bi-bell-fill text-primary me-2"></i>
      <strong class="me-auto">${escapeHtml(notification.title || "Notification")}</strong>
      <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Fermer"></button>
    </div>
    <div class="toast-body">
      ${escapeHtml(notification.content || "")}
      ${notification.targetUrl ? `<div class="mt-2"><a class="btn btn-sm btn-primary" href="${escapeAttribute(notification.targetUrl)}">Ouvrir</a></div>` : ""}
    </div>
  `;

  container.appendChild(toast);

  const instance = new window.bootstrap.Toast(toast, { delay: 6000 });
  toast.addEventListener("hidden.bs.toast", () => toast.remove());
  instance.show();
}

function websocketBasePath() {
  const segments = window.location.pathname.split("/").filter(Boolean);
  return segments.length > 0 && !["home", "dashboard", "notifications", "offers", "applications", "bookmarks", "messages", "internships", "profile", "roles"].includes(segments[0])
    ? `/${segments[0]}`
    : "";
}

function connectRealtimeNotifications() {
  if (document.body.dataset.notificationsRealtime !== "true" || !window.StompJs?.Client) {
    return;
  }

  const protocol = window.location.protocol === "https:" ? "wss" : "ws";
  const brokerURL = `${protocol}://${window.location.host}${websocketBasePath()}/ws-notifications`;

  const client = new window.StompJs.Client({
    brokerURL,
    reconnectDelay: 5000,
    onConnect: () => {
      client.subscribe("/user/queue/notifications", (message) => {
        try {
          showNotificationToast(JSON.parse(message.body));
        } catch {
          // A malformed payload should still refresh the badge from the source of truth.
        } finally {
          refreshUnreadCount();
        }
      });
    }
  });

  client.activate();
}

document.addEventListener("DOMContentLoaded", () => {
  refreshUnreadCount();
  connectRealtimeNotifications();
});
