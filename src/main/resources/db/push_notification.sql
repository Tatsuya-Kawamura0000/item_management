-- Push通知用テーブル
-- このファイルは spring.sql.init では実行しません（既存DBを壊さないため）。
-- Supabase の SQL Editor で手動実行してください。

-- 端末（Subscription）単位の購読情報
-- 1ユーザーが複数端末を持てるよう、user_id ではなく endpoint を一意にする
CREATE TABLE IF NOT EXISTS push_subscriptions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    endpoint TEXT NOT NULL,
    p256dh TEXT NOT NULL,
    auth TEXT NOT NULL,
    user_agent VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_push_subscriptions_endpoint UNIQUE (endpoint)
);

CREATE INDEX IF NOT EXISTS idx_push_subscriptions_user_id
    ON push_subscriptions (user_id);

-- 同一ユーザー・同一日の重複通知防止
-- status:
--   processing … 送信処理中（同時実行の排他用）
--   sent       … 1端末以上への送信に成功
--   failed     … 送信失敗（同日の再実行でリトライ可）
CREATE TABLE IF NOT EXISTS push_notification_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sent_on DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    item_count INTEGER,
    claimed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_push_notification_logs_user_sent_on UNIQUE (user_id, sent_on)
);

CREATE INDEX IF NOT EXISTS idx_push_notification_logs_sent_on
    ON push_notification_logs (sent_on);
