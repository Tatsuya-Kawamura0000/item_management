if ("serviceWorker" in navigator) {
    window.addEventListener("load", async () => {
        try {
            const registration = await navigator.serviceWorker.register("/sw.js", {
                scope: "/"
            });

            await registerPushSubscription(registration);
        } catch (error) {
            console.warn("Service Worker registration failed.", error);
        }
    });
}

async function registerPushSubscription(registration) {
    // Push API 非対応ブラウザ
    if (!("PushManager" in window)) {
        console.warn("Push API is not supported.");
        return;
    }

    // すでに拒否されている場合は、毎回許可を求めない
    if (Notification.permission === "denied") {
        return;
    }

    try {
        // まず既存のSubscriptionを確認
        let subscription = await registration.pushManager.getSubscription();

        // 未登録端末だけ通知許可を求める
        if (!subscription) {
            const permission = await Notification.requestPermission();

            if (permission !== "granted") {
                return;
            }

            const response = await fetch("/api/push/public-key");

            if (!response.ok) {
                throw new Error("VAPID public key の取得に失敗しました。");
            }

            const publicKey = await response.text();

            if (!publicKey) {
                throw new Error("VAPID public key が設定されていません。");
            }

            subscription = await registration.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: urlBase64ToUint8Array(publicKey)
            });
        }

        await savePushSubscription(subscription);

    } catch (error) {
        console.warn("Push subscription failed.", error);
    }
}

async function savePushSubscription(subscription) {
    const subscriptionJson = subscription.toJSON();

    const response = await fetch("/api/push/subscribe", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            endpoint: subscriptionJson.endpoint,
            p256dh: subscriptionJson.keys?.p256dh,
            auth: subscriptionJson.keys?.auth,
            userAgent: navigator.userAgent
        })
    });

    if (!response.ok) {
        throw new Error(`Push subscription の保存に失敗しました。HTTP ${response.status}`);
    }
}

function urlBase64ToUint8Array(base64String) {
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding)
        .replace(/-/g, "+")
        .replace(/_/g, "/");

    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);

    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }

    return outputArray;
}