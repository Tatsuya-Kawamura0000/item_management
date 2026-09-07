/**
 * PWA Service Worker 登録 & Web Push 通知設定スクリプト (iOS風トグルスイッチ対応)
 */

let swRegistration = null;
let cachedVapidPublicKey = null;

if ("serviceWorker" in navigator) {
    window.addEventListener("load", async () => {
        try {
            swRegistration = await navigator.serviceWorker.register("/sw.js", {
                scope: "/"
            });

            // ヘッダーにPush通知トグルが存在する場合のみ初期化を実行
            const toggleWrapper = document.getElementById("pushToggleWrapper");
            if (toggleWrapper) {
                await initPushToggleUI(swRegistration);
            }
        } catch (error) {
            console.warn("Service Worker registration failed.", error);
        }
    });
}

/**
 * VAPID公開鍵を取得（キャッシュ保持）
 * @returns {Promise<string>}
 */
async function getVapidPublicKey() {
    if (cachedVapidPublicKey) {
        return cachedVapidPublicKey;
    }

    const response = await fetch("/api/push/public-key");
    if (!response.ok) {
        throw new Error(`VAPID public key の取得に失敗しました (HTTP ${response.status})`);
    }

    const key = await response.text();
    if (!key) {
        throw new Error("VAPID public key が設定されていません。");
    }

    cachedVapidPublicKey = key;
    return cachedVapidPublicKey;
}

/**
 * Push通知トグルスイッチの初期化・状態判定
 * @param {ServiceWorkerRegistration} registration 
 */
async function initPushToggleUI(registration) {
    const toggleWrapper = document.getElementById("pushToggleWrapper");
    const toggleCheckbox = document.getElementById("pushToggleCheckbox");

    if (!toggleWrapper || !toggleCheckbox) {
        return;
    }

    // 初期化中は操作不可にしておく
    toggleCheckbox.disabled = true;

    // 1. Push API / Notification API サポート判定
    const isPushSupported = ("PushManager" in window) && ("Notification" in window);

    if (!isPushSupported) {
        // 非対応環境（iOS Safariでホーム画面未追加の場合など）
        toggleWrapper.style.display = "none";
        return;
    }

    // イベントリスナーの重複登録防止
    if (!toggleCheckbox.dataset.bound) {
        toggleCheckbox.dataset.bound = "true";
        toggleCheckbox.addEventListener("change", (event) => handleToggleChange(event, registration));
    }

    // 2. 現在の通知権限の判定
    const permission = Notification.permission;

    if (permission === "denied") {
        // 拒否されている場合：OFF表示かつ無効化、ツールチップで案内
        toggleCheckbox.checked = false;
        toggleCheckbox.disabled = true;
        toggleWrapper.title = "通知が拒否されています。端末またはブラウザの設定から通知を許可してください。";
        toggleWrapper.style.display = "flex";
        return;
    }

    // 3. ページロード時にVAPID公開鍵を事前取得（取得完了までトグルは操作不可）
    try {
        await getVapidPublicKey();
    } catch (err) {
        console.warn("VAPID public key fetch failed on load:", err);
        toggleCheckbox.checked = false;
        toggleCheckbox.disabled = true;
        toggleWrapper.title = "Push通知の設定情報を取得できませんでした。";
        toggleWrapper.style.display = "flex";
        return;
    }

    // 4. すでに通知許可されている場合のSubscription同期
    if (permission === "granted") {
        try {
            const subscription = await registration.pushManager.getSubscription();

            if (subscription) {
                // Subscriptionが存在する → バックエンドDBとの同期・保存を行い ON 表示
                await savePushSubscription(subscription);
                toggleCheckbox.checked = true;
            } else {
                // 権限はあるがSubscriptionがない → OFF 表示
                toggleCheckbox.checked = false;
            }
        } catch (error) {
            console.warn("Push subscription sync failed on load:", error);
            toggleCheckbox.checked = false;
        }
    } else {
        // permission === "default" (未登録状態)
        toggleCheckbox.checked = false;
    }

    // 公開鍵の取得が完了し、準備完了後に操作可能にする
    toggleCheckbox.disabled = false;
    toggleWrapper.style.display = "flex";
}

/**
 * トグルスイッチ切り替えハンドラ（User Activationコンテキスト）
 * @param {Event} event 
 * @param {ServiceWorkerRegistration} registration 
 */
async function handleToggleChange(event, registration) {
    const checkbox = event.target;
    const isTurningOn = checkbox.checked;

    if (isTurningOn) {
        // ==========================================
        // 【ONにする処理】通知許可・購読登録
        // ==========================================
        if (Notification.permission === "denied") {
            alert("通知がブロックされています。お使いの端末またはブラウザの設定から通知を許可してください。");
            checkbox.checked = false;
            checkbox.disabled = true;
            return;
        }

        // 事前取得済みのVAPID公開鍵の存在確認（新規fetchは発生させない）
        if (!cachedVapidPublicKey) {
            alert("Push通知の設定情報を取得できませんでした。ページを再読み込みしてください。");
            checkbox.checked = false;
            checkbox.disabled = true;
            return;
        }

        checkbox.disabled = true;

        try {
            // 1. User Activationコンテキストで即座にNotification.requestPermission()を実行
            const permission = await Notification.requestPermission();

            if (permission === "denied") {
                checkbox.checked = false;
                checkbox.disabled = true;
                const wrapper = document.getElementById("pushToggleWrapper");
                if (wrapper) {
                    wrapper.title = "通知が拒否されています。端末またはブラウザの設定から通知を許可してください。";
                }
                return;
            }

            if (permission !== "granted") {
                // キャンセルされた場合
                checkbox.checked = false;
                checkbox.disabled = false;
                return;
            }

            // 2. 事前取得済みのキャッシュ公開鍵を使用して即座にsubscribe()を実行（fetchなし）
            let subscription = await registration.pushManager.getSubscription();
            if (!subscription) {
                subscription = await registration.pushManager.subscribe({
                    userVisibleOnly: true,
                    applicationServerKey: urlBase64ToUint8Array(cachedVapidPublicKey)
                });
            }

            // 3. バックエンドDBへ保存
            await savePushSubscription(subscription);

            // 完了
            checkbox.checked = true;
            checkbox.disabled = false;

        } catch (error) {
            console.error("Push subscription failed:", error);
            checkbox.checked = false;
            checkbox.disabled = false;
            alert("Push通知の設定に失敗しました。通信環境を確認のうえ再度お試しください。");
        }

    } else {
        // ==========================================
        // 【OFFにする処理】購読解除・DB削除
        // ==========================================
        checkbox.disabled = true;

        try {
            const subscription = await registration.pushManager.getSubscription();

            if (subscription) {
                // 1. バックエンドDBから削除 (認証ユーザーのSubscription)
                await unsubscribePush(subscription.endpoint);

                // 2. ブラウザ側のPushSubscriptionを解除
                await subscription.unsubscribe();
            }

            checkbox.checked = false;
            checkbox.disabled = false;

        } catch (error) {
            console.error("Push unsubscribe failed:", error);
            // 解除失敗時もUIはOFFにしつつエラーログ
            checkbox.checked = false;
            checkbox.disabled = false;
        }
    }
}

/**
 * PushSubscription をバックエンド (/api/push/subscribe) へ保存
 * @param {PushSubscription} subscription 
 */
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

/**
 * PushSubscription をバックエンド (/api/push/unsubscribe) から削除
 * @param {string} endpoint 
 */
async function unsubscribePush(endpoint) {
    const response = await fetch("/api/push/unsubscribe", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            endpoint: endpoint
        })
    });

    if (!response.ok) {
        throw new Error(`Push subscription の解除に失敗しました。HTTP ${response.status}`);
    }
}

/**
 * Base64 URL文字列をUint8Arrayに変換するヘルパー関数
 * @param {string} base64String 
 * @returns {Uint8Array}
 */
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